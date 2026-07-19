package com.tech.shoeshop.dto.request.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryFilterRequest {
    private String name;
    private String description;
}
