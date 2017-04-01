package com.larkin.web.http;

import com.beust.jcommander.internal.Maps;
import com.larkin.web.http.exception.ApiError;

public class ApiResponseBody {
    //-------------------------------------------------------------
    // Variables - Private
    //-------------------------------------------------------------
    private Result result = Result.success;
    private int code = 200;
    private Object info = new Object();
    private String[] errorMsg = new String[0];

    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------
    public ApiResponseBody(final Result result, final int code, final String info) {
        this.result = result;
        this.code = code;
        this.info = (null == info) ? Maps.newHashMap() : new String[]{info};
    }

    private ApiResponseBody(final Result result, final int code, final String info, final String[] errorMsg) {
        this(result, code, info);
        this.errorMsg = (null == errorMsg) ? new String[0] : errorMsg;
    }

    public ApiResponseBody(final Object info) {
        this.info = (null == info) ? new Object[0] : info;
    }

    public ApiResponseBody(final ApiError apiError) {
        this(Result.fail, apiError.getCode(), null, new String[]{apiError.getMessage()});
    }

    public ApiResponseBody(final ApiError apiError, final String errorMsg) {
        this(apiError);
        this.errorMsg = (null == errorMsg) ? new String[]{apiError.getMessage()} : new String[]{apiError.getMessage(), errorMsg};
    }

    public String toString(){
        return null;
    }

    //-------------------------------------------------------------
    // Methods - Getter/Setter
    //-------------------------------------------------------------
    public Result getResult() {
        return result;
    }
    public void setResult(final Result result) {
        this.result = result;
    }
    public String getCode() {
        return String.valueOf(code);
    }
    public void setCode(final int code) {
        this.code = code;
    }
    public Object getInfo() {
        return info;
    }
    public void setInfo(final Object info) {
        this.info = info;
    }
    public String[] getErrorMsg() {
        return this.errorMsg;
    }
    public void setErrorMsg(final String[] errorMsg) {
        this.errorMsg = errorMsg;
    }

    //-------------------------------------------------------------
    // Inner Class
    //-------------------------------------------------------------
    public static enum Result {
        success, fail;
    }
}