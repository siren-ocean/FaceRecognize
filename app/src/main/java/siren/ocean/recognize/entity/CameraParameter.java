package siren.ocean.recognize.entity;

import java.util.Arrays;

/**
 * 摄像头参数
 * Created by Siren on 2021/6/18.
 */
public class CameraParameter {
    private int cameraId;
    private int[] resolution;
    private int orientation;
    private int rotation;
    private boolean isMirror;

    public CameraParameter(int cameraId, int[] resolution, int orientation, int rotation, boolean isMirror) {
        this.cameraId = cameraId;
        this.resolution = resolution;
        this.orientation = orientation;
        this.rotation = rotation;
        this.isMirror = isMirror;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int[] getResolution() {
        return resolution;
    }

    public void setResolution(int[] resolution) {
        this.resolution = resolution;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public boolean isMirror() {
        return isMirror;
    }

    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    public boolean isFrontCamera() {
        return cameraId == 1 && rotation == 270 && orientation == 90 && isMirror;
    }

    /**
     * 设置为手机前置摄像头标准参数
     */
    public void setFrontCamera() {
        this.cameraId = 1;
        this.rotation = 270;
        this.orientation = 90;
        this.isMirror = true;
    }

    public boolean isBackCamera() {
        return cameraId == 0 && rotation == 90 && orientation == 90 && !isMirror;
    }

    /**
     * 设置为手机后置摄像头标准参数
     */
    public void setBackCamera() {
        this.cameraId = 0;
        this.rotation = 90;
        this.orientation = 90;
        this.isMirror = false;
    }

    @Override
    public String toString() {
        return "CameraParameter{" +
                "cameraId=" + cameraId +
                ", resolution=" + Arrays.toString(resolution) +
                ", orientation=" + orientation +
                ", rotation=" + rotation +
                ", isMirror=" + isMirror +
                '}';
    }
}
