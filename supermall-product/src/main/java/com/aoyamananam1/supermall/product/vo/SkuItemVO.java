package com.aoyamananam1.supermall.product.vo;

import com.aoyamananam1.supermall.product.entity.SkuImagesEntity;
import com.aoyamananam1.supermall.product.entity.SkuInfoEntity;
import com.aoyamananam1.supermall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVO {

    //1 sku基本信息
    SkuInfoEntity info;

    boolean hasStock = true;
    //2 sku图片信息
    List<SkuImagesEntity> images;
    //3 spu销售属性组合
    List<SkuItemSaleAttrVO> saleAttr;
    //4 获取spu介绍
    SpuInfoDescEntity desp;
    //5 spu规格参数信息
    List<SpuItemAttrGroupVO> groupAttrs;


    @Data
    public static class SkuItemSaleAttrVO{
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVO> attrValues;
    }

    @Data
    public static class AttrValueWithSkuIdVO{
        private String attrValue;
        private String skuIds;
    }

    @Data
    public static class SpuItemAttrGroupVO{
        private String groupName;
        private List<SpuBaseAttrVO> attrs;
    }

    @Data
    public static class SpuBaseAttrVO{
        private String attrName;
        private List<String> attrValue;
    }
}
