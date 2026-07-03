package com.tech.shoeshop.exception;

public class DuplicateUsernameException extends BusinessException{
    public DuplicateUsernameException (String username){
        super("The username already exists: "+username);
    }
}
