package com.gamgoon.accounts;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by gamgoon on 2015. 9. 16..
 */
public class AccountTest {
    @Test
    public void getterSetter() {
        Account account = new Account();
        account.setUsername("gamgoon");
        account.setPassword("password");

        assertThat(account.getUsername(), is("gamgoon"));
    }
}