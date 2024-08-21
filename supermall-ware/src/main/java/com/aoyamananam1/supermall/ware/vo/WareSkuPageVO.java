package com.aoyamananam1.supermall.ware.vo;

import com.aoyamananam1.common.vo.PageVO;
import lombok.Data;

@Data
public class WareSkuPageVO extends PageVO {

    private Long skuId;

    private Long wareId;
}
