package com.code.community.exception;

public enum  CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND (2001,"您访问的问题不存在，请返回首页选择其它问题。"),
    TARGET_NOT_FOUND(2002,"未选中任何问题和评论进行回复。"),
    NO_LOGIN(2003,"当前操作需要用户登录，请登录后重试。"),
    SYSTEM_ERROR(2004, "服务器出错了，请稍后重试。"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在！"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在了！"),
    CONTENT_IS_EMPTY(2007, "输入的内容不能为空！"),
    ;
    private  Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }


}
