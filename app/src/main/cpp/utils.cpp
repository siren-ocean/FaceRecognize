#include "utils.h"

namespace jniutils {

    std::vector<std::string> modelToVector(JNIEnv *env, jobjectArray modelPaths) {
        jsize size = env->GetArrayLength(modelPaths);
        std::vector<std::string> vector;

        for (int i = 0; i < size; i++) {
            jstring path = (jstring) env->GetObjectArrayElement(modelPaths, i);
            std::string str = (std::string) env->GetStringUTFChars(path, nullptr);
            vector.push_back(str);
            env->DeleteLocalRef(path);
        }
        env->DeleteLocalRef(modelPaths);
        return vector;
    }

    ncnn::Mat formatMat(unsigned char *imageData, int imageWidth, int imageHeight, int imageType) {

        switch (imageType) {
            case IMAGE_TYPE_NV21: {
                auto *charData = new unsigned char[imageWidth * imageHeight * 3];
                ncnn::yuv420sp2rgb((unsigned char *) imageData, imageWidth, imageHeight, charData);
                ncnn::Mat ncnn_img = ncnn::Mat::from_pixels(charData, ncnn::Mat::PIXEL_RGB,
                                                            imageWidth,
                                                            imageHeight);
                delete[] charData;
                return ncnn_img;
            }
            case IMAGE_TYPE_RGBA: {
                ncnn::Mat ncnn_img = ncnn::Mat::from_pixels((unsigned char *) imageData,
                                                            ncnn::Mat::PIXEL_RGBA2RGB,
                                                            imageWidth,
                                                            imageHeight);

                return ncnn_img;
            }
            default :
                LOGD("cannot match the image type");
                return NULL;
        }
    }
}