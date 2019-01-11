package com.transform.demo.okhttp;

import okhttp3.Response;

/**
 * Created by wangyan on 2019/1/10
 */
public interface IResultParser {
    Object parse(Response response, String reponseBody) throws Exception;
}
