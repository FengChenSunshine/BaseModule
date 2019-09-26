package com.duanlu.module.base.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/********************************
 * @name NetworkStatusChangedEvent
 * @author 段露
 * @createDate 2017/09/06 13:41.
 * @updateDate 2017/09/06 13:41.
 * @version V1.0.0
 * @describe 网络变化事件.
 ********************************/
public final class NetworkStatusChangedEvent implements Event {

    private int mEvent;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EVENT_UNCONNECTION, EVENT_CONNECTION})
    public @interface Event {

    }

    public static final int EVENT_UNCONNECTION = -1;//没有网络
    public static final int EVENT_CONNECTION = 1;//网络连接

    public NetworkStatusChangedEvent(@Event int event) {
        this.mEvent = event;
    }

    public int getEvent() {
        return mEvent;
    }

}
