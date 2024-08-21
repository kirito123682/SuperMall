package com.aoyamananam1.supermall.product.vo;

import lombok.Data;

@Data
public class SpuInfoPageVO extends PageVO{
    /*
    三级分类id
     */
    private Long catelogId;
    /*
    品牌id
     */
    private Long brandId;
    /*
    商品状态
     */
    private Integer status;
}
