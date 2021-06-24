package siren.ocean.recognize.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicYuvToRGB;
import androidx.renderscript.Type;


/**
 * 图片工具
 * Created by Siren on 2021/6/18.
 */
public class PhotoUtils {
    private static final Paint faceBoundPaint = new Paint();
    private static final Paint keyPointPaint = new Paint();

    static {
        faceBoundPaint.setColor((Color.WHITE));
        faceBoundPaint.setStyle(Paint.Style.STROKE);
        faceBoundPaint.setStrokeWidth(6);
        keyPointPaint.setColor((Color.WHITE));
        keyPointPaint.setStyle(Paint.Style.FILL);
    }

    public static Bitmap nv21ToBitmap(Context context, byte[] data, int width, int height) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(data);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);
        return bitmap;
    }

    /**
     * bitmap转rgba
     *
     * @param image
     * @return
     */
    public static byte[] getPixelsRGBA(Bitmap image) {
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        image.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    /**
     * 获取头像
     */
    public static Bitmap getAvatar(Bitmap res, int[] faceInfo) {
        if (faceInfo == null) return res;
        int width = faceInfo[3] - faceInfo[1];
        int span = width / 5;
        int left = Math.max(faceInfo[1] - span, 0);
        int right = Math.min(faceInfo[3] + span, res.getWidth());
        int top = Math.max(faceInfo[2] - (int) (span * 1.5f), 0);
        int bottom = Math.min(faceInfo[4] + (int) (span * 0.5f), res.getHeight());
        return Bitmap.createBitmap(res, left, top, right - left, bottom - top);
    }

    /**
     * 绘制surfaceView
     */
    public static void surfaceDraw(SurfaceHolder surfaceHolder, int[] faceInfo, float ratio) {
        ThreadUtil.handler().post(() -> {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvasDraw(canvas, faceInfo, ratio);
            surfaceHolder.unlockCanvasAndPost(canvas);
        });
    }

    private static void canvasDraw(Canvas canvas, int[] faceInfo, float ratio) {
        if (faceInfo == null || faceInfo.length == 1) return;
        Rect dst = getBound(faceInfo, ratio);
        canvas.drawRect(dst, faceBoundPaint);
        for (int i = 0; i < 5; i++) {
            int pointX = (int) (faceInfo[i + 5] * ratio);
            int pointY = (int) (faceInfo[i + 10] * ratio);
            canvas.drawCircle(pointX, pointY, 6, keyPointPaint);
        }
    }

    private static Rect getBound(int[] faceInfo, float ratio) {
        Rect bounds = new Rect();
        bounds.left = (int) (faceInfo[1] * ratio);
        bounds.top = (int) (faceInfo[2] * ratio);
        bounds.right = (int) (faceInfo[3] * ratio);
        bounds.bottom = (int) (faceInfo[4] * ratio);
        return bounds;
    }
}