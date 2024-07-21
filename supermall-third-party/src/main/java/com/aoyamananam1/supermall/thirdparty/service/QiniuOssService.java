package com.aoyamananam1.supermall.thirdparty.service;

import org.springframework.stereotype.Service;

import java.util.Map;


public interface QiniuOssService {

    Map<String, String> getUpToken(String key);

    void uploadFromServer(String path);
}
