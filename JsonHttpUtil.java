package com.wanfajie.test1201.util;

import android.util.Log;
import com.google.gson.Gson;
import okhttp3.*;

public class JsonHttpUtil {
    private static final String TAG = "JsonHttpUtil";

    public static final Gson gson = new Gson();
    public static final OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON_TYPE = MediaType.parse("application/json");

    public static void post(String url, Object data, Callback callback) {
        String bodyStr = "";

        if (data != null) {
            bodyStr = gson.toJson(data);
        }

        Log.d(TAG, url);
        Log.d(TAG, bodyStr);

        RequestBody body = RequestBody.create(JSON_TYPE, bodyStr);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request)
                .enqueue(callback);
    }
}
