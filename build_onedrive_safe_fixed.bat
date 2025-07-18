@echo off
setlocal EnableDelayedExpansion

echo ============================================
echo   OneDrive-Safe Android Build Script v2.0
echo   Fixed for Build Directory Locking Issues
echo ============================================
echo.

set "BUILD_TYPE=%1"
if "%BUILD_TYPE%"=="" set "BUILD_TYPE=debug"

echo Target: %BUILD_TYPE% APK with OneDrive-safe settings
echo Project: BarcodeScanner
echo.

REM ============================================
REM   ONEDRIVE FILE LOCKING MITIGATION
REM ============================================

echo [STEP 1] Stopping potential file locks...

REM Stop any running Gradle daemons that might lock files
echo   - Stopping Gradle daemons...
call gradlew.bat --stop 2>nul

REM Kill any Java processes that might be locking build files
echo   - Terminating Java build processes...
taskkill /F /IM java.exe 2>nul >nul
taskkill /F /IM javaw.exe 2>nul >nul

REM Wait for OneDrive to finish syncing
echo   - Waiting for OneDrive sync to complete...
timeout /T 3 /NOBREAK >nul

echo [STEP 2] OneDrive-safe cleanup...

REM Manual cleanup of problematic directories before Gradle clean
if exist "app\build\kotlin" (
    echo   - Removing Kotlin cache directories...
    rmdir /S /Q "app\build\kotlin" 2>nul
    if exist "app\build\kotlin" (
        echo   ⚠️  Kotlin cache still locked, will use alternative cleanup
        for /D %%d in ("app\build\kotlin\*") do (
            attrib -R -H -S "%%d\*.*" /S 2>nul
            rmdir /S /Q "%%d" 2>nul
        )
    )
)

if exist "app\build\tmp" (
    echo   - Removing temp directories...
    rmdir /S /Q "app\build\tmp" 2>nul
    if exist "app\build\tmp" (
        echo   ⚠️  Temp files still locked, marking for cleanup
        attrib -R -H -S "app\build\tmp\*.*" /S 2>nul
    )
)

if exist "app\build\intermediates" (
    echo   - Removing intermediate files...
    rmdir /S /Q "app\build\intermediates" 2>nul
)

REM ============================================
REM   ENHANCED GRADLE CONFIGURATION
REM ============================================

echo [STEP 3] Configuring OneDrive-safe Gradle environment...

REM Enhanced OneDrive-safe Gradle options
set GRADLE_OPTS=-Dorg.gradle.daemon=false
set GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.parallel=false
set GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.caching=false
set GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.configureondemand=false
set GRADLE_OPTS=%GRADLE_OPTS% -Dfile.encoding=UTF-8
set GRADLE_OPTS=%GRADLE_OPTS% -Djava.io.tmpdir="%TEMP%"

REM Additional Windows/OneDrive specific settings
set GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.internal.launcher.welcomeMessageEnabled=false
set GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.console=plain

echo   GRADLE_OPTS configured: %GRADLE_OPTS%
echo.

REM ============================================
REM   SMART BUILD EXECUTION
REM ============================================

echo [STEP 4] Executing smart build process...

REM First attempt: Gentle clean without full daemon shutdown
echo   - Attempting gentle clean...
call gradlew.bat clean --no-configuration-cache --no-daemon --no-build-cache --console=plain 2>error_log.txt
if !ERRORLEVEL! EQU 0 (
    echo   ✅ Gentle clean successful
    goto :BUILD_APK
) else (
    echo   ⚠️  Gentle clean failed, trying force cleanup...
    type error_log.txt
    echo.
)

REM Second attempt: Force cleanup with extended OneDrive-safe options
echo   - Force cleaning with extended options...
timeout /T 2 /NOBREAK >nul

REM Manually remove what we can
for /D %%d in ("app\build\*") do (
    if not "%%~nd"=="outputs" (
        echo     Removing %%d...
        rmdir /S /Q "%%d" 2>nul
    )
)

REM Try clean again
call gradlew.bat clean --no-configuration-cache --no-daemon --no-build-cache --refresh-dependencies --console=plain 2>error_log2.txt
if !ERRORLEVEL! EQU 0 (
    echo   ✅ Force clean successful
) else (
    echo   ⚠️  Force clean also failed, proceeding with build anyway...
    type error_log2.txt
    echo   (Build may still succeed despite clean issues)
    echo.
)

:BUILD_APK
echo [STEP 5] Building APK...

if /i "%BUILD_TYPE%"=="release" (
    echo   Building RELEASE APK...
    call gradlew.bat assembleRelease --no-configuration-cache --no-daemon --no-build-cache --console=plain --refresh-dependencies
    set "APK_PATH=app\build\outputs\apk\release\app-release-unsigned.apk"
    set "APK_NAME=release"
) else (
    echo   Building DEBUG APK...
    call gradlew.bat assembleDebug --no-configuration-cache --no-daemon --no-build-cache --console=plain --refresh-dependencies
    set "APK_PATH=app\build\outputs\apk\debug\app-debug.apk"
    set "APK_NAME=debug"
)

set BUILD_RESULT=!ERRORLEVEL!

REM ============================================
REM   POST-BUILD CLEANUP & VERIFICATION
REM ============================================

echo.
echo [STEP 6] Post-build cleanup...

REM Stop daemon to prevent future file locks
echo   - Stopping Gradle daemon...
call gradlew.bat --stop 2>nul

REM Clean up temporary files
if exist "error_log.txt" del /Q "error_log.txt" 2>nul
if exist "error_log2.txt" del /Q "error_log2.txt" 2>nul

echo.
echo ============================================
echo                 BUILD RESULTS
echo ============================================

if !BUILD_RESULT! EQU 0 (
    if exist "%APK_PATH%" (
        echo    SUCCESS: %APK_NAME% APK built successfully!
        echo    Location: %APK_PATH%
        echo.
        echo    File Details:
        for %%I in ("%APK_PATH%") do (
            set /A SIZE_MB=%%~zI/1024/1024
            echo      Size: %%~zI bytes (!SIZE_MB! MB)
            echo      Modified: %%~tI
            echo      Full Path: %%~fI
        )
        echo.
        echo    APK is ready for installation!
        echo    Tip: adb install "%APK_PATH%"
    ) else (
        echo    UNEXPECTED: Build reported success but APK not found
        echo    Checking alternative locations...
        if exist "app\build\outputs\apk" (
            dir /B /S "app\build\outputs\apk\*.apk" 2>nul
        )
    )
) else (
    echo    FAILED: Build process encountered errors
    echo    This might be due to OneDrive file locking issues
    echo.
    echo    Troubleshooting suggestions:
    echo     1. Close any IDEs (Android Studio, VS Code)
    echo     2. Pause OneDrive sync temporarily
    echo     3. Run script as Administrator
    echo     4. Try: build_offline.bat %BUILD_TYPE%
    echo     5. Check: tasklist ^| findstr java
)

echo ============================================
echo.
echo  OneDrive-Safe Build Configuration Used:
echo    No Gradle daemon (prevents file locks)
echo    No configuration cache (avoids cache conflicts)
echo    No build cache (prevents sync issues)
echo    No parallel execution (reduces conflicts)
echo    Manual cleanup of problematic directories
echo    Smart retry logic for OneDrive sync delays
echo    Enhanced Windows temp directory usage
echo.
echo  For future builds, consider:
echo   - Using build_gradle9.bat for modern features
echo   - Moving project outside OneDrive for development
echo   - Using Git for version control instead of OneDrive sync
echo.

REM Cleanup environment variables
set GRADLE_OPTS=

exit /B !BUILD_RESULT!
