package com.gamgoon.accounts;

/**
 * Created by gamgoon on 2015. 9. 17..
 */
public class UserDuplicatedException extends RuntimeException{

    String username;

    public UserDuplicatedException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
