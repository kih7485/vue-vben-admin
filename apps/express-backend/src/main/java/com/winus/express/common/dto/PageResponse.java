package com.winus.express.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageResponse<T> of(List<T> content, long totalElements, int totalPages,
                                        int currentPage, int pageSize) {
        return PageResponse.<T>builder()
            .content(content)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .hasNext(currentPage < totalPages - 1)
            .hasPrevious(currentPage > 0)
            .build();
    }
}