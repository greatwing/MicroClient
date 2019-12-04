package com.github.greatwing;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        WebViewCacheInterceptor.Builder builder =  new WebViewCacheInterceptor.Builder(this);
        builder.setCacheSize(2*1024*1024*1024);//设置缓存大小,2G
        builder.setDebug(true);

        CacheExtensionConfig extension = new CacheExtensionConfig();
        extension
                .addExtension("mp3")
                .addExtension("json")
                .addExtension("lua")
                .addExtension("atlas")
                .removeExtension("html");
        builder.setCacheExtensionConfig(extension);

        WebViewCacheInterceptorInst.getInstance().init(builder);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }
            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }
}
