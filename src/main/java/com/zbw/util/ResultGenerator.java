package com.zbw.util;


import com.zbw.constants.HttpStatusEnum;
import com.zbw.pojo.dto.Result;

/**
 * 响应结果生成工具
 **/
public class ResultGenerator {

    private ResultGenerator() {
    }

    public static <T> Result<T> getResultByHttp(HttpStatusEnum constants, String msg, T data) {
        Result<T> result = new Result<>();
        result.setResultCode(constants.getStatus());
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> getResultByHttp(HttpStatusEnum constants, T data) {
        Result<T> result = new Result<>();
        result.setResultCode(constants.getStatus());
        result.setMessage(constants.getContent());
        result.setData(data);
        return result;
    }

    /**
     * 自定义提示消息
     * @param constants Http枚举
     * @param msg 提示消息
     */
    public static Result<String> getResultByMsg(HttpStatusEnum constants, String msg) {
        Result<String> result = new Result<>();
        result.setResultCode(constants.getStatus());
        result.setMessage(msg);
        return result;
    }

    /**
     * @Description: 根据传入的常量返回对应result
     * @Param: [constants] http状态
     */
    public static Result<String> getResultByHttp(HttpStatusEnum constants) {
        Result<String> result = new Result<>();
        result.setResultCode(constants.getStatus());
        result.setMessage(constants.getContent());
        return result;
    }

    //用于抛出异常
    public static Result<String> getFailResult(Integer code, String msg){
        Result<String> res = new Result<>();
        res.setResultCode(code);
        res.setMessage(msg);
        return res;
    }

}
