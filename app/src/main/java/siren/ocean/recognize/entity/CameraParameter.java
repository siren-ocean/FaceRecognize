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
