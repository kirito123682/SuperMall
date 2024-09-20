package com.aoyamananam1.supermall.authserver.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRegistVO {

    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名长度6-18")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "用户名长度6-18")
    private String password;

    //手机号正则
//    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+$", message = "格式不正确")//邮箱正则
    @NotEmpty(message = "（邮箱）不能为空")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String code;
}
