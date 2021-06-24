package siren.ocean.recognize;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import siren.ocean.recognize.util.FileUtils;

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
            Log.d("jniNativeClassInit", " success");
        } catch (UnsatisfiedLinkError e) {
            Log.e("error", "library not found");
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

    /**
     * 初始化模型路径
     *
     * @param context
     */
    public void initModels(Context context) {
        String det1Param = initPath(context, "det1.param");
        String det1Bin = initPath(context, "det1.bin");
        String det2Param = initPath(context, "det2.param");
        String det2Bin = initPath(context, "det2.bin");
        String det3Param = initPath(context, "det3.param");
        String det3Bin = initPath(context, "det3.bin");
        String mobilefacenetParam = initPath(context, "mobilefacenet.param");
        String mobilefacenetBin = initPath(context, "mobilefacenet.bin");

        String[] detectPath = new String[]{det1Param, det1Bin, det2Param, det2Bin, det3Param, det3Bin};
        String[] recognizePath = new String[]{mobilefacenetParam, mobilefacenetBin};
        initModels(detectPath, recognizePath);
    }

    private String initPath(Context context, String filename) {
        String targetPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + File.separator + filename;
        File file = new File(targetPath);
        if (!file.exists() || !file.isFile()) {
            FileUtils.copyAssetsFile(context, filename, file);
        }
        return file.getPath();
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

    public native boolean faceDeInit();
}
