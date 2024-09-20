package com.aoyamananam1.supermall.authserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.common.vo.GiteeUser;
import com.aoyamananam1.supermall.authserver.exception.Oauth2Exception;
import com.aoyamananam1.supermall.authserver.feign.MemberFeignService;
import com.aoyamananam1.supermall.authserver.util.HttpClientUtil;
import com.aoyamananam1.common.vo.MemberRespVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class Oauth2Controller {

    private String clientId = "7ea64f5e6184ce19f038f6a2e80d52673bb2cac0b353c73a2d17a46527ac3c74";
    private String redirectUri = "http://auth.supermall00.com/oauth2.0/gitee/success&response_type=code";
    private String clientSecret = "42ca00dd4a51f0a18cd6de8cc213983e388b89fbebdc437ee7ddc3a85c7cd576";

    @Resource
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/gitee/success")
    public String gitee(@RequestParam("code") String code, HttpSession session) {

        //1根据code换取accessToken
//        https://gitee.com/oauth/token?grant_type=authorization_code&code={code}&client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
        Map<String, Object> map = new HashMap<>();
        map.put("grant_type", "authorization_code");
        map.put("code", code);
        map.put("client_id", clientId);
        map.put("redirect_uri", redirectUri);
        map.put("client_secret", clientSecret);
        JSONObject jsonObject = new JSONObject(map);

        try {
            String post = HttpClientUtil.post("https://gitee.com/oauth/token", jsonObject, null);
//            SocialUser socialUser = JSONObject.parseObject(post, SocialUser.class);
            JSONObject accessTokenJson = JSONObject.parseObject(post);
            String accessToken = accessTokenJson.getString("access_token");//得到accesstoken

            //为当前用户自动注册，或者登录
            //发送请求 得到当前用户的具体信息
            Map<String, Object> map2 = new HashMap<>();
            map2.put("access_token", accessToken);
            JSONObject jsonObject1 = new JSONObject(map2);
            String userinfo = HttpClientUtil.get("https://gitee.com/api/v5/user", jsonObject1);

            //得到gitee用户具体信息
            GiteeUser giteeUser = JSONObject.parseObject(userinfo, GiteeUser.class);
            giteeUser.setAccessToken(accessToken);

            //调用远程接口验证用户
            R r = memberFeignService.login(giteeUser);
            if (r.getCode() == 0){
                //成功
                MemberRespVO data = r.getData("data", new TypeReference<MemberRespVO>() {
                });
                log.info("登陆成功，用户信息： {}", data);
                session.setAttribute("loginUser", data);

                return "redirect:http://supermall00.com";
            }else {
                //失败
                return "redirect:http://auth.supermall00.com/login.html";
            }

        }catch (Oauth2Exception e){
            return "redirect:http://auth.supermall00.com/login.html";
        }
        //2登陆成功跳回首页

//        return "redirect:http://supermall00.com";
    }
}
