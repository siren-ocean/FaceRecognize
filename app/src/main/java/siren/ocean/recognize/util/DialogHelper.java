package siren.ocean.recognize.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * Dialog工具
 * Created by Siren on 2022/1/24.
 */
public class DialogHelper {

    public interface EnterCallback {
        void onConfirm(String name, float[] feature);
    }

    public static void showDialog(Activity activity, Bitmap avatar, float[] feature, EnterCallback callback) {
        EditText editText = new EditText(activity);
        editText.setSingleLine();
        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(avatar);
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 50, 50, 50);
        linearLayout.addView(imageView);
        linearLayout.addView(editText);

        new AlertDialog.Builder(activity)
                .setTitle("请输入姓名")
                .setView(linearLayout)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(activity, "姓名不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    callback.onConfirm(name, feature);
                }).show();
    }
}
