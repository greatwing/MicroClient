package com.github.greatwing;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.CookieSyncManager;

import java.lang.ref.WeakReference;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;

public class MainActivity extends Activity {

    private ViewGroup mViewParent;
    private X5WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        mTestHandler = new MHandler(this);
        mTestHandler.sendEmptyMessageDelayed(MSG_INIT_VIEW, 10);

        // 常亮状态
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 19) {
            // 导航栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 标题栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        hideVirtualButton();

        // 防止虚拟键盘出现后导航栏重新显示
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    hideVirtualButton();
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected void hideVirtualButton() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // 全屏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void initView() {
        mViewParent = (ViewGroup) findViewById(R.id.viewGroup);

        mWebView = new X5WebView(this, null);
        mViewParent.addView(mWebView, 0, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        //加载游戏
        String url = getResources().getString(R.string.clientaddr);
        mWebView.loadUrl(url);
        WebViewCacheInterceptorInst.getInstance().loadUrl(url,mWebView.getSettings().getUserAgentString());

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        mTestHandler.sendEmptyMessageDelayed(MSG_SWITCH_UI, 1000);
    }

    protected void removeFlashScene() {
        View flash = findViewById(R.id.flashView);
        if(flash != null) {
            mViewParent.removeView(flash);
        }
    }

    public static final int MSG_INIT_VIEW = 1;
    public static final int MSG_SWITCH_UI = 2;
    private static MHandler mTestHandler = null;
    static class MHandler extends Handler {
        WeakReference<MainActivity> outerClass;

        MHandler(MainActivity activity) {
            outerClass = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            MainActivity theClass = outerClass.get();
            switch (msg.what) {
                case MSG_INIT_VIEW:
                    theClass.initView();
                    break;
                case MSG_SWITCH_UI:
                    theClass.removeFlashScene();
                    break;
            }
        }
    }
}
