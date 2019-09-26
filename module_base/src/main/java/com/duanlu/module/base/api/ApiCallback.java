package com.duanlu.module.base.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.duanlu.api.ApiException;
import com.duanlu.api.BaseCallback;
import com.duanlu.baseui.activity.BaseActivity;
import com.duanlu.baseui.adapter.CommonLoadMoreLayout;
import com.duanlu.baseui.dialog.HintDialog;
import com.duanlu.utils.NetworkUtils;
import com.fengchen.uistatus.annotation.UiStatus;
import com.fengchen.uistatus.controller.IUiStatusController;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

/**
 * Created by 段露 on 2017/9/6 14:29.
 *
 * @author 段露
 * @version 2.0.0
 * @class ApiCallback
 * @describe 网络请求.
 */
public abstract class ApiCallback<T> extends BaseCallback<T> {

    private boolean isShowDialog;//是否显示进度框，true显示，false不显示，默认显示。
    private HintDialog mLoadingDialog;

    private IUiStatusController mUiStatusController;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommonLoadMoreLayout mLoadMoreLayout;

    public ApiCallback(Context context) {
        this(context, true, null, null, null);
    }

    public ApiCallback(Context context, boolean isShowDialog) {
        this(context, isShowDialog, null, null, null);
    }

    public ApiCallback(Context context, IUiStatusController uiStatusController) {
        this(context, false, uiStatusController, null, null);
    }

    public ApiCallback(Context context, IUiStatusController uiStatusController, SwipeRefreshLayout swipeRefreshLayout) {
        this(context, false, uiStatusController, swipeRefreshLayout, null);
    }

    public ApiCallback(Context context, IUiStatusController uiStatusController, CommonLoadMoreLayout loadMore) {
        this(context, false, uiStatusController, null, loadMore);
    }

    public ApiCallback(Context context, IUiStatusController uiStatusController, SwipeRefreshLayout swipeRefreshLayout, CommonLoadMoreLayout loadMore) {
        this(context, false, uiStatusController, swipeRefreshLayout, loadMore);
    }

    private ApiCallback(Context context, boolean isShowDialog, IUiStatusController uiStatusController, SwipeRefreshLayout swipeRefreshLayout, CommonLoadMoreLayout loadMore) {
        super(context);
        this.isShowDialog = isShowDialog;
        this.mUiStatusController = uiStatusController;
        this.mSwipeRefreshLayout = swipeRefreshLayout;
        this.mLoadMoreLayout = loadMore;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        if (!NetworkUtils.isConnected(mContext)) {
            OkGo.getInstance().cancelAll();
        } else if (isShowDialog) {
            showLoadingDialog();//网络请求前显示加载loading
        }
    }

    @Override
    public void onSuccess(Response<T> response) {

        this.onSuccess(response.body());

        if (null != mUiStatusController) {
            mUiStatusController.changeUiStatusIgnore(UiStatus.CONTENT);
        }
    }

    protected abstract void onSuccess(T resultBean);

    @Override
    public void onError(@NonNull ApiException exception) {
        if (null != mUiStatusController) {
            mUiStatusController.changeUiStatusIgnore(UiStatus.LOAD_ERROR);
        }
        if (null != mLoadMoreLayout) {
            mLoadMoreLayout.setLoadMoreError();
        }
        hideLoadingDialog();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        //网络请求结束后取消loading并释放资源.
        if (isShowDialog) hideLoadingDialog();
        //网络请求结束后刷新结束.
        if (null != mSwipeRefreshLayout) mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showLoadingDialog() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).showLoadingDialog();
        } else {
            mLoadingDialog = new HintDialog.Builder(mContext)
                    .setIconType(HintDialog.Builder.ICON_TYPE_LOADING)
                    .setMessage("加载中...")
                    .create();
            mLoadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).dismissLoadingDialog();
        } else if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

}