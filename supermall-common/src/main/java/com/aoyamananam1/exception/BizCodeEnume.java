package com.aoyamananam1.exception;

public enum BizCodeEnume {
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，请稍后再试"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USER_EXIST_EXCEPTION(15001, "用户名存在异常"),
    EMAIL_EXIST_EXCEPTION(15002, "邮箱存在异常"),
    LOGINACCT_PASSWORD_INVALID_EXCEPTION(15003, "账号或密码有误");

    private int code;
    private String message;

    BizCodeEnume(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
