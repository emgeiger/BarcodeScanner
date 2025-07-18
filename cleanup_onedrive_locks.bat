@echo off
setlocal EnableDelayedExpansion

echo ============================================
echo   OneDrive Build Directory Cleanup Tool
echo   Fixes file locking issues automatically
echo ============================================
echo.

echo [INFO] This script resolves OneDrive file locking issues that prevent
echo        Android builds from cleaning properly.
echo.

REM ============================================
REM   STEP 1: STOP ALL LOCKING PROCESSES
REM ============================================

echo [STEP 1] Stopping processes that may lock build files...

echo   - Stopping Gradle daemons...
call gradlew.bat --stop 2>nul
timeout /T 2 /NOBREAK >nul

echo   - Terminating Java processes...
taskkill /F /IM java.exe 2>nul >nul
taskkill /F /IM javaw.exe 2>nul >nul

echo   - Checking for Android Studio processes...
taskkill /F /IM studio64.exe 2>nul >nul

echo   - Waiting for OneDrive to release file locks...
timeout /T 3 /NOBREAK >nul

REM ============================================
REM   STEP 2: IDENTIFY PROBLEMATIC DIRECTORIES
REM ============================================

echo [STEP 2] Scanning for problematic build directories...

set "PROBLEM_DIRS="
set "CLEANUP_COUNT=0"

REM Check the specific directories that failed in the error
set PROBLEM_PATHS[1]=app\build\kotlin\compileDebugKotlin\local-state
set PROBLEM_PATHS[2]=app\build\kotlin\compileDebugKotlin
set PROBLEM_PATHS[3]=app\build\kotlin
set PROBLEM_PATHS[4]=app\build\tmp\compileDebugJavaWithJavac\compileTransaction\backup-dir
set PROBLEM_PATHS[5]=app\build\tmp\compileDebugJavaWithJavac\compileTransaction\stash-dir
set PROBLEM_PATHS[6]=app\build\tmp\compileDebugJavaWithJavac\compileTransaction
set PROBLEM_PATHS[7]=app\build\tmp\compileDebugJavaWithJavac
set PROBLEM_PATHS[8]=app\build\tmp\compileReleaseJavaWithJavac\compileTransaction\backup-dir
set PROBLEM_PATHS[9]=app\build\tmp\compileReleaseJavaWithJavac\compileTransaction\stash-dir
set PROBLEM_PATHS[10]=app\build\tmp\compileReleaseJavaWithJavac\compileTransaction
set PROBLEM_PATHS[11]=app\build\tmp\compileReleaseJavaWithJavac
set PROBLEM_PATHS[12]=app\build\tmp

for /L %%i in (1,1,12) do (
    if exist "!PROBLEM_PATHS[%%i]!" (
        echo     Found problematic directory: !PROBLEM_PATHS[%%i]!
        set /A CLEANUP_COUNT+=1
    )
)

if !CLEANUP_COUNT! EQU 0 (
    echo   ✅ No problematic directories found
    goto :VERIFY_CLEAN
)

echo   📊 Found !CLEANUP_COUNT! directories that need cleanup
echo.

REM ============================================
REM   STEP 3: SMART CLEANUP PROCESS
REM ============================================

echo [STEP 3] Performing smart cleanup of locked directories...

REM Function to safely remove a directory
:SAFE_REMOVE
set "TARGET_DIR=%~1"
if not exist "%TARGET_DIR%" goto :EOF

echo   🧹 Cleaning: %TARGET_DIR%

REM Try method 1: Remove attributes and delete
attrib -R -H -S "%TARGET_DIR%\*.*" /S /D 2>nul
rmdir /S /Q "%TARGET_DIR%" 2>nul

if not exist "%TARGET_DIR%" (
    echo     ✅ Successfully removed
    goto :EOF
)

REM Try method 2: Force unlock and retry
echo     🔓 Attempting force unlock...
timeout /T 1 /NOBREAK >nul
for /D %%d in ("%TARGET_DIR%\*") do (
    attrib -R -H -S "%%d\*.*" /S /D 2>nul
    rmdir /S /Q "%%d" 2>nul
)
rmdir /S /Q "%TARGET_DIR%" 2>nul

if not exist "%TARGET_DIR%" (
    echo     ✅ Force unlock successful
    goto :EOF
)

REM Try method 3: Rename and mark for deletion
echo     🏷️  Directory still locked, marking for cleanup...
set "TEMP_NAME=%TARGET_DIR%_cleanup_%RANDOM%"
ren "%TARGET_DIR%" "%TEMP_NAME%" 2>nul
if !ERRORLEVEL! EQU 0 (
    echo     ✅ Renamed for later cleanup: %TEMP_NAME%
) else (
    echo     ❌ Directory remains locked by OneDrive
)
goto :EOF

REM Clean directories in reverse order (deepest first)
for /L %%i in (1,1,12) do (
    if exist "!PROBLEM_PATHS[%%i]!" (
        call :SAFE_REMOVE "!PROBLEM_PATHS[%%i]!"
    )
)

REM ============================================
REM   STEP 4: ADDITIONAL CLEANUP
REM ============================================

echo [STEP 4] Additional cleanup of build artifacts...

REM Clean other common problematic directories
set OTHER_DIRS[1]=app\build\intermediates
set OTHER_DIRS[2]=app\build\generated
set OTHER_DIRS[3]=app\build\outputs\logs
set OTHER_DIRS[4]=build\kotlin

for /L %%i in (1,1,4) do (
    if exist "!OTHER_DIRS[%%i]!" (
        echo   🧹 Cleaning: !OTHER_DIRS[%%i]!
        rmdir /S /Q "!OTHER_DIRS[%%i]!" 2>nul
        if not exist "!OTHER_DIRS[%%i]!" (
            echo     ✅ Successfully removed
        ) else (
            echo     ⚠️  Partially cleaned
        )
    )
)

REM Clean gradle cache if present in project
if exist ".gradle" (
    echo   🧹 Cleaning local Gradle cache...
    rmdir /S /Q ".gradle" 2>nul
    if not exist ".gradle" (
        echo     ✅ Local Gradle cache cleaned
    )
)

:VERIFY_CLEAN
echo.
echo [STEP 5] Verification and recommendations...

REM Check if main problematic directories are gone
set "REMAINING_ISSUES=0"
for /L %%i in (1,1,12) do (
    if exist "!PROBLEM_PATHS[%%i]!" (
        set /A REMAINING_ISSUES+=1
    )
)

echo   📊 Cleanup Results:
echo     - Original issues: !CLEANUP_COUNT!
echo     - Remaining issues: !REMAINING_ISSUES!

if !REMAINING_ISSUES! EQU 0 (
    echo     ✅ All problematic directories cleaned successfully!
    echo.
    echo   🎯 Ready for build! You can now run:
    echo      - build_onedrive_safe_fixed.bat debug
    echo      - build_onedrive_safe_fixed.bat release
    echo      - gradlew.bat assembleDebug --no-daemon
) else (
    echo     ⚠️  Some directories remain locked
    echo.
    echo   💡 Manual Resolution Required:
    echo      1. Pause OneDrive sync in system tray
    echo      2. Close any open IDEs or file explorers
    echo      3. Run this script as Administrator
    echo      4. Restart Windows if issues persist
    echo.
    echo   🔧 Alternative Solutions:
    echo      - Move project outside OneDrive for development
    echo      - Use Git for version control instead
    echo      - Use build_offline.bat for completely offline builds
)

REM ============================================
REM   ONEDRIVE-SPECIFIC RECOMMENDATIONS
REM ============================================

echo.
echo ============================================
echo              ONEDRIVE OPTIMIZATION
echo ============================================
echo.
echo 📝 To prevent future file locking issues:
echo.
echo   1. 📁 EXCLUDE BUILD DIRECTORIES from OneDrive sync:
echo      Right-click on 'build' folders → 'Always keep on this device'
echo      This prevents OneDrive from syncing temporary build files
echo.
echo   2. ⚙️  ADD TO .onedriveignore (if available):
echo      **/build/
echo      **/.gradle/
echo      **/gradle/
echo      *.tmp
echo      *.log
echo.
echo   3. 🔧 GRADLE USER HOME outside OneDrive:
echo      Set GRADLE_USER_HOME to C:\gradle-cache
echo      This moves Gradle cache away from OneDrive
echo.
echo   4. 🏗️  USE BUILD SCRIPTS:
echo      - build_onedrive_safe_fixed.bat (this version)
echo      - build_offline.bat (completely offline)
echo      - build_gradle9.bat (modern features)
echo.
echo   5. 🚀 DEVELOPMENT BEST PRACTICES:
echo      - Keep source code in OneDrive for backup
echo      - Use Git for version control
echo      - Build in local directories outside OneDrive
echo.

REM Show OneDrive status
echo   📊 Current OneDrive Status:
tasklist /FI "IMAGENAME eq OneDrive.exe" 2>nul | find "OneDrive.exe" >nul
if !ERRORLEVEL! EQU 0 (
    echo     🟢 OneDrive is running
    echo     💡 Consider pausing sync during builds
) else (
    echo     🔴 OneDrive is not running
)

echo.
echo ✅ Cleanup complete! Ready for OneDrive-safe building.
echo.

timeout /T 3 /NOBREAK >nul
