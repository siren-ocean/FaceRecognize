//
// Created by liguiyuan on 19-4-5.
// Update by Siren on 2021/6/24
//
#ifndef MOBILEFACENET_AS_RECOGNIZE_H
#define MOBILEFACENET_AS_RECOGNIZE_H

#include <string>
#include <algorithm>
#include <mat.h>
#include <net.h>

class Recognize {
public:
    Recognize(std::vector<std::string> &modelFiles);

    Recognize(const std::string &model_path);

    ~Recognize();

    void start(ncnn::Mat &ncnn_img, float* &feature128);

    void SetThreadNum(int threadNum);

    void getAffineMatrix(float *src_5pts, const float *dst_5pts, float *M);

    void warpAffineMatrix(ncnn::Mat src, ncnn::Mat &dst, float *M, int dst_w, int dst_h);

    ncnn::Mat preprocess(ncnn::Mat img, int *info);

private:
    void RecogNet(ncnn::Mat &img_);

    void normalize(std::vector<float> &feature);

    ncnn::Net Recognet;
    //ncnn::Mat ncnn_img;
    std::vector<float> feature_out;
    int threadnum = 1;
};

double calculSimilar(std::vector<float> &v1, std::vector<float> &v2, int distance_metric);

#endif //MOBILEFACENET_AS_RECOGNIZE_H
