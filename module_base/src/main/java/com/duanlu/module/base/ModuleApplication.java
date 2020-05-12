package com.duanlu.module.base;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import androidx.annotation.NonNull;

import com.duanlu.baseui.fragment.ShellActivity;
import com.duanlu.imageloader.ImageLoader;
import com.duanlu.imageloader.PlaceholderProvider;
import com.duanlu.module.base.crash.AppUncaughtExceptionHandler;
import com.duanlu.module.base.imageloader.GlideLoader;
import com.duanlu.module.base.receiver.NetworkChangeReceiver;
import com.duanlu.utils.AppFolderManager;
import com.duanlu.utils.CrashHandler;
import com.duanlu.utils.DisplayUtils;
import com.duanlu.utils.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by 段露 on 2019/3/8  10:37.
 *
 * @author DUANLU
 * @version 1.0.0
 * @class ModuleApplication
 * @describe Module Application.
 */
public abstract class ModuleApplication extends RootApplication {

    public static int sHeightPixels;//屏幕高度.
    public static int sWidthPixels;//屏幕宽度.

    @Override
    protected void init() {
        sHeightPixels = DisplayUtils.getScreenHeight(this);
        sWidthPixels = DisplayUtils.getScreenWidth(this);
    }

    @Override
    protected void configLoggerFactory() {
        LogUtils.init(isDebug()
                , false
                , LogUtils.V
                , AppFolderManager.getLogFolder().getAbsolutePath()
                , ""
                , true);
    }

    @Override
    protected CrashHandler.CustomUncaughtExceptionHandler makeUncaughtExceptionHandler() {
        return new AppUncaughtExceptionHandler();
    }

    @Override
    protected Class<? extends Activity> getShellActivity() {
        return ShellActivity.class;
    }

    @Override
    protected void registerNetworkChangedReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), filter);
    }

    @Override
    protected void configHttpClient() {
        OkGo.getInstance().setOkHttpClient(makeHttpClick());
    }

    @Override
    protected void configImageLoader() {
        //配置图片加载框架默认占位图.
        PlaceholderProvider.getInstance()
                .setDefault(R.drawable.resource_shape_image_loader_placeholder, R.drawable.resource_shape_image_loader_placeholder)
                .setCircleDefault(R.drawable.resource_shape_image_loader_placeholder_circle, R.drawable.resource_shape_image_loader_placeholder_circle)
                .setCornerDefault(R.drawable.resource_shape_image_loader_placeholder_corner, R.drawable.resource_shape_image_loader_placeholder_corner);
        //初始化图片加载框架.
        ImageLoader.init(new GlideLoader());
    }

    @NonNull
    @Override
    protected String getAuthority() {
        return getPackageName() + ".FileProvider";
    }

    private OkHttpClient makeHttpClick() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        addHttpInterceptor(builder);

        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        return builder.build();
    }

    /**
     * 添加网络请求拦截器.
     */
    protected void addHttpInterceptor(OkHttpClient.Builder builder) {
        //nothing.
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        new Thread(() -> {
            ImageLoader.clearDiskCache(getApplicationContext());
            System.gc();
        });
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
        //页面销毁时取消该页面所有网络请求.
        OkGo.getInstance().cancelTag(this);
    }

}