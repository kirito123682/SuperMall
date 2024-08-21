package com.aoyamananam1.supermall.product.util;

import com.aoyamananam1.common.utils.Constant;
import com.aoyamananam1.supermall.product.vo.PageVO;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static Map<String, Object> getMap(PageVO pageVO) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.PAGE, String.valueOf(pageVO.getPage()));
        map.put(Constant.LIMIT, String.valueOf(pageVO.getLimit()));
        map.put(Constant.ORDER, pageVO.getOrder());
        map.put(Constant.ORDER_FIELD, pageVO.getSidx());

        return map;
    }
}
