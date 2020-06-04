package com.code.community.dto;

import com.code.community.exception.CustomizeErrorCode;
import com.code.community.exception.CustomizeException;
import lombok.Data;

import java.util.List;

@Data
public class ResultDTO<T> {
    private Integer code;
    private String message;
    private T data;

    public static ResultDTO errorOf(Integer code, String message){
        ResultDTO restultDTO = new ResultDTO();
        restultDTO.setCode(code);
        restultDTO.setMessage(message);
        return restultDTO;
    }

    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {

        return errorOf(errorCode.getCode(), errorCode.getMessage());
    }

    public static Object okOff(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }
    public static <T> ResultDTO okOf(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeException ex) {
        return ResultDTO.errorOf(ex.getCode(), ex.getMessage());
    }
}
