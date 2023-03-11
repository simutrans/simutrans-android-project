
# Init submodules
git submodule init
git submodule update

cd app/jni

# Build libraries
if ! ./build_libraries.sh; then
  exit 1
fi

# Assets
./simutrans/src/android/AndroidPreBuild.sh
mv AndroidData ../src/main/assets

# Build Android project
cd ../..
cp -r app/jni/SDL/android-project/app/src/main/java app/src/main
cp -r app/jni/SDL/android-project/app/src/main/res app/src/main
./gradlew assembleDebug
