
# Init submodules
git submodule init
git submodule update

cd app/jni

# Build libraries
if ! ./build_libraries.sh; then
  exit 1
fi

# Fluidsynth is a PITA to build; using the prebuilt release instead
wget https://github.com/FluidSynth/fluidsynth/releases/download/v2.3.1/fluidsynth-2.3.1-android24.zip
unzip fluidsynth-*.zip -d fluidsynth

# Assets
./simutrans/src/android/AndroidPreBuild.sh
cp -rf simutrans/simutrans/. ../src/main/assets

# Build Android project
cd ../..
cp -r app/jni/SDL/android-project/app/src/main/java app/src/main
cp -r app/jni/SDL/android-project/app/src/main/res app/src/main
./gradlew assembleDebug
