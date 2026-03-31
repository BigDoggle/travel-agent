package com.travel.agent.dto;

import lombok.Data;

/**
 * 响应基类
 */
@Data
public class ResponseDTO {
    private Integer code;
    private String message;
    private Object data;

    public ResponseDTO(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseDTO success(Object data) {
        return new ResponseDTO(200, "Success", data);
    }

    public static ResponseDTO success(String message, Object data) {
        return new ResponseDTO(200, message, data);
    }

    public static ResponseDTO error(Integer code, String message) {
        return new ResponseDTO(code, message, null);
    }
}
