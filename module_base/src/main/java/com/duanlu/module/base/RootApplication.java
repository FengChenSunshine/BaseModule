package com.duanlu.module.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.duanlu.router.UiPage;
import com.duanlu.utils.AppFolderManager;
import com.duanlu.utils.AppForegroundBackgroundManager;
import com.duanlu.utils.CrashHandler;
import com.duanlu.utils.StackManager;

/********************************
 * @name RootApplication
 * @author 段露
 * @createDate 2019/03/07  11:15.
 * @updateDate 2019/03/07  11:15.
 * @version V1.0.0
 * @describe 项目根Application.
 ********************************/
public abstract class RootApplication extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public final void onCreate() {
        super.onCreate();
        initialize();
        init();
    }

    private void initialize() {
        //初始化文件管理器.
        AppFolderManager.init(getRootFolderName(), getAuthority());

        configLoggerFactory();

        //初始化全局未铺货异常处理器.
        if (useCustomUncaughtExceptionHandler()) {
            CrashHandler.getInstance().init(this, makeUncaughtExceptionHandler());
        }

        //初始化应用程序前后台改变监听器.
        AppForegroundBackgroundManager.init(this, this::appForegroundBackgroundChanged);

        //初始化一个简单的路由导航.
        UiPage.init(getShellActivity());

        //初始化Activity栈管理器.
        StackManager.init(this);

        //注册Activity生命周期感知组件.
        registerActivityLifecycleCallbacks(this);

        //网络状态监听.
        registerNetworkChangedReceiver();

        //配置Http客户端.
        configHttpClient();

        //配置图片加载框架.
        configImageLoader();
    }

    /**
     * 是否启用自定义的未知异常捕获器.
     *
     * @return true启用, false不启用.默认非debug模式时启用.
     */
    protected boolean useCustomUncaughtExceptionHandler() {
        return !isDebug();
    }

    protected abstract void init();

    protected abstract boolean isDebug();

    /**
     * 初始化日志工具类.
     */
    protected abstract void configLoggerFactory();

    /**
     * 创建全局未铺货异常处理器.
     */
    protected abstract CrashHandler.CustomUncaughtExceptionHandler makeUncaughtExceptionHandler();

    /**
     * 获取路由导航需要的承载Fragment的壳Activity的Class对象.
     */
    protected abstract Class<? extends Activity> getShellActivity();

    /**
     * 注册网络状态改变监听广播(7.0后需要动态注册才可以监听到).
     */
    protected abstract void registerNetworkChangedReceiver();

    /**
     * 配置Http客户端.
     */
    protected abstract void configHttpClient();

    /**
     * 配置图片加载框架.
     */
    protected abstract void configImageLoader();

    /**
     * 获取项目文件存储根文件名.
     *
     * @return 项目文件存储根文件名.
     */
    @NonNull
    protected abstract String getRootFolderName();

    @NonNull
    protected abstract String getAuthority();

    /**
     * 应用程序前后台改变回调.
     *
     * @param isForeground true表示应用程序切换到前台,false后台.
     */
    protected void appForegroundBackgroundChanged(boolean isForeground) {
        //nothing.
    }

    //////////////////////////////////////////////////////////////////////////////
    //////////////////--------ActivityLifecycleCallbacks--------//////////////////
    //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //nothing.
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //nothing.
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //nothing.
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //nothing.
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //nothing.
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //nothing.
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //nothing.
    }

}
