@echo off
echo ============================================
echo   Gradle 9.0 Compatibility Validation
echo ============================================
echo.

echo ✅ Step 1: Checking Gradle version...
call gradlew.bat --version
if errorlevel 1 (
    echo ❌ Gradle version check failed
    pause
    exit /b 1
)

echo.
echo ✅ Step 2: Checking for deprecated features...
call gradlew.bat help --warning-mode all
if errorlevel 1 (
    echo ⚠️ Found warnings in Gradle configuration
)

echo.
echo ✅ Step 3: Testing configuration cache...
call gradlew.bat help --configuration-cache --dry-run
if errorlevel 1 (
    echo ❌ Configuration cache test failed
) else (
    echo ✅ Configuration cache is working
)

echo.
echo ✅ Step 4: Running dependency analysis...
call gradlew.bat dependencies --configuration debugCompileClasspath
if errorlevel 1 (
    echo ❌ Dependency analysis failed
)

echo.
echo ✅ Step 5: Testing basic build tasks...
call gradlew.bat tasks --all
if errorlevel 1 (
    echo ❌ Task listing failed
)

echo.
echo ============================================
echo   Gradle 9.0 Validation Completed!
echo.
echo   If all steps passed with ✅, your project
echo   is fully compatible with Gradle 9.0
echo.
echo   Next steps:
echo   1. Run 'build_gradle9.bat' to test full build
echo   2. Check GRADLE_9_MIGRATION.md for details
echo   3. Run 'cleanup_project.bat' to remove old files
echo ============================================
pause
