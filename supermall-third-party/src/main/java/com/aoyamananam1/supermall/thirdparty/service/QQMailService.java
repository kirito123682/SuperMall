package com.aoyamananam1.supermall.thirdparty.service;

public interface QQMailService {

    /**
     * 发送文本邮件
     * @param from
     * @param to
     * @param subject
     * @param context
     */
    void sendText(String name, String from, String to, String subject, String context);

    /**
     * 发送验证码
     * @param to
     * @param code
     */
    void sendText(String to, String code);
}
