package com.aoyamananam1.supermall.thirdparty.service.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.aoyamananam1.supermall.thirdparty.service.QiniuOssService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QiniuOssServiceImpl implements QiniuOssService {

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private Auth auth;

    @Value("${Qiniu.bucket-name}")
    private String bucket;
    @Value("${Qiniu.url}")
    private String URL;

    private StringMap putPolicy;

    private static final long EXPIRE_SECONDS = 300L;
    private static final long F_SIZE_LIMIT = 524288000L;//100M

    /**
     * 得到token并返回token key 和host
     * @param key
     * @return
     */
    @Override
    public Map<String, String> getUpToken(String key) {
        Map<String, String> map = new HashMap<>();
        key = getKey(key);

        map.put("token", getUptoken(key));
        map.put("key", key);
        map.put("host", URL);
        return map;
    }

    /**
     * 重新生成文件名称（路径和名称）
     * @param key
     * @return
     */
    private String getKey(String key){
        String fileFolder = "icon/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = UUID.randomUUID() + "_" + key;
        String resKey = fileFolder + date + "/" + fileName;
        return resKey;
    }

    /**
     *
     * 服务器端直传文件
     * @param localFilePath
     */
    public void uploadFromServer(String localFilePath){
//        localFilePath = "D:\\Data\\JavaProj\\sky-take-out-main\\资料\\day03\\图片资源\\3.png";
        String fileName = localFilePath.substring(localFilePath.lastIndexOf('\\') + 1);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
//		String key = null;
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = "icon/" + format + "/";//文件夹前缀
        String key = dir + fileName;


//        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            ex.printStackTrace();
            if (ex.response != null) {
                System.err.println(ex.response);

                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 得到上传token
     * @param key
     * @return
     */
    private String getUptoken(String key){
//        String accessKey = "access key";
//        String secretKey = "secret key";
//        String bucket = "bucket name";
//        StringMap putPolicy = new StringMap();
        this.putPolicy = new StringMap();
        putPolicy.put("fsizeLimit", F_SIZE_LIMIT);

//        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket, key, EXPIRE_SECONDS, putPolicy);
        System.out.println(upToken);

        return upToken;
    }
}
