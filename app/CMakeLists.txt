cmake_minimum_required(VERSION 3.4.1)

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
add_library(libncnn STATIC IMPORTED)
set_target_properties(libncnn
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/ncnn/libs/${ANDROID_ABI}/libncnn.a)

#编译为动态库
add_library(recognize SHARED ${FACE_COMPILE_CODE})

#添加工程所依赖的库
find_library(log-lib log)
find_library(android-lib android)
target_link_libraries(recognize
        libncnn
        jnigraphics
        z
        ${android-lib}
        ${log-lib})
