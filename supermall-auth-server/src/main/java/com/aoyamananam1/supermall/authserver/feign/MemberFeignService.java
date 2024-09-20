package com.aoyamananam1.supermall.authserver.feign;

import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.common.vo.GiteeUser;
import com.aoyamananam1.supermall.authserver.vo.UserLoginVO;
import com.aoyamananam1.supermall.authserver.vo.UserRegistVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("supermall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVO vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVO vo);

    @PostMapping("/member/member/gitee/login")
    R login(@RequestBody GiteeUser vo);
}
