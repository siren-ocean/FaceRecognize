package siren.ocean.recognize.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件处理工具
 * Created by Siren on 2021/6/22.
 */
public class FileUtils {

    /**
     * 复制assets文件
     *
     * @param context
     * @param name
     * @param to
     */
    public static void copyAssetsFile(Context context, String name, File to) {
        InputStream in = null;
        try {
            in = context.getResources().getAssets().open(name);
            write(to, in, false);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in == null) return;
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写入文件流
     *
     * @param file
     * @param in
     * @param append
     * @throws IOException
     */
    public static void write(File file, InputStream in, boolean append) throws IOException {
        OutputStream out = new FileOutputStream(file, append);
        try {
            byte data[] = new byte[1024];
            int length;
            while ((length = in.read(data)) != -1) {
                out.write(data, 0, length);
            }
            out.flush();
        } finally {
            out.close();
        }
    }
}
