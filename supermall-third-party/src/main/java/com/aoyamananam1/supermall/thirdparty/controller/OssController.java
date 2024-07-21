package com.aoyamananam1.supermall.thirdparty.controller;

import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.supermall.thirdparty.service.QiniuOssService;
import com.aoyamananam1.supermall.thirdparty.config.QiniuOssConfiguration;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OssController {

//    @Autowired
//    QiniuOss qiniuOss;

    @Resource
    private QiniuOssService qiniuOssService;

    /**
     * 得到上传token key host
     * @param key
     * @return
     */
    @GetMapping("/oss/uptoken")
    public R getUpToken(@RequestParam String key){
        Map<String, String> map = qiniuOssService.getUpToken(key);
        return R.ok().put("data", map);
    }

    /**
     * 服务端直传文件测试
     * @return
     */
    @PostMapping("/oss/upload")
    public R uploadtest(){
        qiniuOssService.uploadFromServer("D:\\Data\\JavaProj\\sky-take-out-main\\资料\\day03\\图片资源\\4.png");
        return R.ok();
    }
}
