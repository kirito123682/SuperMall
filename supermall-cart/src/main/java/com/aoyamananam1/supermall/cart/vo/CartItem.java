package com.aoyamananam1.supermall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItem {

    private Long skuId;
    private boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal("" + this.count));
    }
}
