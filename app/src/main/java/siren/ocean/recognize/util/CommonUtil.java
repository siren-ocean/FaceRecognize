package siren.ocean.recognize.util;

import android.content.Context;
import android.hardware.Camera;
import android.util.DisplayMetrics;

/**
 * 通用工具
 * Created by Siren on 2021/6/22.
 */
public class CommonUtil {

    /**
     * 获取摄像头id数组
     */
    public static Integer[] getCameraIds() {
        int number = Camera.getNumberOfCameras();
        if (number > 0) {
            Integer[] data = new Integer[number];
            for (int i = 0; i < number; i++) {
                data[i] = i;
            }
            return data;
        }
        return new Integer[1];
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
}
