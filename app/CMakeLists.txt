cmake_minimum_required(VERSION 3.10)

#include头文件目录
include_directories(src/main/cpp)

#source directory源文件目录
file(GLOB FACE_SRC
        src/main/cpp/face_jni.cpp
        src/main/cpp/recognize.cpp
        src/main/cpp/utils.cpp
        src/main/cpp/mtcnn.cpp)
set(FACE_COMPILE_CODE ${FACE_SRC})

#添加ncnn库
set(ncnn_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/ncnn/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

#编译为动态库
add_library(recognize SHARED ${FACE_COMPILE_CODE})

#添加工程所依赖的库
target_link_libraries(recognize ncnn)
