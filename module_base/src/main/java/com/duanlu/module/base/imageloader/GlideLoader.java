package com.duanlu.module.base.imageloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.duanlu.imageloader.ILoaderStrategy;
import com.duanlu.imageloader.LoaderOptions;
import com.duanlu.imageloader.Shape;

/**
 * Created by 枫尘 on 2019/3/6  20:04.
 *
 * @author DUANLU
 * @version 1.0.0
 * @class GlideLoader
 * @describe Glide加载器.
 */
public class GlideLoader implements ILoaderStrategy {

    @Override
    public void load(@NonNull LoaderOptions options) {
        RequestBuilder builder;
        RequestManager request = Glide.with(options.getContext());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(options.getPlaceholder())
                .error(options.getPlaceholder())
                .dontAnimate();

        if (options.mTargetWidth > 0 && options.mTargetHeight > 0) {
            requestOptions = requestOptions.override(options.mTargetWidth, options.mTargetHeight);
        }

        if (Shape.CIRCLE == options.mShape) {//圆形
            requestOptions = requestOptions.transform(new CircleCrop());
        } else if (Shape.CORNER == options.mShape) {//圆角
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(options.mCornerRadius));
        }
        if (options.mDrawableResId > 0) {
            builder = request.load(options.mDrawableResId);
            if (options.mTargetView instanceof ImageView) {
                ((ImageView) options.mTargetView).setScaleType(ImageView.ScaleType.FIT_XY);
                requestOptions = requestOptions.placeholder(options.mDrawableResId)
                        .error(options.mDrawableResId);
            }
        } else {
            builder = options.file != null ? request.load(options.file)
                    : (options.mPath == null || options.mPath.equals("")) ?
                    options.mUri != null ? request.load(options.mUri)
                            : request.load(options.getError())
                    : request.load(options.mPath);
        }
        if (options.mTargetView instanceof ImageView) {
            builder.apply(requestOptions)
                    .into((ImageView) options.mTargetView);
        }
    }

    public void clearMemoryCache(@NonNull Context context) {
        Glide.get(context).clearMemory();
    }

    public void clearDiskCache(@NonNull Context context) {
        Glide.get(context).clearDiskCache();
    }

    public void clearCache(@NonNull Context context, View targetView) {
        Glide.with(context).clear(targetView);
    }

}