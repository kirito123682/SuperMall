package com.aoyamananam1.supermall.authserver.vo;

import lombok.Data;

@Data
public class SocialUser {
    private String access_token; //客户端id
    private String remind_in; //登陆后后的回调地址
    private Long expires_in; //密钥
    private String uid;
    private String isRealName;
    //get、set方法
}
