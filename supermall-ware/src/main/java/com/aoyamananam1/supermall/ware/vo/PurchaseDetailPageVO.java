package com.aoyamananam1.supermall.ware.vo;

import com.aoyamananam1.common.vo.PageVO;
import lombok.Data;

@Data
public class PurchaseDetailPageVO extends PageVO {

    /*
    状态
     */
    private Integer status;

    /*
    仓库id
     */
    private Long wareId;
}
