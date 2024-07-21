package com.aoyamananam1.supermall.thirdparty.config;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class QiniuOssConfiguration {

//    static {
//        //构造一个带指定 Region 对象的配置类
//        Configuration cfg = new Configuration(Region.region2());
//        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//        //...其他参数参考类注释
//
//        uploadManager = new UploadManager(cfg);
//    }
    //...生成上传凭证，然后准备上传
    @Value("${Qiniu.access-key}")
    private String accessKey;
    @Value("${Qiniu.secret-key}")
    private String secretKey;
    @Value("${Qiniu.bucket-name}")
    private String bucket;

    @Value("${Qiniu.url}")
    private String URL;


//    private UploadManager uploadManager;


    @Bean
    public Configuration qiniuConfiguration(){
        Configuration cf = new Configuration(Region.region2());
        cf.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        return cf;
    }

    @Bean
    public UploadManager uploadManager(){
        return new UploadManager(qiniuConfiguration());
    }

    @Bean
    public Auth auth(){
        return Auth.create(accessKey, secretKey);
    }

    @Bean
    public BucketManager bucketManager(){
        return new BucketManager(auth(), qiniuConfiguration());
    }

}
