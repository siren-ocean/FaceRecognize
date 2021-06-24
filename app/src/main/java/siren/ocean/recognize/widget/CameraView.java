package siren.ocean.recognize.widget;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.util.List;

import siren.ocean.recognize.util.ThreadUtil;


/**
 * 基于camera1预览
 * Created by Siren on 2021/6/17.
 */
public class CameraView extends TextureView {

    private final static String TAG = "CameraView";
    private Camera mCamera;
    public int mPreviewWidth = 640;
    public int mPreviewHeight = 480;
    private int orientation;
    private int cameraId;

    public CameraView(Context context) {
        this(context, null, 0);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void openCamera() {
        try {
            if (isAvailable()) {
                Log.d(TAG, "openCamera: ");
                releaseCamera();
                mCamera = Camera.open(cameraId);
                mCamera.setPreviewTexture(getSurfaceTexture());
                initParameter();
            }
        } catch (Exception e) {
            e.printStackTrace();
            reconnect();
        }
    }

    /**
     * 重连策略
     */
    private void reconnect() {
        ThreadUtil.runOnMainThreadDelayed(() -> {
            Log.d(TAG, "reconnect: ");
            openCamera();
        }, 2000);
    }

    /**
     * 设置自动对焦模式
     */
    private void initFocusModes() {
        try {
            Parameters parameters = mCamera.getParameters();
            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initParameter() {
        initFocusModes();
        initDisplayOrientation();
        initPreviewSize();
        initPreviewBuffer();
        mCamera.startPreview();
        requestLayout();
    }

    /**
     * 初始化预览尺寸大小
     */
    private void initPreviewSize() {
        Parameters parameters = mCamera.getParameters();
        Log.d("initPreviewSize: ", mPreviewWidth + ":::" + mPreviewHeight);
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight); // 设置预览分辨率
        mCamera.setParameters(parameters);
    }

    /**
     * 设置相机显示的方向
     */
    private void initDisplayOrientation() {
        mCamera.setDisplayOrientation(orientation);
    }

    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);
        int finalWidth, finalHeight;
        float scale = (float) mPreviewHeight / (float) mPreviewWidth;
        if (originalWidth < originalHeight) {
            finalWidth = originalWidth;
            finalHeight = (int) (finalWidth / scale);
        } else {
            finalWidth = originalWidth;
            finalHeight = (int) (finalWidth * scale);
        }
        setMeasuredDimension(finalWidth, finalHeight);
    }

    private Camera.PreviewCallback mPreviewCallback;
    public byte[] mPreviewBuffer;

    private void initPreviewBuffer() {
        mPreviewBuffer = new byte[mPreviewWidth * mPreviewHeight * 3 / 2]; // 初始化预览缓冲数据的大小
        mCamera.addCallbackBuffer(mPreviewBuffer); // 将此预览缓冲数据添加到相机预览缓冲数据队列里
        mCamera.setPreviewCallbackWithBuffer(mPreviewCallback); // 设置预览的回调
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }

    /**
     * 每次预览的回调中，需要调用这个方法才可以起到重用mBuffer
     */
    public void addCallbackBuffer() {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(mPreviewBuffer);
        }
    }

    public void setParameter(int cameraId, int[] resolution, int orientation) {
        this.cameraId = cameraId;
        this.mPreviewWidth = resolution[0];
        this.mPreviewHeight = resolution[1];
        this.orientation = orientation;
        openCamera();
    }
}