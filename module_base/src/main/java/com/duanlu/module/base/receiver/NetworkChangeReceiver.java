package com.duanlu.module.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.duanlu.module.base.event.NetworkStatusChangedEvent;
import com.duanlu.utils.LogUtils;
import com.duanlu.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

/********************************
 * @name NetworkChangeReceiver
 * @author 段露
 * @createDate 2017/09/06 13:42.
 * @updateDate 2017/09/06 13:42.
 * @version V1.0.0
 * @describe 监听网络变化广播.
 ********************************/
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (NetworkUtils.isConnected(context)) {//连接到网络.
                LogUtils.i(TAG, "-------------------------连接到网络---------------------------");
                EventBus.getDefault().post(
                        new NetworkStatusChangedEvent(
                                NetworkStatusChangedEvent.EVENT_CONNECTION));
            } else {//网络连接断开.
                LogUtils.i(TAG, "--------------------------网络连接断开--------------------------");
                EventBus.getDefault().post(
                        new NetworkStatusChangedEvent(
                                NetworkStatusChangedEvent.EVENT_UNCONNECTION));
            }
        }
    }

}