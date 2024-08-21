package com.aoyamananam1.supermall.ware.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class PurchaseDoneVO {

    @NotNull
    private Long id;

    private List<PurchaseDoneItemVO> items;
}
