# FaceRecognize

该项目在YuvUtil的基础上，实现摄像头实时检测人脸，以及绘制人脸框。加入拍照录入人脸，和获取特征值。并模拟特征内存块管理，进行1：N匹配结果，显示最近似人员和相似度。

---
### 环境
NCNN: android 预编译库 20210525 f6c4952  
NDK: android-ndk-r19c

---

### 使用方式
底层主要封装了检测和人脸提特征两个方法，支持Nv21和RGBA两种数据格式。
<br>

```
/**
     * 初始化
     *
     * @param detectPath    检测模型路径
     * @param recognizePath 识别模型路径
     * @return
     */
    private native boolean initModels(String[] detectPath, String[] recognizePath);

    /**
     * 人脸检测
     *
     * @param imageData 数据
     * @param width     宽度
     * @param height    高度
     * @param imageType 类型 IMAGE_TYPE_NV21 | IMAGE_TYPE_RGBA
     * @return
     */
    private native int[] faceDetect(byte[] imageData, int width, int height, int imageType);

    /**
     * 人脸提特征
     *
     * @param imageData 数据
     * @param width     宽度
     * @param height    高度
     * @param faceInfo  关键点
     * @param imageType 类型 IMAGE_TYPE_NV21 | IMAGE_TYPE_RGBA
     * @return
     */
    private native float[] extractFeature(byte[] imageData, int width, int height, int[] faceInfo, int imageType);

```
<br>
检测模型用到mtcnn，并只检测最大人脸，提升检测速度。  
识别模型使用insightface的长度为128的mobilefacenet，用于提取人脸特征。  
一般特征值会保存进数据库，这里就不加了，避免提升整体的复杂度。  
正常情况下内存也会预留一份特征数组，方便计算结果，一般用LRU策略，此处使用简单的Map模拟一下计算方式。



###计算相似度。

```
/**
 * 特征比对：1：N
 * 算出最高的相似度以及相对应的index
 */
public static float[] distanceArray(float[] p, float[][] array)；


/**
 * 特征比对：1：1
 * 计算两者相似度
 */
 public static float distance(float[] p1, float[] p2);
```



### 参考
https://github.com/Tencent/ncnn  
https://github.com/deepinsight/insightface  
https://github.com/moli232777144/mtcnn_ncnn  
https://github.com/zhaotun/MTCNN_MobileFacenet_NCNN_Android  
https://github.com/siren-ocean/YuvUtil
