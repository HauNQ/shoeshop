package com.tech.shoeshop.exception;

public class DuplicateCategoryNameException extends BusinessException{
    public DuplicateCategoryNameException(String categoryName) {
        super("Category name '" + categoryName + "' already exists");
    }
}
