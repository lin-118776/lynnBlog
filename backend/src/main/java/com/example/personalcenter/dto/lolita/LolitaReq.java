package com.example.personalcenter.dto.lolita;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Lolita 服饰新增/更新请求
 */
@Data
public class LolitaReq {

    /** 服饰名称/款式名 */
    private String name;

    /** 品牌 */
    private String brand;

    /** 系列/印花名 */
    private String series;

    /** 分类（OP/JSK/SK/衬衫/配饰/假发/鞋/其他） */
    private String category;

    /** 主色 */
    private String color;

    /** 尺码 */
    private String size;

    /** 购买日期 */
    private LocalDate purchaseDate;

    /** 购买价格 */
    private BigDecimal purchasePrice;

    /** 状态（预约中/在途/待补尾款/现货/已出/已送人） */
    private String status;

    /** 待补金额（状态为"待补尾款"时填写） */
    private BigDecimal balanceDue;

    /** 存放位置 */
    private String location;

    /** 主图URL */
    private String coverImage;

    /** 多图URL列表 */
    private List<String> images;

    /** 个人笔记/种草心得 */
    private String note;

    /** 是否公开：0仅自己 1公开 */
    private Integer isPublic;
}