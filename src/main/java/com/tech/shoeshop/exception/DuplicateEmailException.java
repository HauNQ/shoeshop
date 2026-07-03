package com.tech.shoeshop.exception;

public class DuplicateEmailException extends BusinessException{
    public DuplicateEmailException(String email){
        super("Email already exists: "+email);
    }
}
