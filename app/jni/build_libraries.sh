#!/usr/bin/env bash

if [ -z "$ANDROID_NDK" ]; then
  echo "Please set ANDROID_NDK to the Android NDK folder"
  exit 1
fi

if [ -z "$JAVA_HOME" ]; then
  echo "Please set JAVA_HOME to the java folder"
  exit 1
fi



function build_for_android {
  ABI=$1
  ANDROID_SYSTEM_VERSION=$2
  BUILD_TYPE_NAME=$3
  shift 3
  CMAKE_ARGS=$@
  if [[ "$BUILD_TYPE_NAME" == "debug" ]]
  then
    BUILD_TYPE="Debug"
  elif [[ "$BUILD_TYPE_NAME" == "release" ]]
  then
    BUILD_TYPE="Release"
  else
    echo "the BUILD_TYPE_NAME in second argument isn't managed : ${BUILD_TYPE_NAME}"
    exit 1
  fi

  ABI_BUILD_DIR=${CMAKE_DIR}/build/${ABI}
  OUTPUT_DIR=${LIBRARY_DIR}/prebuilt/${BUILD_TYPE_NAME}/${ABI}

  cmake -B${ABI_BUILD_DIR} \
        -H. \
        -DCMAKE_BUILD_TYPE=${BUILD_TYPE} \
        -DANDROID_ABI=${ABI} \
        -DCMAKE_RUNTIME_OUTPUT_DIRECTORY=$OUTPUT_DIR \
        -DCMAKE_LIBRARY_OUTPUT_DIRECTORY=$OUTPUT_DIR \
        -DCMAKE_ARCHIVE_OUTPUT_DIRECTORY=$OUTPUT_DIR \
        -DCMAKE_INSTALL_LIBDIR=$OUTPUT_DIR \
        -DANDROID_PLATFORM=${ANDROID_SYSTEM_VERSION} \
        -DCMAKE_ANDROID_STL=c++_static \
        -DCMAKE_ANDROID_NDK=$ANDROID_NDK \
        -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
        -DANDROID_TOOLCHAIN=clang \
        -DCMAKE_INSTALL_PREFIX=. \
        ${CMAKE_ARGS[@]}

  pushd ${ABI_BUILD_DIR}
    make -j5
  popd

  rm -rf ${ABI_BUILD_DIR}
}

function build_library {
  LIBRARY_DIR=$1
  CMAKE_DIR=$2
  shift 2
  CMAKE_ARGS=$@
  cd $CMAKE_DIR
  build_for_android armeabi-v7a android-21 debug ${CMAKE_ARGS[@]}
  build_for_android arm64-v8a android-21 debug ${CMAKE_ARGS[@]}
  #build_for_android x86 android-21 debug ${CMAKE_ARGS[@]}
  #build_for_android x86_64 android-21 debug ${CMAKE_ARGS[@]}
  #build_for_android armeabi-v7a android-21 release ${CMAKE_ARGS[@]}
  #build_for_android arm64-v8a android-21 release ${CMAKE_ARGS[@]}
  #build_for_android x86 android-21 release ${CMAKE_ARGS[@]}
  #build_for_android x86_64 android-21 release ${CMAKE_ARGS[@]}
  cd $TOP_DIR
}

TOP_DIR=$(pwd)
build_library $TOP_DIR/zlib $TOP_DIR/zlib
build_library $TOP_DIR/bzip2 $TOP_DIR/bzip2 "-DENABLE_LIB_ONLY=ON"
build_library $TOP_DIR/libpng $TOP_DIR/libpng
cp $TOP_DIR/libpng/scripts/pnglibconf.h.prebuilt $TOP_DIR/libpng/pnglibconf.h
ZSTD_CMAKE_ARGS=("-DZSTD_BUILD_STATIC=OFF" "-DZSTD_BUILD_PROGRAMS=OFF")
build_library $TOP_DIR/zstd $TOP_DIR/zstd/build/cmake ${ZSTD_CMAKE_ARGS[@]}

# Not necessary, SLD is compiled as CMake subproject
#SDL_CMAKE_ARGS=("-DHAVE_IMMINTRIN_H=OFF")
#build_library $TOP_DIR/SDL $TOP_DIR/SDL  ${SDL_CMAKE_ARGS[@]}
