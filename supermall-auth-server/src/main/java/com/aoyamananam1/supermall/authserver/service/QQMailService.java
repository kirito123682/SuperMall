package com.aoyamananam1.supermall.authserver.service;

public interface QQMailService {

    /**
     * 发送文本邮件
     * @param from
     * @param to
     * @param subject
     * @param context
     */
    void sendText(String name, String from, String to, String subject, String context);

}
