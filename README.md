# FaceRecognize

该项目在YuvUtil的基础上，实现摄像头实时检测人脸，以及绘制人脸框。加入拍照录入人脸，和获取特征值。并模拟特征内存块管理，进行1：N匹配结果，显示最近似人员和相似度。

---
![avatar](demonstration.gif)

### 环境
NCNN: android 预编译库 20210525 f6c4952  
NDK: android-ndk-r19c

---

### 使用方式
底层主要封装了检测和人脸提特征两个方法，支持Nv21和RGBA两种数据格式。
<br>

```
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


### 计算相似度。
提供1:1 和 1:N匹配方法
<br>

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

### mtcnn检测
模型用到mtcnn，检测最大人脸，提升检测速度
<br>

```
//根据不同数据类型转ncnn::Mat
ncnn::Mat ncnn_img = jniutils::formatMat((unsigned char *) imageData, imageWidth, imageHeight,
                                             imageType);
if (ncnn_img.data == nullptr) {
    env->ReleaseByteArrayElements(imageData_, imageData, 0);
    return nullptr;
}

std::vector<Bbox> finalBbox;
mtcnn->detectMaxFace(ncnn_img, finalBbox);
```

### mobilefacenet识别
识别模型使用insightface的长度为128的mobilefacenet，用于提取人脸特征。
<br>

```
ncnn::Mat out;
ncnn::Extractor ex = Recognet.create_extractor();
ex.set_num_threads(threadnum);
ex.set_light_mode(true);
ex.input("data", img_);
ex.extract("fc1", out);

for (int j = 0; j < 128; j++) {
    feature_out[j] = out[j];
}
```

### 数据管理
正常业务情况下会用到数据库，内存管理则用到Map + LRU策略，此处使用普通的Map模拟一下
<br>

```
//模拟内存管理Map
private final Map<String, float[]> memoryMap = new HashMap<>();

//map转数组类型方便计算
private float[][] getMemoryFeatures() {
	if (memoryMap.size() == 0) {
	    return null;
	}
	
	int len = memoryMap.size();
	float[][] fea = new float[len][128];
	int i = 0;
	for (Map.Entry<String, float[]> entry : memoryMap.entrySet()) {
	    fea[i++] = entry.getValue();
	}
	return fea;
}

//根据index返回用户名
private String getNameByIndex(int index) {
	Iterator<Map.Entry<String, float[]>> iterator = memoryMap.entrySet().iterator();
	for (int i = 0; i < index; i++) {
	    iterator.next();
	}
	return iterator.next().getKey();
}
```

### 拍照注册
将图片bitmap直接转RGBA数据，通过检测人脸，提取特征，用户输入名字保存到Map
<br>


```
int[] faceInfo = FaceRecognize.getInstance().detectFace(imageData, width, height, FaceRecognize.IMAGE_TYPE_RGBA);
if (faceInfo != null && faceInfo.length > 1) {
    float[] feature = FaceRecognize.getInstance().featureExtract(imageData, width, height, faceInfo, FaceRecognize.IMAGE_TYPE_RGBA);
    Bitmap avatar = PhotoUtils.getAvatar(sourceBitmap, faceInfo);
    DialogHelper.showDialog(this, avatar, feature, new DialogHelper.EnterCallback() {
        @Override
        public void onConfirm(String name, float[] feature) {
            //保存注册人员的姓名与指定的特征
            memoryMap.put(name, feature);
        }
    });
}
```


### 参考
https://github.com/Tencent/ncnn  
https://github.com/deepinsight/insightface  
https://github.com/moli232777144/mtcnn_ncnn  
https://github.com/zhaotun/MTCNN_MobileFacenet_NCNN_Android  
https://github.com/siren-ocean/YuvUtil
