# Contributing to Barcode Scanner

Thank you for your interest in contributing to the Barcode Scanner project! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK API 24 or higher
- Git

### Setting Up Development Environment

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-username/BarcodeScanner.git
   cd BarcodeScanner
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository

3. **Build the project**

   ```bash
   ./gradlew build
   ```

## 📋 How to Contribute

### Reporting Bugs

- Use the [Bug Report template](.github/ISSUE_TEMPLATE/bug_report.md)
- Include device information and Android version
- Provide steps to reproduce the issue
- Add screenshots if applicable

### Suggesting Features

- Use the [Feature Request template](.github/ISSUE_TEMPLATE/feature_request.md)
- Explain the use case and benefits
- Consider existing alternatives

### Code Contributions

#### Branch Naming Convention

- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `hotfix/description` - Critical fixes
- `docs/description` - Documentation updates

#### Pull Request Process

1. **Fork the repository**
2. **Create a feature branch**

   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow the coding standards below
   - Add tests for new functionality
   - Update documentation if needed

4. **Test your changes**

   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

5. **Commit your changes**

   ```bash
   git add .
   git commit -m "feat: add barcode history feature"
   ```

6. **Push to your fork**

   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Use the [PR template](.github/PULL_REQUEST_TEMPLATE.md)
   - Link related issues
   - Add screenshots for UI changes

## 📖 Coding Standards

### Kotlin Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

### Android Specific Guidelines

- Follow [Android development best practices](https://developer.android.com/develop/quality-guidelines)
- Use ViewBinding for UI components
- Handle permissions properly
- Implement proper lifecycle management

### Testing

- Write unit tests for business logic
- Add UI tests for critical user flows
- Ensure tests are deterministic and fast
- Mock external dependencies

## 🔄 CI/CD Process

### Automated Checks

Every PR triggers:

- Unit tests
- UI tests
- Lint checks
- Security scans
- Build verification

### Release Process

1. Merge to `main` branch
2. Create a version tag (e.g., `v1.2.0`)
3. Automated release build
4. APK artifacts uploaded to GitHub Releases

## 📱 Testing Guidelines

### Manual Testing Checklist

- [ ] Camera permission request works
- [ ] Barcode scanning works with different formats
- [ ] App handles device rotation
- [ ] No crashes on different Android versions
- [ ] UI is accessible and responsive

### Device Testing

Test on:

- Different screen sizes (phone, tablet)
- Various Android versions (API 24+)
- Different camera configurations
- Low-light conditions

## 🐛 Debugging

### Common Issues

- **Camera not working**: Check permissions and hardware availability
- **Barcode not detected**: Verify lighting and barcode quality
- **Build failures**: Check Gradle version compatibility

### Logging

Use appropriate log levels:

```kotlin
Log.d(TAG, "Debug information")
Log.i(TAG, "General information")
Log.w(TAG, "Warning message")
Log.e(TAG, "Error occurred", exception)
```

## 📚 Resources

- [Android Developer Documentation](https://developer.android.com/)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
- [Material Design Guidelines](https://material.io/design)

## 📞 Getting Help

- Create an issue for bugs or questions
- Join our discussions for general questions
- Check existing issues and documentation first

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to Barcode Scanner! 🙏
