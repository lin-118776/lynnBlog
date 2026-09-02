package com.example.personalcenter.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点赞切换结果：liked 为操作后是否已赞，count 为最新点赞数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeResp {
    private boolean liked;
    private int count;
}
