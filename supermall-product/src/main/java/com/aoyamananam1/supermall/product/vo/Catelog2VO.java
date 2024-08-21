package com.aoyamananam1.supermall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2VO {

    private String catalog1Id;//1级父分类id

    private List<Catelog3VO> catalog3List;//三级子分类集合

    private String id;

    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3VO{
        private String catalog2Id;//父分类 ，2级分类id
        private String id;
        private String name;
    }
}
