package com.aoyamananam1.supermall.product.feign;

import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.to.es.SkuEsModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("supermall-search")
public interface SearchFeignService {

    @PostMapping("search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
