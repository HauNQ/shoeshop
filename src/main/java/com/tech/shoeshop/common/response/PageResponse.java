package com.tech.shoeshop.common.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElement;
    private int totalPages;
    private boolean first;
    private boolean last;
}
