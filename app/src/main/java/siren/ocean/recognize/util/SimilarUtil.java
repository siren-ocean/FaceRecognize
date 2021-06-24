package siren.ocean.recognize.util;


/**
 * 获取相似度工具
 * Created by Siren on 2021/6/23.
 */

public class SimilarUtil {

    /**
     * 特征比对：1：N
     * 算出最高的相似度以及相对应的index
     */
    public static float[] distanceArray(float[] p, float[][] array) {
        int index = 0;
        float value = 0;
        float res;
        for (int i = 0; i < array.length; i++) {
            res = distance(p, array[i]);
            if (res > value) {
                value = res;
                index = i;
            }
        }
        return new float[]{index, value};
    }

    /**
     * 特征比对：1：1
     * 计算两者相似度
     */
    public static float distance(float[] p1, float[] p2) {
        float rss = 0;
        float square = 0;
        float sq = 0;

        for (int j = 0; j < p2.length; j++) {
            sq += p1[j] * p1[j];
            square += p2[j] * p2[j];
            rss += p2[j] * p1[j];
        }
        return rss / (float) (Math.sqrt(square) * Math.sqrt(sq));
    }
}
