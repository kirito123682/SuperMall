package com.aoyamananam1.supermall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuInfoPageVO extends PageVO{

    private Long catelogId;

    private Long brandId;

    private BigDecimal min;

    private BigDecimal max;

}
