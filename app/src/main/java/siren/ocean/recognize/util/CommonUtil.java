package siren.ocean.recognize.util;

import android.content.Context;
import android.hardware.Camera;
import android.util.DisplayMetrics;

import java.text.DecimalFormat;

import siren.ocean.recognize.AppContext;
import siren.ocean.recognize.entity.CameraParameter;

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

    /**
     * 计算缩放比例，因为分辨率Resolution改变时，预览界面需要相对于屏幕做缩放或扩增
     *
     * @param parameter
     * @return
     */
    public static float calculateBiasRatio(CameraParameter parameter) {
        int width = getScreenWidth(AppContext.get());
        int height = getScreenHeight(AppContext.get());
        float bias = width > height ? (width / (float) parameter.getResolution()[0]) : (width / (float) parameter.getResolution()[1]);
        DecimalFormat format = new DecimalFormat(".0000");
        return Float.parseFloat(format.format(bias));
    }
}
