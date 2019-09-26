package com.duanlu.module.base.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.duanlu.baseui.activity.BaseActivity;
import com.duanlu.utils.PreferencesManager;
import com.fengchen.uistatus.annotation.UiStatus;

/********************************
 * @name SplashActivity
 * @author 段露
 * @createDate 2019/05/30 09:40
 * @updateDate 2019/05/30 09:40
 * @version V1.0.0
 * @describe 闪屏页基类.
 ********************************/
public abstract class BaseSplashActivity extends BaseActivity {

    /**
     * SP存储是否导航到主界面的key.
     */
    private static final String SP_KEY_IS_NAVIGATION_MAIN = "navigation_main";

    private CountDownTimer mCountDownTimer;

    @Override
    public final boolean useDefaultToolbar() {
        return false;
    }

    @Override
    public final int getTitleResId() {
        return 0;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void getHttpData() {
        setHttpData();
    }

    @Override
    public void setHttpData() {
        mUiStatusController.changeUiStatusIgnore(UiStatus.CONTENT);
    }

    /**
     * 跳过倒计时更新.
     *
     * @param second 剩余秒数.
     */
    protected abstract void countDownTick(int second);

    /**
     * 页面打开后调用该方法开始跳过倒计时.
     *
     * @param second 倒计时秒数.
     */
    protected void startSkipCountDown(int second) {
        countDown(second);
    }

    /**
     * 跳过按钮点击后调用该方法执行跳过动作.
     */
    protected void performSkip() {
        cancelCountDown();
        dispatchNavigation(true);
    }

    /**
     * 导航.
     *
     * @param isSkip           true跳过触发,false倒计时完成触发.
     * @param isNavigationMain true导航到主界面,false导航到引导页.
     */
    protected abstract void navigation(boolean isSkip, boolean isNavigationMain);

    private void dispatchNavigation(boolean isSkip) {
        boolean isNavigationMain = PreferencesManager.getInstance(mContext).get(SP_KEY_IS_NAVIGATION_MAIN, false);
        if (!isNavigationMain) {//第一次为false导航到引导页.
            PreferencesManager.getInstance(mContext).put(SP_KEY_IS_NAVIGATION_MAIN, true);
        }
        navigation(isSkip, isNavigationMain);
    }

    private void countDown(int second) {
        if (null != mCountDownTimer) return;

        mCountDownTimer = new CountDownTimer(second * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTick(Math.round((float) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                dispatchNavigation(false);
            }
        };
        mCountDownTimer.start();
    }

    private void cancelCountDown() {
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        cancelCountDown();
        super.onDestroy();
    }

}