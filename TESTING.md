# Testing Guide for Android Barcode Scanner

This document describes the testing strategy and how to run tests for the Android Barcode Scanner application.

## Test Structure

### Unit Tests (`src/test/`)

- **BarcodeHistoryRepositoryTest.kt**: Tests data persistence and barcode history management
- **MainActivityTest.kt**: Tests MainActivity logic components
- **BarcodeAnalyzerLogicTest.kt**: Tests barcode analysis and processing logic
- **TestUtils.kt**: Utility classes and helper methods for testing

### Instrumented Tests (`src/androidTest/`)

- **MainActivityInstrumentedTest.kt**: Tests UI components and Android-specific functionality
- **BarcodeHistoryRepositoryIntegrationTest.kt**: Integration tests with real Android context

## Running Tests

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumented Tests (requires device/emulator)

```bash
./gradlew connectedAndroidTest
```

### Run Specific Test Classes

```bash
# Unit test
./gradlew test --tests com.encana.barcodescanner.data.BarcodeHistoryRepositoryTest

# Instrumented test
./gradlew connectedAndroidTest --tests com.encana.barcodescanner.MainActivityInstrumentedTest
```

### Run Test Suites

```bash
# All unit tests
./gradlew test --tests com.encana.barcodescanner.UnitTestSuite

# All instrumented tests
./gradlew connectedAndroidTest --tests com.encana.barcodescanner.InstrumentedTestSuite
```

## Test Coverage

### What's Tested

#### BarcodeHistoryRepository

- ✅ Saving barcode items to history
- ✅ Retrieving history items
- ✅ Clearing history
- ✅ Deleting specific items
- ✅ Handling duplicates
- ✅ History size limits
- ✅ Data persistence across instances
- ✅ JSON serialization/deserialization
- ✅ Error handling for malformed data

#### MainActivity

- ✅ Barcode type mapping for all supported formats
- ✅ UI component initialization
- ✅ Button click handling
- ✅ Camera permission handling
- ✅ Display text updates

#### BarcodeAnalyzer Logic

- ✅ Processing empty barcode lists
- ✅ Processing single barcodes
- ✅ Processing multiple barcodes
- ✅ Handling null barcode values
- ✅ Format validation for all supported types

### Test Dependencies

The following test dependencies are included:

#### Unit Testing

- **JUnit 4**: Core testing framework
- **Mockito**: Mocking framework for isolating dependencies
- **Robolectric**: Android unit testing without device/emulator

#### Instrumented Testing

- **Espresso**: UI testing framework
- **AndroidX Test**: Android testing utilities
- **UI Automator**: Cross-app UI testing

## Best Practices Implemented

1. **Isolation**: Unit tests use mocks to isolate components
2. **Real Context**: Integration tests use real Android context
3. **Comprehensive Coverage**: Tests cover happy paths, edge cases, and error conditions
4. **Maintainability**: Test utilities and helpers reduce code duplication
5. **Fast Execution**: Unit tests run quickly without Android dependencies
6. **Realistic Scenarios**: Instrumented tests simulate real user interactions

## Test Data

Test utilities provide:

- Sample barcode items with various types
- Common barcode values for different formats
- Helper methods for creating test data

## Continuous Integration

Tests are configured to run in CI/CD pipelines:

- Unit tests run on every pull request
- Instrumented tests run on develop branch merges
- Test reports are generated and archived

## Troubleshooting

### Common Issues

1. **Tests fail with "No such method" error**
   - Ensure all test dependencies are properly configured
   - Check for version conflicts between test and app dependencies

2. **Instrumented tests fail to start**
   - Ensure emulator/device is connected and running
   - Check that app permissions are granted

3. **Mock injection fails**
   - Verify @Mock annotations are used correctly
   - Ensure MockitoJUnitRunner is specified

### Running Tests in Android Studio

1. Right-click on test class/method → "Run"
2. Use "Run with Coverage" to see test coverage reports
3. View test results in the "Run" window

### Command Line Test Reports

Test reports are generated in:

- Unit tests: `app/build/reports/tests/testDebugUnitTest/`
- Instrumented tests: `app/build/reports/androidTests/connected/`
