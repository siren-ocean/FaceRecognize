#include <time.h>
#include <string.h>
#include <jni.h>
#include <string>
#include <vector>
#include <cstdlib>
#include <algorithm>
#include <mat.h>
#include "log.h"

using namespace std;

#define IMAGE_TYPE_NV21 0
#define IMAGE_TYPE_RGBA 1

namespace jniutils {

    std::vector<std::string> modelToVector(JNIEnv *env, jobjectArray modelPaths);

    ncnn::Mat formatMat(unsigned char *imageData, int imageWidth, int imageHeight, int imageType);
}

