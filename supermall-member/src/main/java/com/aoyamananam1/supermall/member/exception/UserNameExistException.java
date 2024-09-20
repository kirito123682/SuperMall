package com.aoyamananam1.supermall.member.exception;

public class UserNameExistException extends RuntimeException{

    public UserNameExistException(){
        super("邮箱已存在");
    }
}
