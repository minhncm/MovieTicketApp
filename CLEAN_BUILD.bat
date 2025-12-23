@echo off
echo Cleaning Gradle cache and build folders...
echo.

echo Deleting .gradle folder...
if exist .gradle rmdir /s /q .gradle

echo Deleting app\build folder...
if exist app\build rmdir /s /q app\build

echo Deleting build folder...
if exist build rmdir /s /q build

echo.
echo Done! Now open Android Studio and:
echo 1. File -^> Sync Project with Gradle Files
echo 2. Build -^> Rebuild Project
echo.
pause
