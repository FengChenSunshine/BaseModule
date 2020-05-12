package com.duanlu.module.base.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.duanlu.baseui.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    public int getTitleResId() {
        return 0;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void getHttpData() {

    }

    @Override
    public void setHttpData() {

    }

}
