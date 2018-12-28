package com.wanfajie.test1201.util;

public interface JsonCallback {
    void onJson(Object data) throws Exception;
    void onFailure(Exception e);
}
