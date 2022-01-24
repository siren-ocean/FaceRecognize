package siren.ocean.recognize.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import siren.ocean.recognize.R;
import siren.ocean.recognize.entity.CameraParameter;

/**
 * camera参数控制器
 * Created by Siren on 2022/1/20.
 */
public class ParameterControl {

    private static final List<String> resolutionData = new ArrayList<>(Arrays.asList("640X480", "1280X720", "1280X960"));
    private static final List<Integer> anglesData = new ArrayList<>(Arrays.asList(0, 90, 180, 270));
    private static final List<Boolean> mirrorData = new ArrayList<>(Arrays.asList(true, false));

    private final CameraParameter mParameter;
    private final Activity mActivity;
    private Callback mCallback;

    public interface Callback {
        void onChangeParameter(CameraParameter parameter);
    }

    public ParameterControl(Activity activity, CameraParameter mParameter) {
        this.mParameter = mParameter;
        this.mActivity = activity;
    }

    public void build(Callback callback) {
        this.mCallback = callback;
        initCameraId();
        initResolution();
        initOrientation();
        initRotation();
        initMirror();
    }

    private void initCameraId() {
        int position = mParameter.getCameraId();
        new SpinnerCreator<Integer>().build(mActivity, R.id.sp_camera_id, Arrays.asList(CommonUtil.getCameraIds()), position, value -> {
            if (mParameter.getCameraId() == value) return;
            mParameter.setCameraId(value);
            mCallback.onChangeParameter(mParameter);
        });
    }

    private void initResolution() {
        int[] data = mParameter.getResolution();
        int position = resolutionData.indexOf(data[0] + "X" + data[1]);
        new SpinnerCreator<String>().build(mActivity, R.id.sp_resolution, resolutionData, position, value -> {
            String[] num = value.split("X");
            int[] resolution = new int[]{Integer.parseInt(num[0]), Integer.parseInt(num[1])};
            if (Arrays.equals(mParameter.getResolution(), resolution)) return;
            mParameter.setResolution(resolution);
            mCallback.onChangeParameter(mParameter);
        });
    }

    private void initOrientation() {
        int position = anglesData.indexOf(mParameter.getOrientation());
        new SpinnerCreator<Integer>().build(mActivity, R.id.sp_orientation, anglesData, position, value -> {
            if (mParameter.getOrientation() == value) return;
            mParameter.setOrientation(value);
            mCallback.onChangeParameter(mParameter);
        });
    }

    private void initRotation() {
        int position = anglesData.indexOf(mParameter.getRotation());
        new SpinnerCreator<Integer>().build(mActivity, R.id.sp_rotation, anglesData, position, value -> {
            if (mParameter.getRotation() == value) return;
            mParameter.setRotation(value);
            mCallback.onChangeParameter(mParameter);
        });
    }

    private void initMirror() {
        int position = mirrorData.indexOf(mParameter.isMirror());
        new SpinnerCreator<Boolean>().build(mActivity, R.id.sp_mirror, mirrorData, position, value -> {
            if (mParameter.isMirror() == value) return;
            mParameter.setMirror(value);
            mCallback.onChangeParameter(mParameter);
        });
    }
}