package com.aoyamananam1.supermall.authserver.feign;

import com.aoyamananam1.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("supermall-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendCode")
    R sendCode(@RequestParam("to") String to, @RequestParam("code") String code);

}
