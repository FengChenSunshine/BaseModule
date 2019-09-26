package com.duanlu.module.base.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.duanlu.utils.AppFolderManager;
import com.duanlu.utils.CrashHandler;
import com.duanlu.utils.DateUtils;
import com.duanlu.utils.FileUtils;
import com.duanlu.utils.LogUtils;
import com.duanlu.utils.StackManager;
import com.duanlu.utils.ToastUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/********************************
 * @name AppUncaughtExceptionHandler
 * @author 段露
 * @createDate 2019/06/20 10:44.
 * @updateDate 2019/06/20 10:44.
 * @version V1.0.0
 * @describe 自定义全局未捕获异常处理器.
 ********************************/
public class AppUncaughtExceptionHandler implements CrashHandler.CustomUncaughtExceptionHandler {

    private static final String TAG = AppUncaughtExceptionHandler.class.getSimpleName();

    @Override
    public boolean handleMainThreadException(Context applicationContext, Thread thread, Throwable e) {
        return handleException(applicationContext, thread, e);
    }

    @Override
    public boolean handleOtherThreadException(Context applicationContext, Thread thread, Throwable e) {
        return handleException(applicationContext, thread, e);
    }

    private boolean handleException(Context applicationContext, Thread thread, Throwable e) {
        toast(applicationContext);
        StackManager.getInstance().finishActivity();

        if (null != e) {
            Map<String, String> deviceInfoMap = collectDeviceInfo(applicationContext);
            //保存日志文件
            String filePath = saveCrashInfo2File(e, deviceInfoMap);
        }
        return true;
    }

    private void toast(final Context applicationContext) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToastOnce(applicationContext, "未知错误");
            }
        });
    }

    /**
     * 收集设备参数信息
     */
    private Map<String, String> collectDeviceInfo(Context ctx) {
        //用来存储设备信息和异常信息
        Map<String, String> infoMap = new HashMap<>();
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infoMap.put("versionName", versionName);
                infoMap.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.i(TAG, e.getLocalizedMessage());
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infoMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                LogUtils.i(TAG, e.getLocalizedMessage());
            }
        }
        return infoMap;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param e 错误异常.
     * @return 返回文件路径, 便于将文件传送到服务器.
     */
    private String saveCrashInfo2File(Throwable e, Map<String, String> infoMap) {

        StringBuilder sb = new StringBuilder();
        //设备信息.
        if (null != infoMap) {
            for (Map.Entry<String, String> entry : infoMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key).append("=").append(value).append("\n");
            }
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        String time = DateUtils.date2Str(new Date(), "yyyy_MM_dd_HH_mm_ss");
        String fileName = "crash-" + time + "-log.txt";

        File file = new File(AppFolderManager.getLogFolder(), fileName);

        FileUtils.writeFileFromString(file, sb.toString(), true);

        return file.getAbsolutePath();
    }

}
