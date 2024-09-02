package com.aoyamananam1.supermall.search.service;

import com.aoyamananam1.supermall.search.vo.SearchParamVO;
import com.aoyamananam1.supermall.search.vo.SearchRespVO;

public interface MallSearchService {

    /**
     * 查询页面搜索
     * @param searchParamVO 检索的所有参数
     * @return 包含页面需要的所有信息
     */
    SearchRespVO search(SearchParamVO searchParamVO);
}
