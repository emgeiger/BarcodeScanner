# 📱 Barcode Scanner Sensitivity Control Guide

## 🎯 How to Adjust Scanning Sensitivity

Your barcode scanner now has advanced sensitivity controls to optimize scanning for different conditions and requirements.

### 📍 **How to Access Sensitivity Settings:**

1. **Long-press the "Clear" button** in the app
2. Choose from 5 different sensitivity modes
3. Settings apply immediately and persist for your session

---

## ⚙️ **Available Sensitivity Modes:**

### 🎯 **Maximum Sensitivity**

- **Best for:** Damaged, small, or poor-quality barcodes
- **Performance:** Highest accuracy, slowest processing
- **Settings:**
  - Cooldown: 100ms (very responsive)
  - Resolution: 1920x1080 (highest quality)
  - Exposure: Darker for better contrast
  - Use case: Medical/pharmaceutical scanning, inventory with worn labels

### ⚡ **High Sensitivity**

- **Best for:** Standard scanning with maximum accuracy
- **Performance:** High accuracy, moderate speed
- **Settings:**
  - Cooldown: 300ms
  - Resolution: 1920x1080 (high quality)
  - Exposure: Slightly darker for contrast
  - Use case: Retail checkout, library systems

### ⚖️ **Normal Sensitivity (Default)**

- **Best for:** General-purpose scanning
- **Performance:** Balanced accuracy and speed
- **Settings:**
  - Cooldown: 1500ms
  - Resolution: 1280x720 (standard)
  - Exposure: Automatic
  - Use case: Most common scanning scenarios

### 🚀 **Fast Scanning**

- **Best for:** High-volume scanning with good barcodes
- **Performance:** Good speed, good accuracy
- **Settings:**
  - Cooldown: 2500ms
  - Resolution: 1280x720 (standard)
  - Exposure: Slightly brighter
  - Use case: Warehouse operations, event check-ins

### ⏰ **Ultra Fast**

- **Best for:** Maximum speed when accuracy is less critical
- **Performance:** Maximum speed, basic accuracy
- **Settings:**
  - Cooldown: 3500ms
  - Resolution: 960x540 (lower for speed)
  - Exposure: Brighter for quick processing
  - Use case: Mass scanning, time-sensitive operations

---

## 🛠️ **Technical Details:**

### **What Each Setting Controls:**

1. **Cooldown Time:** How long to wait between scanning the same barcode
   - Lower = More responsive to repeated scans
   - Higher = Less processing overhead

2. **Camera Resolution:** Image quality for barcode detection
   - Higher = Better detection of small/damaged codes
   - Lower = Faster processing

3. **Processing Strategy:** How the camera handles multiple frames
   - `KEEP_ONLY_LATEST`: Best for accuracy
   - `BLOCK_PRODUCER`: Better for speed

4. **Exposure Compensation:** Camera brightness adjustment
   - Negative values: Darker images, better contrast
   - Positive values: Brighter images, faster processing

---

## 💡 **Tips for Optimal Scanning:**

### **For Best Results:**

- Use **Maximum** or **High Sensitivity** for:
  - Damaged or worn barcodes
  - Very small barcodes
  - Poor lighting conditions
  - Critical accuracy requirements

- Use **Fast** or **Ultra Fast** for:
  - High-volume scanning
  - Good quality barcodes
  - Time-sensitive operations
  - Batch processing

### **Environmental Factors:**

- **Good lighting** improves all modes
- **Steady hands** help with high-resolution modes
- **Clean camera lens** is essential for accuracy
- **Proper distance** (6-12 inches typically optimal)

---

## 🔧 **Future Enhancements:**

- Custom sensitivity with manual parameter adjustment
- Automatic mode switching based on scanning success rate
- Per-barcode-type sensitivity profiles
- Performance analytics and recommendations

---

## 📊 **Performance Comparison:**

| Mode | Accuracy | Speed | Battery | Best Use Case |
|------|----------|-------|---------|---------------|
| Maximum | ★★★★★ | ★★☆☆☆ | ★★☆☆☆ | Critical accuracy |
| High | ★★★★☆ | ★★★☆☆ | ★★★☆☆ | Quality scanning |
| Normal | ★★★☆☆ | ★★★★☆ | ★★★★☆ | General use |
| Fast | ★★★☆☆ | ★★★★★ | ★★★★☆ | High volume |
| Ultra Fast | ★★☆☆☆ | ★★★★★ | ★★★★★ | Speed priority |

---

**💡 Pro Tip:** The app remembers your last sensitivity setting, so set it once for your typical use case and adjust as needed for special situations.
