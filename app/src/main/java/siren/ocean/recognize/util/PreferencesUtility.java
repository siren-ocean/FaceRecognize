package siren.ocean.recognize.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import siren.ocean.recognize.AppContext;
import siren.ocean.recognize.entity.CameraParameter;

/**
 * sharePreferences工具
 * Created by Siren on 2021/6/17.
 */
public class PreferencesUtility {
    private final static String CAMERA_PARAMETER = "camera_parameter";

    static {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(AppContext.get());
    }

    private static SharedPreferences mPreferences;

    public static CameraParameter getCameraParameter() {
        CameraParameter parameter = null;
        try {
            String data = mPreferences.getString(CAMERA_PARAMETER, "");
            if (TextUtils.isEmpty(data)) {
                return new CameraParameter(0, new int[]{640, 480}, 0, 0, false);
            }
            parameter = new Gson().fromJson(data, CameraParameter.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parameter;
    }

    public static void setCameraParameter(CameraParameter parameter) {
        try {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(CAMERA_PARAMETER, new Gson().toJson(parameter, CameraParameter.class));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}