package siren.ocean.yuv;

import android.util.Log;

/**
 * yuv实现流处理工具
 * Created by Siren on 2021/6/18.
 */
public class YuvUtil {

    static {
        try {
            System.loadLibrary("yuv_util");
            Log.d("jniNativeClassInit", " success");
        } catch (UnsatisfiedLinkError e) {
            Log.e("error", "library not found");
        }
    }

    /**
     * @param src      源数据
     * @param width    宽度
     * @param height   长度
     * @param rotation 旋转角度
     * @param isMirror 是否镜像
     * @param ratio    缩放值 0~1之间小数
     * @return
     */
    public static native byte[] nv21RotateMirror(byte[] src, int width, int height, int rotation, boolean isMirror, float ratio);
}
