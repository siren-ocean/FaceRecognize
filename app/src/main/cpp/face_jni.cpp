#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <jni.h>
#include <string>
#include <vector>
#include <malloc.h>
#include "log.h"
#include "recognize.h"
#include "mtcnn.h"
#include "utils.h"

using namespace std;
MTCNN *mtcnn;
Recognize *recognize;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_siren_ocean_recognize_FaceRecognize_initModels(JNIEnv *env, jobject instance,
                                                    jobject assetManager) {

    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    mtcnn = new MTCNN(mgr);
    recognize = new Recognize(mgr);
    LOGD("init models success");
    return (jboolean) true;
}

JNIEXPORT jintArray JNICALL
Java_siren_ocean_recognize_FaceRecognize_faceDetect(JNIEnv *env,
                                                    jobject instance,
                                                    jbyteArray imageData_,
                                                    jint imageWidth,
                                                    jint imageHeight,
                                                    jint imageType) {

    jbyte *imageData = env->GetByteArrayElements(imageData_, nullptr);
    if (nullptr == imageData) {
        env->ReleaseByteArrayElements(imageData_, imageData, 0);
        return nullptr;
    }

    //根据不同数据类型转ncnn::Mat
    ncnn::Mat ncnn_img = jniutils::formatMat((unsigned char *) imageData, imageWidth, imageHeight,
                                             imageType);
    if (ncnn_img.data == nullptr) {
        env->ReleaseByteArrayElements(imageData_, imageData, 0);
        return nullptr;
    }

    std::vector<Bbox> finalBbox;
    mtcnn->detectMaxFace(ncnn_img, finalBbox);
    auto num_face = static_cast<int32_t>(finalBbox.size());
    int out_size = 1 + num_face * 14;
    int *faceInfo = new int[out_size];
    faceInfo[0] = num_face;
    for (int i = 0; i < num_face; i++) {
        faceInfo[14 * i + 1] = finalBbox[i].x1;//left
        faceInfo[14 * i + 2] = finalBbox[i].y1;//top
        faceInfo[14 * i + 3] = finalBbox[i].x2;//right
        faceInfo[14 * i + 4] = finalBbox[i].y2;//bottom
        for (int j = 0; j < 10; j++) {
            faceInfo[14 * i + 5 + j] = static_cast<int>(finalBbox[i].ppoint[j]);
        }
    }

    jintArray tFaceInfo = env->NewIntArray(out_size);
    env->SetIntArrayRegion(tFaceInfo, 0, out_size, faceInfo);
    env->ReleaseByteArrayElements(imageData_, imageData, 0);
    return tFaceInfo;
}

JNIEXPORT jfloatArray JNICALL
Java_siren_ocean_recognize_FaceRecognize_extractFeature(JNIEnv *env, jobject instance,
                                                        jbyteArray imageData_,
                                                        jint imageWidth,
                                                        jint imageHeight,
                                                        jintArray point_,
                                                        jint imageType) {

    jint *point = env->GetIntArrayElements(point_, 0);
    jbyte *imageData = env->GetByteArrayElements(imageData_, nullptr);
    if (nullptr == imageData) {
        env->ReleaseByteArrayElements(imageData_, imageData, 0);
        return nullptr;
    }

    //根据不同数据类型转ncnn::Mat
    ncnn::Mat ncnn_img = jniutils::formatMat((unsigned char *) imageData, imageWidth, imageHeight,
                                             imageType);
    if (ncnn_img.data == nullptr) {
        env->ReleaseByteArrayElements(imageData_, imageData, 0);
        return nullptr;
    }

    ncnn::Mat img = recognize->preprocess(ncnn_img, (int *) point);
    int size = 128;
    auto *feature = new float[size];
    recognize->start(img, feature);
    jfloatArray featureArray = env->NewFloatArray(size);
    env->SetFloatArrayRegion(featureArray, 0, size, feature);
    env->ReleaseByteArrayElements(imageData_, imageData, 0);
    env->ReleaseIntArrayElements(point_, point, 0);
    return featureArray;
}

JNIEXPORT jboolean JNICALL
Java_siren_ocean_recognize_FaceRecognize_faceDeInit(JNIEnv *env, jobject instance) {
    delete mtcnn;
    delete recognize;
    LOGD("faceDeInit release success");
    return (jboolean) true;
}
}