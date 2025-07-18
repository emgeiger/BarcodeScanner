package com.encana.barcodescanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.encana.barcodescanner.data.AssetTagRepository
import com.encana.barcodescanner.data.BarcodeHistoryRepository
import com.encana.barcodescanner.data.SupabaseRepository
import com.encana.barcodescanner.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeHistoryRepository: BarcodeHistoryRepository
    private lateinit var supabaseRepository: SupabaseRepository
    private lateinit var assetTagRepository: AssetTagRepository
    
    // Scanning control variables
    private var isScanning = false
    private var lastScanTime = 0L
    private var scanCooldownMs = 1000L
    private val scanTimeoutMs = 30000L // 30 seconds
    private var scanTimeoutHandler: Handler? = null
    private var scanTimeoutRunnable: Runnable? = null

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).toTypedArray()
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                permissionGranted = false
        }
        if (!permissionGranted) {
            Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT).show()
        } else {
            startCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize repositories
        barcodeHistoryRepository = BarcodeHistoryRepository(this)
        supabaseRepository = SupabaseRepository()
        assetTagRepository = AssetTagRepository()

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Set up scan button click listener
        binding.scanButton.setOnClickListener {
            if (!isScanning) {
                startScanning()
            }
        }

        // Set up clear button click listener
        binding.clearButton.setOnClickListener {
            clearHistory()
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Test Supabase connection
        testSupabaseConnection()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_maximum_sensitivity -> {
                scanCooldownMs = 3000
                Toast.makeText(this, "Maximum sensitivity enabled", Toast.LENGTH_SHORT).show()
                updateMenuSelection(item)
                true
            }
            R.id.menu_high_sensitivity -> {
                scanCooldownMs = 2000
                Toast.makeText(this, "High sensitivity enabled", Toast.LENGTH_SHORT).show()
                updateMenuSelection(item)
                true
            }
            R.id.menu_normal_sensitivity -> {
                scanCooldownMs = 1000
                Toast.makeText(this, "Normal sensitivity enabled", Toast.LENGTH_SHORT).show()
                updateMenuSelection(item)
                true
            }
            R.id.menu_fast_scanning -> {
                scanCooldownMs = 500
                Toast.makeText(this, "Fast scanning enabled", Toast.LENGTH_SHORT).show()
                updateMenuSelection(item)
                true
            }
            R.id.menu_ultra_fast -> {
                scanCooldownMs = 200
                Toast.makeText(this, "Ultra fast scanning enabled", Toast.LENGTH_SHORT).show()
                updateMenuSelection(item)
                true
            }
            R.id.menu_custom_sensitivity -> {
                showCustomSensitivityDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateMenuSelection(selectedItem: MenuItem) {
        selectedItem.isChecked = true
        // Uncheck other items in the submenu
        val menu = selectedItem.subMenu
        if (menu != null) {
            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                if (item != selectedItem) {
                    item.isChecked = false
                }
            }
        }
    }

    private fun showCustomSensitivityDialog() {
        val customView = layoutInflater.inflate(R.layout.dialog_custom_sensitivity, null)
        val cooldownEditText = customView.findViewById<android.widget.EditText>(R.id.cooldownEditText)
        val sensitivitySeekBar = customView.findViewById<SeekBar>(R.id.sensitivitySeekBar)
        val sensitivityLabel = customView.findViewById<TextView>(R.id.sensitivityLabel)
        
        // Set current values
        cooldownEditText.setText(scanCooldownMs.toString())
        sensitivitySeekBar.progress = 50 // Default to medium
        
        // Update label as user adjusts sensitivity
        sensitivitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val sensitivity = when {
                    progress < 20 -> "Very Low"
                    progress < 40 -> "Low"
                    progress < 60 -> "Medium"
                    progress < 80 -> "High"
                    else -> "Very High"
                }
                sensitivityLabel.text = "$sensitivity ($progress%)"
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        AlertDialog.Builder(this)
            .setTitle("Custom Sensitivity Settings")
            .setView(customView)
            .setPositiveButton("Apply") { _, _ ->
                val cooldown = cooldownEditText.text.toString().toLongOrNull()
                if (cooldown != null && cooldown >= 100 && cooldown <= 5000) {
                    scanCooldownMs = cooldown
                    Toast.makeText(this, "Custom sensitivity applied: ${cooldown}ms cooldown", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Invalid cooldown value. Please enter 100-5000 milliseconds.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startScanning() {
        if (isScanning) return
        
        isScanning = true
        binding.scanButton.text = "📷 Scanning..."
        binding.resultTextView.text = "Point camera at barcode"
        
        // Set up timeout mechanism
        scanTimeoutHandler = Handler(Looper.getMainLooper())
        scanTimeoutRunnable = Runnable {
            if (isScanning) {
                stopScanning()
                Toast.makeText(this, "⏱️ Scan timeout - try again", Toast.LENGTH_SHORT).show()
            }
        }
        scanTimeoutHandler?.postDelayed(scanTimeoutRunnable!!, scanTimeoutMs)
        
        Toast.makeText(this, "📷 Scanning - point camera at barcode", Toast.LENGTH_SHORT).show()
    }

    private fun stopScanning() {
        isScanning = false
        binding.scanButton.text = "📷 Scan"
        
        // Clear timeout handler
        scanTimeoutHandler?.removeCallbacks(scanTimeoutRunnable!!)
        scanTimeoutHandler = null
        scanTimeoutRunnable = null
    }

    private fun clearHistory() {
        barcodeHistoryRepository.clearHistory()
        binding.historyTextView.text = "History cleared"
        binding.resultTextView.text = "Ready to scan"
        Log.d(TAG, "History cleared")
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            // Image capture
            imageCapture = ImageCapture.Builder().build()

            // Image analyzer
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcodes ->
                        runOnUiThread {
                            processBarcodes(barcodes)
                        }
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processBarcodes(barcodes: List<Barcode>) {
        if (!isScanning) return
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScanTime < scanCooldownMs) return
        
        for (barcode in barcodes) {
            val barcodeValue = barcode.rawValue ?: continue
            val barcodeType = getBarcodeType(barcode.format)
            
            lastScanTime = currentTime
            
            // Stop scanning immediately after detection
            stopScanning()
            
            // Display results
            binding.resultTextView.text = "✅ $barcodeType: $barcodeValue"
            
            // Add to history
            barcodeHistoryRepository.addBarcode(barcodeValue, barcodeType)
            updateHistoryDisplay()
            
            // Save to Supabase
            saveBarcodeData(barcodeValue, barcodeType)
            
            Toast.makeText(this, "✅ $barcodeType barcode saved", Toast.LENGTH_SHORT).show()
            
            Log.d(TAG, "Barcode detected: $barcodeValue (Type: $barcodeType)")
            return // Process only first barcode found
        }
    }

    private fun getBarcodeType(format: Int): String {
        return when (format) {
            Barcode.FORMAT_CODE_128 -> "Code 128"
            Barcode.FORMAT_CODE_39 -> "Code 39"
            Barcode.FORMAT_CODE_93 -> "Code 93"
            Barcode.FORMAT_CODABAR -> "Codabar"
            Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
            Barcode.FORMAT_EAN_13 -> "EAN-13"
            Barcode.FORMAT_EAN_8 -> "EAN-8"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_UPC_A -> "UPC-A"
            Barcode.FORMAT_UPC_E -> "UPC-E"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_AZTEC -> "Aztec"
            else -> "Unknown"
        }
    }

    private fun updateHistoryDisplay() {
        val history = barcodeHistoryRepository.getHistory()
        binding.historyTextView.text = history.joinToString("\n") { 
            "${it.type}: ${it.value}" 
        }
    }

    private fun testSupabaseConnection() {
        CoroutineScope(Dispatchers.Main).launch {
            val barcodeResult = supabaseRepository.getAllBarcodeHistory()
            val assetTagResult = assetTagRepository.getAllAssetTags()
            
            barcodeResult.fold(
                onSuccess = { barcodes ->
                    Log.d(TAG, "✅ Supabase connection successful: ${barcodes.size} barcode records")
                },
                onFailure = { error ->
                    Log.e(TAG, "❌ Supabase connection failed: ${error.message}")
                }
            )
            
            assetTagResult.fold(
                onSuccess = { assetTags ->
                    Log.d(TAG, "✅ Asset tag connection successful: ${assetTags.size} asset tag records")
                },
                onFailure = { error ->
                    Log.e(TAG, "❌ Asset tag connection failed: ${error.message}")
                }
            )
        }
    }

    private fun saveBarcodeData(barcodeValue: String, barcodeType: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val historyResult = supabaseRepository.insertBarcodeHistory(barcodeValue, barcodeType)
                val assetTagResult = assetTagRepository.insertAssetTag(barcodeValue, barcodeType)
                
                historyResult.fold(
                    onSuccess = { history ->
                        Log.d(TAG, "✅ Barcode saved to history: ${history.id}")
                    },
                    onFailure = { error ->
                        Log.w(TAG, "⚠️ Failed to save barcode: ${error.message}")
                    }
                )
                
                assetTagResult.fold(
                    onSuccess = { assetTag ->
                        Log.d(TAG, "✅ Asset tag saved: ${assetTag.id}")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Asset tag saved successfully", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { error ->
                        Log.w(TAG, "⚠️ Asset tag save failed: ${error.message}")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Warning: Asset tag not saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error saving barcode data: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private inner class BarcodeAnalyzer(private val barcodeListener: (barcodes: List<Barcode>) -> Unit) :
        ImageAnalysis.Analyzer {

        private val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_CODABAR,
                Barcode.FORMAT_DATA_MATRIX,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_ITF,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_PDF417
            )
            .build()

        private val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = androidx.camera.core.ImageProxy.create(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodeListener(barcodes)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Barcode analysis failed", exception)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
    }
}
