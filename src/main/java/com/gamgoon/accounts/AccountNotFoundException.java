package com.gamgoon.accounts;

/**
 * Created by gamgoon on 2015-09-24.
 */
public class AccountNotFoundException extends RuntimeException {

     Long id;

    public AccountNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
