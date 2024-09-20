package com.aoyamananam1.supermall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.aoyamananam1.common.constant.AuthServerConstant;
import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.common.vo.MemberRespVO;
import com.aoyamananam1.exception.BizCodeEnume;
import com.aoyamananam1.supermall.authserver.feign.MemberFeignService;
import com.aoyamananam1.supermall.authserver.feign.ThirdPartFeignService;
import com.aoyamananam1.supermall.authserver.vo.UserLoginVO;
import com.aoyamananam1.supermall.authserver.vo.UserRegistVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

//    @GetMapping("/login.html")
//    public String loginPage(){
//        return "login";
//    }
//
//    @GetMapping("/reg.html")
//    public String regPage(){
//        return "reg";
//    }

    @Resource
    ThirdPartFeignService thirdPartFeignService;

    @Resource
    MemberFeignService memberFeignService;

    @Autowired
    StringRedisTemplate template;
//    @Qualifier("redisTemplate")
//    @Autowired
//    private RedisTemplate redisTemplate;

    /**
     * 获取验证码
     *
     * @param mailaddr
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("mailaddr") String mailaddr) {

        String redisCode = template.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mailaddr);
        if (StringUtils.hasLength(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                //60s内不能再发送
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMessage());
            }
        }

        //验证码再校验
        String code = UUID.randomUUID().toString().substring(0, 5);
        //redis缓存验证码，防止60s内再次发送
        template.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mailaddr, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);


        thirdPartFeignService.sendCode(mailaddr, code);
        return R.ok();
    }

    /**
     * @param vo
     * @param result
     * @param redirectAttributes 模拟重定向携带数据
     * @return
     */
    @PostMapping("/register")
    public String regist(@Valid UserRegistVO vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

//            model.addAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错，转发到注册页
//            return "forword:/reg.html"; //转发是post请求到reg，reg不支持（默认get）
//            return "reg";
            return "redirect:http://auth.supermall00.com/reg.html";
        }


        //调用远程服务注册
        // 校验验证码
        String code = vo.getCode();
        String s = template.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (StringUtils.hasLength(s)) {
            if (code.equals(s.split("_")[0])) {
                //验证码校验通过
                //删除验证码
                template.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //调用远程服务
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.supermall00.com/login.html";
                } else {
                    //异常，失败
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.supermall00.com/reg.html";
                }
            } else {
                //验证码校验不通过
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.supermall00.com/reg.html";
            }
        } else {
            //验证码过期
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.supermall00.com/reg.html";
        }


        //注册成功，回到登录页
//        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVO vo, RedirectAttributes redirectAttributes, HttpSession session) {

        //远程登录
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            //成功
            MemberRespVO data = r.getData("data", new TypeReference<MemberRespVO>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://supermall00.com";

        } else {
            //失败
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.supermall00.com/login.html";
        }

    }

    /**
     * 登录页跳转
     *
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            return "login";
        }else {
            return "redirect:http://supermall00.com";
        }

    }
}
