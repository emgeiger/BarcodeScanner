<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilot-instructions.md-file -->

# Android Barcode Scanner Project Instructions

This is a production-ready Android application using Kotlin that scans and decodes barcodes via device camera. The app demonstrates modern Android development practices with CameraX, ML Kit, and comprehensive testing.

## Build Configuration ⚠️ CRITICAL

### Version Compatibility Requirements - UPDATED for Gradle 9.0
- **Java Version**: Must use Java 17+ (JDK 17+) for compilation
- **Android Gradle Plugin**: 8.7.0 (compatible with Gradle 9.0)
- **Gradle Wrapper**: 9.0 (latest with modern features)
- **Kotlin**: 2.0.20 (latest stable with full Gradle 9.0 support)
- **Target SDK**: 35, Min SDK: 24

### Project Migration Status
- ✅ **Gradle 9.0 Ready**: All deprecated features removed
- ✅ **Modern Build Scripts**: Using plugins DSL only
- ✅ **Configuration Cache**: Enabled for faster builds
- ✅ **Clean Project Structure**: Old build files removed

### Build Commands
```bash
# Clean build with modern Gradle 9.0 features
./gradlew clean assembleDebug --configuration-cache

# Release build
./gradlew assembleRelease --configuration-cache

# Run tests with performance optimizations
./gradlew test --configuration-cache --parallel

# Debug APK build
./gradlew assembleDebug --configuration-cache

# Release APK build  
./gradlew assembleRelease --configuration-cache

# Run tests
./gradlew test --configuration-cache
./gradlew connectedAndroidTest --configuration-cache

# Windows batch scripts
.\build.bat debug      # Build debug APK
.\build.bat release    # Build release APK
.\build_gradle9.bat    # Full build with tests
```

## Project Architecture

### Core Components
- **MainActivity.kt**: Main activity with camera lifecycle, permissions, barcode processing
- **BarcodeHistoryRepository.kt**: Data persistence using SharedPreferences and Gson serialization
- **activity_main.xml**: Material Design layout with camera preview, result display, controls
- **BarcodeAnalyzer**: Inner class implementing ImageAnalysis.Analyzer for real-time detection

### Package Structure
```
com.encana.barcodescanner/
├── MainActivity.kt                    # Main activity with camera and scanning logic
├── data/
│   └── BarcodeHistoryRepository.kt   # Data layer for barcode history management
└── analyzer/                         # (Tested but not yet extracted to separate file)
    └── BarcodeAnalyzer               # Image analysis for barcode detection
```

## Technology Stack

### Core Dependencies
- **CameraX** (1.3.1): Modern camera framework
  - camera-core, camera-camera2, camera-lifecycle
  - camera-view for PreviewView integration
- **ML Kit Barcode Scanning** (17.2.0): Google's barcode detection library
- **Material Components** (1.11.0): UI components following Material Design
- **Gson** (2.10.1): JSON serialization for barcode history persistence

### Testing Framework
- **JUnit 4**: Unit testing framework
- **Mockito**: Mocking for unit tests
- **Robolectric**: Android unit tests without emulator
- **Espresso**: UI testing for instrumented tests
- **AndroidX Test**: Testing utilities and runners

## Key Implementation Patterns

### Camera Initialization Flow
1. Check/request CAMERA permission using ActivityResultLauncher
2. Get ProcessCameraProvider instance
3. Build Preview, ImageCapture, and ImageAnalysis use cases
4. Bind to lifecycle with CameraSelector.DEFAULT_BACK_CAMERA
5. Set up BarcodeAnalyzer with ML Kit BarcodeScanning client

### Barcode Processing Pattern
```kotlin
// In BarcodeAnalyzer.analyze()
val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
scanner.process(image)
    .addOnSuccessListener { barcodes -> /* Process results */ }
    .addOnFailureListener { /* Handle errors */ }
    .addOnCompleteListener { imageProxy.close() }
```

### Supported Barcode Formats
- **1D**: CODE_128, CODE_39, CODE_93, CODABAR, EAN_13, EAN_8, UPC_A, UPC_E, ITF
- **2D**: QR_CODE, DATA_MATRIX, PDF417, AZTEC

## Data Management

### Barcode History Features
- **Automatic Save**: Every successful scan saves to history
- **Deduplication**: Rescanning same barcode moves it to top of history
- **Size Limit**: Maximum 100 items (configurable via MAX_HISTORY_ITEMS)
- **Persistence**: Uses SharedPreferences with Gson serialization
- **Data Structure**: BarcodeHistoryItem(value, type, timestamp, id)

### Repository Pattern Implementation
```kotlin
class BarcodeHistoryRepository(context: Context) {
    fun saveBarcodeToHistory(value: String, type: String)  // Save with deduplication
    fun getHistory(): List<BarcodeHistoryItem>             // Retrieve all items
    fun deleteHistoryItem(id: String)                      // Delete specific item
    fun clearHistory()                                     // Clear all items
}
```

## UI/UX Design Principles

### Layout Components (activity_main.xml)
- **PreviewView**: CameraX surface for camera feed (id: viewFinder)
- **ResultText**: Displays scanned barcode value (id: result_text)
- **TypeText**: Shows barcode format type (id: barcode_type_text)
- **ScanButton**: Shows recent scan history (id: scan_button)
- **ClearButton**: Resets display text (id: clear_button)
- **ScanningOverlay**: Visual guide for barcode positioning (id: scanning_overlay)

### Material Design Features
- Portrait orientation lock for consistent UX
- Material button styling with custom backgrounds
- Constraint layout for responsive design
- Proper accessibility content descriptions

## Development Guidelines

### Error Handling Patterns
- Use try-catch blocks for camera operations
- Log errors with appropriate levels (Log.d, Log.e)
- Show user-friendly Toast messages for failures
- Handle null values from barcode.rawValue

### Permission Management
- Request CAMERA permission before accessing camera
- Use ActivityResultLauncher for modern permission requests
- Handle permission denial gracefully with user feedback
- Check permissions with ContextCompat.checkSelfPermission

### Testing Strategy
- **Unit Tests**: Business logic, data repository, barcode format mapping
- **Integration Tests**: Repository with real Android context
- **UI Tests**: User interactions, camera permissions, component visibility
- **Mock Usage**: Isolate dependencies for faster, deterministic tests

### Code Quality Standards
- Follow Kotlin coding conventions
- Use ViewBinding for type-safe view access
- Implement proper lifecycle management (onDestroy)
- Use companion objects for constants and static data
- Add KDoc comments for public APIs

## Common Development Tasks

### Adding New Barcode Format Support
1. Add format constant to getBarcodeType() method
2. Update test cases in BarcodeAnalyzerLogicTest
3. Add format to supported formats documentation
4. Test with physical barcode samples

### Extending History Features
1. Modify BarcodeHistoryItem data class
2. Update Gson serialization/deserialization
3. Add repository methods for new operations
4. Write tests for new functionality
5. Update UI to display new data

### Camera Configuration Changes
1. Modify ImageAnalysis.Builder() settings
2. Test on different device orientations
3. Handle different camera resolutions
4. Update preview aspect ratios if needed

## Testing Commands

### Local Testing
```bash
# Unit tests (fast, no device needed)
./gradlew test --tests com.encana.barcodescanner.data.BarcodeHistoryRepositoryTest

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest --tests com.encana.barcodescanner.MainActivityInstrumentedTest

# Specific test methods
./gradlew test --tests "*.saveBarcodeToHistory*"
```

### Test Coverage Areas
- ✅ Barcode format detection and mapping
- ✅ History persistence and retrieval
- ✅ UI component interactions
- ✅ Permission handling
- ✅ Camera lifecycle management
- ✅ Data serialization/deserialization

### Troubleshooting Guide

### Build Issues
- **Gradle Version Mismatch**: Project now uses Gradle 9.0 with configuration cache enabled
- **Dependency Conflicts**: Use `./gradlew dependencies` to analyze conflicts
- **Configuration Cache Issues**: Add `--no-configuration-cache` flag if needed during development

### Runtime Issues
- **Camera Access**: Verify permissions in device settings, restart app if needed
- **Scanning Problems**: Check lighting conditions, barcode quality, camera focus
- **Memory Issues**: Monitor camera executor shutdown in onDestroy()

### Testing Issues
- **Mock Failures**: Ensure @Mock annotations and MockitoJUnitRunner are correct
- **UI Test Flakes**: Add proper wait conditions, verify device orientation
- **Permission Tests**: Use GrantPermissionRule for consistent test environment

## Code Maintenance Notes

### Performance Considerations
- Camera executor uses single background thread
- ML Kit processing is asynchronous
- UI updates use runOnUiThread() for thread safety
- ImageProxy.close() called in onCompleteListener to prevent memory leaks

### Security & Privacy
- Camera permission requested explicitly with user consent
- No network requests or data transmission
- Local data storage only (SharedPreferences)
- No sensitive information logged

### Accessibility
- Content descriptions on interactive elements
- Large touch targets for buttons
- High contrast visual elements
- Support for screen readers
