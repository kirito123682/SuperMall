package com.aoyamananam1.supermall.thirdparty.controller;

import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.supermall.thirdparty.service.QQMailService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class MailSendController {

    @Resource
    QQMailService qqMailService;

    /**
     * 提供给别的服务进行调用
     * @param to
     * @return
     */
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("to") String to, @RequestParam("code") String code){
        qqMailService.sendText(to, code);
        return R.ok();
    }


}
