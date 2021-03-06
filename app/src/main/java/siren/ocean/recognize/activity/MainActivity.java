package siren.ocean.recognize.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.EasyPermissions;
import siren.ocean.recognize.FaceRecognize;
import siren.ocean.recognize.R;
import siren.ocean.recognize.entity.CameraParameter;
import siren.ocean.recognize.util.CommonUtil;
import siren.ocean.recognize.util.DialogHelper;
import siren.ocean.recognize.util.ParameterControl;
import siren.ocean.recognize.util.PhotoUtils;
import siren.ocean.recognize.util.PreferencesUtility;
import siren.ocean.recognize.util.SimilarUtil;
import siren.ocean.recognize.util.ThreadUtil;
import siren.ocean.recognize.widget.CameraView;

/**
 * 主页
 * Created by Siren on 2021/6/17.
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private CameraView mCameraView;
    private SurfaceHolder mSurfaceHolder;
    private ImageView ivPhoto;
    private TextView tvResult;
    private CameraParameter mParameter = PreferencesUtility.getCameraParameter();
    private final ExecutorService detectThread = ThreadUtil.getSingleThreadExecutor();//基于检测的单线程策略,尽可能实时绘制人脸框
    private final ExecutorService recognizeThread = ThreadUtil.getSingleThreadExecutor();//基于识别的单线程策略，耗时操作，同时需要计算相似度并匹配结果，独立出来的线程
    private final Map<String, float[]> memoryMap = new HashMap<>();//模拟内存管理
    private float ratio;
    private LinearLayout llGesture;
    private BottomSheetBehavior<LinearLayout> mSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPreview();
        initCameraParameter();
        requestPermission();
        FaceRecognize.getInstance().initModels(getAssets());
        ratio = CommonUtil.calculateBiasRatio(mParameter);
    }

    private void initView() {
        tvResult = findViewById(R.id.tv_result);
        ivPhoto = findViewById(R.id.iv_photo);
        mCameraView = findViewById(R.id.view_camera);
        initSheetBehavior();
        initGesture();
        initSurface();
    }

    private void initSheetBehavior() {
        mSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.include_bottom_sheet));
        mSheetBehavior.setHideable(false);
    }

    private void initGesture() {
        llGesture = findViewById(R.id.ll_gesture);
        llGesture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                llGesture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mSheetBehavior.setPeekHeight(llGesture.getMeasuredHeight());
            }
        });
    }

    private void initSurface() {
        SurfaceView surfaceView = findViewById(R.id.view_surface);
        surfaceView.setZOrderOnTop(true);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    private void initCameraParameter() {
        new ParameterControl(this, mParameter).setCallback(parameter -> {
            mParameter = parameter;
            updateParameter();
        });
    }

    private void updateParameter() {
        PreferencesUtility.setCameraParameter(mParameter);
        mCameraView.setParameter(mParameter);
        ratio = CommonUtil.calculateBiasRatio(mParameter);
    }

    private void initPreview() {
        mCameraView.setParameter(mParameter);
        mCameraView.setPreviewCallback((imageData, width, height) -> {
            detectFace(imageData, width, height);
            ivPhoto.setImageBitmap(PhotoUtils.nv21ToBitmap(this, imageData, width, height));
        });
    }

    private void detectFace(byte[] imageData, int w, int h) {
        detectThread.submit(() -> {
            int[] faceInfo = FaceRecognize.getInstance().detectFace(imageData, w, h, FaceRecognize.IMAGE_TYPE_NV21);
            if (faceInfo == null || faceInfo.length == 1) {
                surfaceDraw(null);
                showResult("", "");
                return;
            }
            surfaceDraw(faceInfo);
            featureMatch(imageData, w, h, faceInfo);
        });
    }

    private void surfaceDraw(int[] faceInfo) {
        PhotoUtils.surfaceDraw(mSurfaceHolder, faceInfo, ratio);
    }

    private void featureMatch(byte[] imageData, int w, int h, int[] faceInfo) {
        recognizeThread.submit(() -> {
            if (memoryMap.size() > 0) {
                float[] feature = FaceRecognize.getInstance().featureExtract(imageData, w, h, faceInfo, FaceRecognize.IMAGE_TYPE_NV21);
                float[] result = SimilarUtil.distanceArray(feature, getMemoryFeatures());
                int index = (int) result[0];
                String name = getNameByIndex(index);
                showResult(name, String.valueOf(result[1]));
            }
        });
    }

    /**
     * 显示最相似结果和相似度
     */
    private void showResult(String name, String similarity) {
        runOnUiThread(() -> tvResult.setText(name + "\n" + similarity));
    }

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

    private String getNameByIndex(int index) {
        Iterator<Map.Entry<String, float[]>> iterator = memoryMap.entrySet().iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next().getKey();
    }

    /**
     * 此处通过TextureView获取bitmap的功能测试不同图片数据类型的处理
     */
    public void takePicture(View view) {
        Bitmap sourceBitmap = mCameraView.getBitmap();
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        byte[] imageData = PhotoUtils.getPixelsRGBA(sourceBitmap);
        int[] faceInfo = FaceRecognize.getInstance().detectFace(imageData, width, height, FaceRecognize.IMAGE_TYPE_RGBA);
        if (faceInfo != null && faceInfo.length > 1) {
            float[] feature = FaceRecognize.getInstance().featureExtract(imageData, width, height, faceInfo, FaceRecognize.IMAGE_TYPE_RGBA);
            Bitmap avatar = PhotoUtils.getAvatar(sourceBitmap, faceInfo);
            DialogHelper.showDialog(this, avatar, feature, memoryMap::put);
        } else {
            Toast.makeText(this, getText(R.string.no_face_be_detection), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ThreadUtil.runOnMainThreadDelayed(() -> mCameraView.openCamera(), 300);
        } else {
            EasyPermissions.requestPermissions(this, "The app must have the permission of cameras", 0, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mCameraView.openCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onResume() {
        mCameraView.openCamera();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCameraView.releaseCamera();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detectThread.shutdown();
        recognizeThread.shutdown();
        FaceRecognize.getInstance().faceDeInit();
    }
}