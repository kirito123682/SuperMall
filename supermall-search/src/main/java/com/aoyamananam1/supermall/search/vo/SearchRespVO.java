package com.aoyamananam1.supermall.search.vo;

import com.aoyamananam1.to.es.SkuEsModel;
import lombok.Data;

import java.util.Calendar;
import java.util.List;

@Data
public class SearchRespVO {

    private List<SkuEsModel> products;

    private Integer pageNum;

    private Long total;

    private Integer totalPages;
    private List<Integer> pageNavs;

    private List<CatalogVO> catalogs;
    private List<BrandVO> brands;
    private List<AttrVO> attrs;

    private List<NavVo> navs;

    private List<Long> attrIds;

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }


    @Data
    public static class CatalogVO{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class BrandVO {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVO{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
