package com.aoyamananam1.supermall.product.feign;

import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.to.SkuReductionTO;
import com.aoyamananam1.to.SpuBoundTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("supermall-coupon")
public interface CouponFeignService {
    /**
     * 保存积分信息
     * @param spuBoundTO
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);

    /**
     * 保存优惠信息
     * @param skuReductionTO
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);
}
