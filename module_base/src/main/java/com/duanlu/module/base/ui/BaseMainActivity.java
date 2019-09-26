package com.duanlu.module.base.ui;

import com.duanlu.baseui.activity.BaseActivity;
import com.duanlu.module.base.R;
import com.duanlu.utils.ToastUtils;

import java.text.MessageFormat;

/********************************
 * @name BaseMainActivity
 * @author 段露
 * @createDate 2019/05/29 18:17
 * @updateDate 2019/05/29 18:17
 * @version V1.0.0
 * @describe 应用主界面基类.
 ********************************/
public abstract class BaseMainActivity extends BaseActivity {

    private long mTimeStamp;
    protected static final int BACK_CRITICAL_VALUE = 2000;

    @Override
    public boolean useDefaultToolbar() {
        return false;
    }

    @Override
    public int getTitleResId() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mTimeStamp > BACK_CRITICAL_VALUE) {

            doHintBackPressedMessage();

            mTimeStamp = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    protected void doHintBackPressedMessage() {
        ToastUtils.showToastOnce(mContext, MessageFormat.format("再按一次退出{0}",
                mContext.getResources().getString(R.string.app_name)));
    }

}
