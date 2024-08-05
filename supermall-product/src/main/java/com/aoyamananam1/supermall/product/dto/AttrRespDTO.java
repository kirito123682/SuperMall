package com.aoyamananam1.supermall.product.dto;

import com.aoyamananam1.supermall.product.vo.AttrVO;
import lombok.Data;

@Data
public class AttrRespDTO extends AttrVO {

    /*
    所属分类名称
     */
    private String catelogName;

    /*
    所属分组
     */
    private String groupName;

    /*
    分类全路径
     */
    private Long[] catelogPath;
}
