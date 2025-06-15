# Android Barcode Scanner

A modern Android application that uses the device camera to scan and decode various types of barcodes including QR codes, UPC, EAN, Code 128, and other common barcode formats.

## Features

- **Real-time scanning**: Continuous barcode detection using the camera preview
- **Multiple barcode formats**: Supports QR codes, UPC-A, UPC-E, EAN-8, EAN-13, Code 39, Code 93, Code 128, Codabar, ITF, Data Matrix, PDF417, and Aztec codes
- **Modern UI**: Material Design components with a clean, intuitive interface
- **Camera permissions**: Proper permission handling and user-friendly prompts
- **Visual feedback**: Scanning overlay to guide barcode positioning
- **Result display**: Shows the scanned barcode value and format type

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Android Views with ViewBinding
- **Camera**: AndroidX CameraX
- **Barcode Scanning**: Google ML Kit Barcode Scanning
- **Design**: Material Components
- **Minimum SDK**: Android 24 (Android 7.0)
- **Target SDK**: Android 34

## Project Structure

```text
app/
├── src/main/
│   ├── java/com/example/barcodescanner/
│   │   └── MainActivity.kt                 # Main activity with camera and scanning logic
│   ├── res/
│   │   ├── drawable/                       # UI drawables and backgrounds
│   │   ├── layout/
│   │   │   └── activity_main.xml          # Main activity layout
│   │   ├── values/                        # Colors, strings, themes
│   │   └── xml/                           # Backup and data extraction rules
│   └── AndroidManifest.xml               # App manifest with permissions
└── build.gradle                          # App-level build configuration
```

## Dependencies

The app uses the following key dependencies:

- **AndroidX CameraX**: For camera preview and image capture
- **Google ML Kit Barcode Scanning**: For barcode detection and decoding
- **Material Components**: For modern UI components
- **AndroidX Activity**: For modern activity handling

## Setup and Installation

1. **Clone the repository**:

   ```bash
   git clone <repository-url>
   cd BarcodeScanner
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project folder and select it

3. **Build the project**:
   - Android Studio will automatically download dependencies
   - Build the project using Build → Make Project

4. **Run the app**:
   - Connect an Android device or use an emulator   - Click the Run button or use Shift+F10

## Usage

1. **Grant Camera Permission**: When first launching the app, grant camera permission when prompted
2. **Point Camera**: Aim the camera at a barcode or QR code
3. **Automatic Detection**: The app will automatically detect and decode barcodes in real-time
4. **View Results**: The decoded value and barcode type will be displayed at the bottom of the screen
5. **Clear Results**: Use the "Clear" button to reset the result display

## Permissions

The app requires the following permission:

- **Camera**: Required for capturing camera preview and scanning barcodes

## Supported Barcode Formats

- QR Code
- UPC-A
- UPC-E
- EAN-8
- EAN-13
- Code 39
- Code 93
- Code 128
- Codabar
- ITF (Interleaved 2 of 5)
- Data Matrix
- PDF417
- Aztec

## Building from Source

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.9+

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is open source and available under the [MIT License](LICENSE).

## Troubleshooting

### Camera Permission Issues

- Ensure camera permission is granted in device settings
- Restart the app after granting permission

### Camera Not Working

- Check if another app is using the camera
- Restart the device if camera access is blocked

### Barcode Not Detected

- Ensure good lighting conditions
- Hold the device steady
- Make sure the barcode is clearly visible and not damaged
- Try different distances from the barcode

## Support

For issues and questions, please create an issue in the repository or contact the development team.
