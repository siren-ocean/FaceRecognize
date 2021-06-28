package siren.ocean.recognize;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * 人脸识别器
 * Created by Siren on 2021/6/22.
 */
public class FaceRecognize {

    public final static int IMAGE_TYPE_NV21 = 0;
    public final static int IMAGE_TYPE_RGBA = 1;

    static {
        try {
            System.loadLibrary("recognize");
            Log.d("FaceRecognize", " success");
        } catch (UnsatisfiedLinkError e) {
            Log.e("FaceRecognize", "library not found");
        }
    }

    private FaceRecognize() {
    }

    private static FaceRecognize single = null;

    public static FaceRecognize getInstance() {
        if (single == null) {
            single = new FaceRecognize();
        }
        return single;
    }

    public int[] detectFace(byte[] imageData, int width, int height, int imageType) {
        synchronized ("FACE_DETECT") {//加锁保证线程安全
            return faceDetect(imageData, width, height, imageType);
        }
    }

    public float[] featureExtract(byte[] imageData, int width, int height, int[] faceInfo, int imageType) {
        synchronized ("EXTRACT_FEATURE") {//加锁保证线程安全
            return extractFeature(imageData, width, height, faceInfo, imageType);
        }
    }

    /**
     * 初始化
     *
     * @param assetManager
     * @return
     */
    public native boolean initModels(AssetManager assetManager);

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

    public native boolean faceDeInit();
}
