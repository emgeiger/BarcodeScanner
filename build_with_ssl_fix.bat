@echo off
echo Starting build process...
cd /d "c:\Users\egeiger\OneDrive - Ovintiv\Documents\GitHub\BarcodeScanner"

echo Setting Java options to bypass SSL issues...
set GRADLE_OPTS=-Dorg.gradle.internal.http.connectionTimeout=120000 -Dorg.gradle.internal.http.socketTimeout=120000 -Dtrust_all_cert=true -Dcom.sun.net.ssl.checkRevocation=false -Dtrust_all_cert=true

echo Building debug APK...
gradlew.bat --no-daemon --stacktrace assembleDebug

echo.
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo SUCCESS: APK built successfully!
    echo Location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo FAILED: APK was not generated. Check the output above for errors.
)

pause
