package com.aoyamananam1.supermall.authserver.exception;

public class Oauth2Exception extends RuntimeException{

    public Oauth2Exception(){
        super("第三方登录失败");
    }
}
