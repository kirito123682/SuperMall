package com.aoyamananam1.supermall.search.service;

import com.aoyamananam1.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {

    /**
     * 商品上架后保存数据到es
     * @param skuEsModels
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
