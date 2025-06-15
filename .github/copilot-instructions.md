<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Android Barcode Scanner Project Instructions

This is an Android application that uses the device camera to scan and decode various types of barcodes including QR codes, UPC, EAN, Code 128, and other common barcode formats.

## Project Structure

- Uses Kotlin as the primary programming language
- Implements CameraX for camera functionality
- Uses Google ML Kit for barcode scanning
- Follows Material Design principles
- Targets Android API 24+ (Android 7.0)

## Key Components

- **MainActivity.kt**: Main activity that handles camera preview and barcode scanning
- **activity_main.xml**: Layout file with camera preview and result display
- **Scanner overlay**: Visual guide for barcode positioning
- **Real-time scanning**: Continuous barcode detection in camera preview

## Dependencies

- AndroidX CameraX libraries for camera functionality
- Google ML Kit Barcode Scanning for barcode detection
- Material Components for UI elements
- Kotlin coroutines for async operations

## Development Guidelines

- Always request camera permissions before accessing camera
- Handle camera lifecycle properly with CameraX
- Implement proper error handling for camera and scanning operations
- Follow Android development best practices
- Ensure UI is responsive and accessible
- Test on different device orientations and screen sizes

## Features Implemented

- Real-time barcode scanning
- Support for multiple barcode formats
- Camera permission handling
- Modern Material Design UI
- Portrait orientation lock
- Visual scanning overlay
