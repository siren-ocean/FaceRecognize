package siren.ocean.recognize;

import android.app.Application;


/**
 * 全局Context
 * Created by Siren on 2021/6/18.
 */
public class AppContext extends Application {

    private static AppContext app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static AppContext get() {
        return app;
    }
}
