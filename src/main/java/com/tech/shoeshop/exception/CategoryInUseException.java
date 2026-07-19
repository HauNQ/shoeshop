package com.tech.shoeshop.exception;

public class CategoryInUseException extends BusinessException{
    public CategoryInUseException(Long categoryId) {
        super("Cannot delete category with ID " + categoryId +
                " because it is assigned to one or more products.");
    }
}
