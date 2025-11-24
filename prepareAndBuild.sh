
if [ ! -d "simutrans/jni/simutrans" ]; then 
  echo "First you must copy simutrans source directory to simutrans/jni/simutrans"
  exit 1
fi

# Init submodules
git submodule init
git submodule update

cd simutrans/jni

# Build libraries
if ! ./build_libraries.sh; then
  exit 1
fi

# Fluidsynth is a PITA to build; using the prebuilt release instead
#wget https://github.com/FluidSynth/fluidsynth/releases/download/v2.3.5/fluidsynth-2.3.5-android24.zip
#unzip fluidsynth-*.zip -d fluidsynth

# Assets
./simutrans/src/android/AndroidPreBuild.sh
cp -rf simutrans/simutrans/. ../src/main/assets

# Revision
cd simutrans/tools
REVISION=$(./get_revision.sh)
sed -i "s/versionCode [0-9]\+/versionCode $REVISION/" ../../../build.gradle

# Get the version and nightly status
cd ../../../..
VERSION=`sed -n 's/#define SIM_VERSION_MAJOR *\([0-9]*\)$/\1/ p' <simutrans/jni/simutrans/src/simutrans/simversion.h`.`sed -n 's/#define SIM_VERSION_MINOR *\([0-9]*\)$/\1/ p' <simutrans/jni/simutrans/src/simutrans/simversion.h`.`sed -n 's/#define SIM_VERSION_PATCH *\([0-9]*\)$/\1/ p' <simutrans/jni/simutrans/src/simutrans/simversion.h`
NIGHTLY=`sed -n 's/#define SIM_VERSION_BUILD SIM_BUILD_NIGHTLY/ Nightly/ p' <simutrans/jni/simutrans/src/simutrans/simversion.h``sed -n 's/#define SIM_VERSION_BUILD SIM_BUILD_RELEASE_CANDIDATE/ Release candidate/ p' <simutrans/jni/simutrans/src/simutrans/simversion.h`
sed -i 's/versionName.*$/versionName "'"$VERSION$NIGHTLY"'"/' simutrans/build.gradle

# Build Android project
cp -r simutrans/jni/SDL/android-project/app/src/main/java simutrans/src/main
./gradlew assembleRelease
./gradlew bundleRelease
