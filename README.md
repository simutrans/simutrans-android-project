# Simutrans Android Project

Project to compile Simutrans for Android with gradle and CMake.

## Requirements

- openjdk-17
- commandline tools (for sdkmanager)
- sdkmanager --install "platform-tools"
- sdkmanager --install "build-tools;33.0.1"
- sdkmanager --install "cmake;3.22.1"
- sdkmanager --install "ndk;25.1.8937393"

The exact version numbers may vary; check the pipeline to know what the latest versions are and details about installing them.

## Compilation

1. Copy Simutrans source code to simutrans/jni/simutrans
2. Set JAVA_HOME, ANDROID_HOME and ANDROID_NDK environment variables and run ./prepareAndBuild.sh

``JAVA_HOME=/usr/lib/jvm/java-17-openjdk ANDROID_HOME=/opt/android-sdk ANDROID_NDK=/opt/android-sdk/ndk/25.1.8937393 ./prepareAndBuild.sh``
