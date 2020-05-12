package com.duanlu.module.base.api;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/********************************
 * @name ParamsInterceptor
 * @author 段露
 * @createDate 2019/05/15 16:17
 * @updateDate 2019/05/15 16:17
 * @version V1.0.0
 * @describe 添加公共参数拦截器.
 ********************************/
public abstract class ParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Map<String, String> params = getParams();

        if (null != params && !params.isEmpty()) {

            String method = request.method();

            if ("GET".equalsIgnoreCase(method)) {
                request = addGetMethodParams(request, params);
            } else if ("POST".equalsIgnoreCase(method) && request.body() instanceof FormBody) {
                request = addPostMethodParams(request, params);
            }
        }
        return chain.proceed(request);
    }

    private Request addGetMethodParams(@NonNull Request request, @NonNull Map<String, String> params) {
        HttpUrl.Builder builder = request.url().newBuilder();
        for (String key : params.keySet()) {
            builder.addQueryParameter(key, params.get(key));
        }
        return request.newBuilder().url(builder.build()).build();
    }

    private Request addPostMethodParams(@NonNull Request request, @NonNull Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        FormBody formBody = (FormBody) request.body();
        if (null != formBody) {
            int size = formBody.size();
            //添加原始参数.
            for (int i = 0; i < size; i++) {
                builder.add(formBody.encodedName(i), formBody.encodedValue(i));
            }
            //添加公共参数.
            String value;
            for (String key : params.keySet()) {
                value = params.get(key);
                if (null != value && !value.isEmpty()) {
                    builder.add(key, value);
                }
            }
        }
        return request.newBuilder().post(builder.build()).build();
    }

    protected abstract Map<String, String> getParams();

}
