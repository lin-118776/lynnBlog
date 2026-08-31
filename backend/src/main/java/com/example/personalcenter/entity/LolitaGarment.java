package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lolita 服饰收藏实体（对应表 lolita_garment）
 * images 字段为 JSON 数组，使用 JacksonTypeHandler 自动映射
 */
@Data
@TableName(value = "lolita_garment", autoResultMap = true)
public class LolitaGarment {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

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

    /** 状态（预约中/在途/现货/已出/已送人） */
    private String status;

    /** 穿着次数 */
    private Integer wearCount;

    /** 存放位置 */
    private String location;

    /** 主图URL */
    private String coverImage;

    /** 多图URL列表（JSON 数组） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    /** 个人笔记/种草心得 */
    private String note;

    /** 是否公开：0仅自己 1公开 */
    private Integer isPublic;

    /** 所属用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}