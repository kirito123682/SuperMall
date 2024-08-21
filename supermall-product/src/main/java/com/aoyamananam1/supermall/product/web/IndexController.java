package com.aoyamananam1.supermall.product.web;

import com.aoyamananam1.supermall.product.entity.CategoryEntity;
import com.aoyamananam1.supermall.product.service.CategoryService;
import com.aoyamananam1.supermall.product.vo.Catelog2VO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Resource
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){

        //TODO 查出所有1级分类
        List<CategoryEntity> entities = categoryService.getLevelOneCategorys();

        model.addAttribute("categorys",entities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2VO>> getCatalogJson(){
        Map<String, List<Catelog2VO>> map = categoryService.getCatalogJson();

        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
