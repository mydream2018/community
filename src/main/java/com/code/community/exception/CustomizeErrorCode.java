package com.code.community.exception;

public enum  CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND ("您访问的问题不存在，请返回首页选择其它问题。");

    private String message;
    CustomizeErrorCode(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

}
