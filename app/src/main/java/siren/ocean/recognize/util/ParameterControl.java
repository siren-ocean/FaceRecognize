package siren.ocean.recognize.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import siren.ocean.recognize.R;
import siren.ocean.recognize.apapter.PhoneCamAdapter;
import siren.ocean.recognize.entity.CameraParameter;

/**
 * camera参数控制器
 * Created by Siren on 2022/1/20.
 */
public class ParameterControl {

    private static final String FRONT_CAMERA = "Front";
    private static final String BACK_CAMERA = "Back";

    private static final List<String> phoneCamData = new ArrayList<>(Arrays.asList(BACK_CAMERA, FRONT_CAMERA));
    private static final List<Integer> cameraIdData = Arrays.asList(CommonUtil.getCameraIds());
    private static final List<String> resolutionData = new ArrayList<>(Arrays.asList("640X480", "1280X720", "1280X960"));
    private static final List<Integer> anglesData = new ArrayList<>(Arrays.asList(0, 90, 180, 270));
    private static final List<Boolean> mirrorData = new ArrayList<>(Arrays.asList(true, false));

    private final CameraParameter mParameter;
    private final Activity mActivity;
    private Callback mCallback;
    private SpinnerCreator cameraIdCreator, resolutionCreator, orientationCreator, rotationCreator, mirrorCreator;
    private TextView tvPhoneCam;
    private PopupWindow popupWindow;

    public interface Callback {
        void onChangeParameter(CameraParameter parameter);
    }

    public ParameterControl(Activity activity, CameraParameter mParameter) {
        this.mParameter = mParameter;
        this.mActivity = activity;
        initPhoneCam();
        takeCreator();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    private void takeCreator() {
        takeCameraId();
        takeResolution();
        takeOrientation();
        takeRotation();
        takeMirror();
    }

    /**
     * 初始化手机摄像头参
     */
    private void initPhoneCam() {
        tvPhoneCam = mActivity.findViewById(R.id.tv_phone_cam);
        tvPhoneCam.setOnClickListener(v -> showPhoneCam());
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_pop, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        RecyclerView recyclerView = view.findViewById(R.id.rv_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        PhoneCamAdapter adapter = new PhoneCamAdapter(phoneCamData, this::choosePhoneCam);
        recyclerView.setAdapter(adapter);
        takePhoneCam();
    }

    private void choosePhoneCam(int position) {
        String value = phoneCamData.get(position);
        if (value.equals(FRONT_CAMERA)) {
            if (cameraIdData.size() > 1) {
                mParameter.setFrontCamera();
            }
        } else if (value.equals(BACK_CAMERA)) {
            mParameter.setBackCamera();
        }
        tvPhoneCam.setText(value);
        mCallback.onChangeParameter(mParameter);
        popupWindow.dismiss();
        takeCreator();
    }

    private void showPhoneCam() {
        popupWindow.showAsDropDown(tvPhoneCam, 0, -tvPhoneCam.getHeight());
    }

    /**
     * 设置手机摄像头显示设置
     */
    private void takePhoneCam() {
        if (mParameter.isBackCamera()) {
            tvPhoneCam.setText(phoneCamData.get(0));
        } else if (cameraIdData.size() > 0 && mParameter.isFrontCamera()) {
            tvPhoneCam.setText(phoneCamData.get(1));
        } else {
            tvPhoneCam.setText("?");
        }
    }

    /**
     * 设置摄像头参数
     */
    private void takeCameraId() {
        if (cameraIdCreator == null)
            cameraIdCreator = new SpinnerCreator<Integer>(mActivity, R.id.sp_camera_id)
                    .setData(cameraIdData)
                    .setCallback(value -> {
                        if (mParameter.getCameraId() == value) return;
                        mParameter.setCameraId(value);
                        mCallback.onChangeParameter(mParameter);
                        takePhoneCam();
                    });
        int position = mParameter.getCameraId();
        cameraIdCreator.setSelection(position);
    }

    /**
     * 设置分辨率
     */
    private void takeResolution() {
        if (resolutionCreator == null)
            resolutionCreator = new SpinnerCreator<String>(mActivity, R.id.sp_resolution)
                    .setData(resolutionData)
                    .setCallback(value -> {
                        String[] num = value.split("X");
                        int[] resolution = new int[]{Integer.parseInt(num[0]), Integer.parseInt(num[1])};
                        if (Arrays.equals(mParameter.getResolution(), resolution)) return;
                        mParameter.setResolution(resolution);
                        mCallback.onChangeParameter(mParameter);
                        takePhoneCam();
                    });
        int[] data = mParameter.getResolution();
        int position = resolutionData.indexOf(data[0] + "X" + data[1]);
        resolutionCreator.setSelection(position);
    }

    /**
     * 设置预览旋转角度
     */
    private void takeOrientation() {
        if (orientationCreator == null)
            orientationCreator = new SpinnerCreator<Integer>(mActivity, R.id.sp_orientation)
                    .setData(anglesData)
                    .setCallback(value -> {
                        if (mParameter.getOrientation() == value) return;
                        mParameter.setOrientation(value);
                        mCallback.onChangeParameter(mParameter);
                        takePhoneCam();
                    });
        int position = anglesData.indexOf(mParameter.getOrientation());
        orientationCreator.setSelection(position);
    }

    /**
     * 设置流旋转角度
     */
    private void takeRotation() {
        if (rotationCreator == null)
            rotationCreator = new SpinnerCreator<Integer>(mActivity, R.id.sp_rotation)
                    .setData(anglesData)
                    .setCallback(value -> {
                        if (mParameter.getRotation() == value) return;
                        mParameter.setRotation(value);
                        mCallback.onChangeParameter(mParameter);
                        takePhoneCam();
                    });
        int position = anglesData.indexOf(mParameter.getRotation());
        rotationCreator.setSelection(position);
    }

    /**
     * 设置流镜像
     */
    private void takeMirror() {
        if (mirrorCreator == null)
            mirrorCreator = new SpinnerCreator<Boolean>(mActivity, R.id.sp_mirror)
                    .setData(mirrorData)
                    .setCallback(value -> {
                        if (mParameter.isMirror() == value) return;
                        mParameter.setMirror(value);
                        mCallback.onChangeParameter(mParameter);
                        takePhoneCam();
                    });
        int position = mirrorData.indexOf(mParameter.isMirror());
        mirrorCreator.setSelection(position);
    }
}