# simutrans-android-cmake

1. Clone this repository

``git clone git@github.com:Roboron3042/simutrans-android-cmake.git``

2. Install Android requirements

- openjdk-17
- commandline tools (for sdkmanager)
- sdkmanager --install "platform-tools"
- sdkmanager --install "build-tools;33.0.1"
- sdkmanager --install "cmake;3.22.1"
- sdkmanager --install "ndk;25.1.8937393"


3. Build (adjusting variables)

``JAVA_HOME=/usr/lib/jvm/java-17-openjdk ANDROID_HOME=/opt/android-sdk ANDROID_NDK=/opt/android-sdk/ndk/25.1.8937393 ./prepareAndBuild.sh``
