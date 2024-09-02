package com.aoyamananam1.supermall.product.web;

import com.aoyamananam1.supermall.product.service.SkuInfoService;
import com.aoyamananam1.supermall.product.vo.SkuItemVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Resource
    SkuInfoService skuInfoService;


    /**
     * 展示当前sku商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVO vo = skuInfoService.item(skuId);
        model.addAttribute("item", vo);

        return "item";
    }
}
