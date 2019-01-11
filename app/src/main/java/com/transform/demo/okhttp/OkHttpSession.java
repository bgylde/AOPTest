package com.transform.demo.okhttp;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wangyan on 2019/1/10
 */
public class OkHttpSession {

    // 服务器返回的json里的code，非httpCode
    private int code;
    // 服务器返回的json里的message
    private String msg;
    private int errorCode = 0;
    private String json;
    private Request request;
    private Response response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
