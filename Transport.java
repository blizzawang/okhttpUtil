package com.wanfajie.test1201.util;

import android.os.Handler;
import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class Transport {
    private static final String TAG = "Transport";
    private static Transport instance;

    private String address = "192.168.1.108:8890";
    private Handler handler = new Handler();

    private Transport() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void fetch(String uri, Object data, TypeToken type, JsonCallback cb, boolean async) {

        if (address == null || address.isEmpty()) {
            cb.onFailure(new IllegalStateException("请设置服务器地址"));
            Log.e(TAG, "请设置服务器地址");
            return;
        }

        String url = "http://" + address + uri;

        JsonHttpUtil.post(url, data, wrapCallback(cb, type, async));
    }

    private Callback wrapCallback(final JsonCallback cb, final TypeToken type, final boolean async) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage(), e);
                cb.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respBody = response.body().string();

                Log.d(TAG, "Response Body: " + respBody);

                final Object json;

                try {
                    json = JsonHttpUtil.gson.fromJson(respBody, type.getType());
                } catch (JsonSyntaxException e) {
                    callFailure(cb, e, async);
                    return;
                }

                if (async) {

                    try {
                        cb.onJson(json);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                        cb.onFailure(e);
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cb.onJson(json);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                                cb.onFailure(e);
                            }
                        }
                    });
                }
            }
        };
    }

    private void callFailure(final JsonCallback cb, final Exception e, boolean async) {
        Log.e(TAG, e.getMessage(), e);
        if (async) {
            cb.onFailure(e);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    cb.onFailure(e);
                }
            });
        }
    }

    public static Transport getInstance() {
        if (instance == null) {

            synchronized (Transport.class) {

                if (instance == null) {
                    instance = new Transport();
                }
            }
        }

        return instance;
    }
}
