@echo off
setlocal EnableDelayedExpansion

echo ============================================
echo   OneDrive-Safe Android Build Script v2.1
echo   Updated with File Lock Resolution
echo ============================================
echo.
echo This script handles OneDrive file locking issues
echo by using safe Gradle options and smart cleanup.
echo.

set "BUILD_TYPE=%1"
if "%BUILD_TYPE%"=="" set "BUILD_TYPE=debug"

echo Building %BUILD_TYPE% APK with OneDrive-safe settings...
echo.

REM ============================================
REM   PRE-BUILD CLEANUP
REM ============================================

echo [1/4] Pre-build cleanup to prevent file locks...

REM Stop any running processes that might lock files
echo   - Stopping Gradle daemons...
call gradlew.bat --stop 2>nul

REM Kill potential locking processes
echo   - Terminating Java processes...
taskkill /F /IM java.exe 2>nul >nul
taskkill /F /IM javaw.exe 2>nul >nul

REM Wait for OneDrive sync
echo   - Waiting for OneDrive sync...
timeout /T 2 /NOBREAK >nul

echo [2/4] Configuring OneDrive-safe environment...

REM Enhanced OneDrive-safe Gradle options
set GRADLE_OPTS=-Dorg.gradle.daemon=false
set GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.parallel=false
set GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.caching=false
set GRADLE_OPTS=%GRADLE_OPTS% -Djava.io.tmpdir="%TEMP%"

echo [3/4] Building APK...

if /i "%BUILD_TYPE%"=="release" (
    echo Running: gradlew.bat assembleRelease --no-configuration-cache --no-daemon --no-build-cache
    call gradlew.bat assembleRelease --no-configuration-cache --no-daemon --no-build-cache --console=plain
    set "APK_PATH=app\build\outputs\apk\release\app-release-unsigned.apk"
) else (
    echo Running: gradlew.bat assembleDebug --no-configuration-cache --no-daemon --no-build-cache
    call gradlew.bat assembleDebug --no-configuration-cache --no-daemon --no-build-cache --console=plain
    set "APK_PATH=app\build\outputs\apk\debug\app-debug.apk"
)

set BUILD_RESULT=!ERRORLEVEL!

echo [4/4] Post-build cleanup...
call gradlew.bat --stop 2>nul

echo.
echo ============================================
echo                 BUILD RESULTS  
echo ============================================

if !BUILD_RESULT! EQU 0 (
    if exist "%APK_PATH%" (
        echo    SUCCESS: %BUILD_TYPE% APK built successfully!
        echo    Location: %APK_PATH%
        echo.
        echo    File details:
        for %%I in ("%APK_PATH%") do (
            set /A SIZE_MB=%%~zI/1024/1024
            echo      Size: %%~zI bytes (!SIZE_MB! MB)
            echo      Modified: %%~tI
        )
        echo.
        echo    APK ready for installation!
    ) else (
        echo    FAILED: %BUILD_TYPE% APK was not generated
        echo   Check the output above for errors.
    )
) else (
    echo    BUILD FAILED: Gradle build encountered errors
    echo.
    echo    Common OneDrive issues and solutions:
    echo     - File locks: Run cleanup_onedrive_locks.bat
    echo     - Sync conflicts: Pause OneDrive temporarily  
    echo     - Permissions: Run as Administrator
    echo     - Alternative: Use build_onedrive_safe_fixed.bat
)

echo ============================================
echo.
echo  OneDrive-Safe Build Options Used:
echo    --no-configuration-cache  (avoids cache file locking)
echo    --no-daemon              (prevents daemon file conflicts)
echo    --no-build-cache         (prevents build cache issues)
echo    GRADLE_OPTS optimized    (reduces file operations)
echo    Java temp dir redirected (uses Windows temp)
echo.

if !BUILD_RESULT! NEQ 0 (
    echo  If build failed due to file locks, try:
    echo   1. cleanup_onedrive_locks.bat
    echo   2. build_onedrive_safe_fixed.bat %BUILD_TYPE%
    echo   3. Pause OneDrive sync during builds
    echo.
)

REM Cleanup environment
set GRADLE_OPTS=

exit /B !BUILD_RESULT!
echo Usage: %0 [debug^|release]
echo   debug   - Build debug APK (default)
echo   release - Build release APK (unsigned)
echo.
pause
