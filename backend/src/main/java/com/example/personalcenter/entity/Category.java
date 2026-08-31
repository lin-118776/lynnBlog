package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private LocalDateTime createTime;

    /** 该分类下的文章数（非表字段，列表接口统计填充） */
    @TableField(exist = false)
    private Long articleCount;
}
