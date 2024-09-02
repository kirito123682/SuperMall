package com.aoyamananam1.supermall.search.feign;

import com.aoyamananam1.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("supermall-product")
public interface ProductFeignService {

    @GetMapping("product/attr/info/{attrId}")
    public R getAttrInfo(@PathVariable("attrId") Long attrId);
}
