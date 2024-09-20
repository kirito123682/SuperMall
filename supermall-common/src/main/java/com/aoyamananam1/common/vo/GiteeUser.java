package com.aoyamananam1.common.vo;

import lombok.Data;

@Data
public class GiteeUser {
    private Integer id;
    private String name;
    private String email;
    private String accessToken;
}
