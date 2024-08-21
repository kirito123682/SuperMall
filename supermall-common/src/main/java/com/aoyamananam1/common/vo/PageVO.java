package com.aoyamananam1.common.vo;

import lombok.Data;

@Data
public class PageVO {
    /*
当前页码
 */
    private Long page = 1L;

    /*
    每页记录数
     */
    private Long limit = 5L;

    /*
    排序字段
     */
    private String sidx;

    /*
    排序方法
     */
    private String order;

    /*
    关键字
     */
    private String key;
}
