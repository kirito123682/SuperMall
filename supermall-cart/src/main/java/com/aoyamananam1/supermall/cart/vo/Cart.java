package com.aoyamananam1.supermall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

    private List<CartItem> items;


    private Integer countNum;//商品数量
    private Integer countType;//商品类型数量

    private BigDecimal totalAmount;//最终总价
    private BigDecimal reduce = new BigDecimal("0.00");//减免价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }



    public Integer getCountType() {
        return items.size();
    }



    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                BigDecimal totalPrice = item.getTotalPrice();
                amount = amount.add(totalPrice);
            }
        }
        BigDecimal subtract = amount.subtract(getReduce());

        return subtract;
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
