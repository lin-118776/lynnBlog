package com.example.personalcenter.dto.article;

import lombok.Data;

@Data
public class ArticleNeighborsResp {

    private Long prevId;
    private String prevTitle;
    private Long nextId;
    private String nextTitle;
}