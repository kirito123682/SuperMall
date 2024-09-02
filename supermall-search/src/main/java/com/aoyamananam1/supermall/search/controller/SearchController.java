package com.aoyamananam1.supermall.search.controller;

import com.aoyamananam1.supermall.search.service.MallSearchService;
import com.aoyamananam1.supermall.search.vo.SearchParamVO;
import com.aoyamananam1.supermall.search.vo.SearchRespVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Resource
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParamVO searchParamVO, Model model){
        SearchRespVO respVO = mallSearchService.search(searchParamVO);
        model.addAttribute("result", respVO);

        return "list";
    }
}
