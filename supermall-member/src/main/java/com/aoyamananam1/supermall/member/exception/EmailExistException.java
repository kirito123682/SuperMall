package com.aoyamananam1.supermall.member.exception;

public class EmailExistException extends RuntimeException{

    public EmailExistException(){
        super("手机号已存在");
    }
}
