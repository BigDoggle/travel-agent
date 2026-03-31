package com.travel.agent.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private T data;

    private Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data);
    }

    /**
     * 成功返回（默认消息）
     */
    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return success("操作成功", null);
    }

    /**
     * 失败返回
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(false, message, null);
    }

    /**
     * 失败返回（带数据）
     */
    public static <T> Result<T> error(String message, T data) {
        return new Result<>(false, message, data);
    }
}
