package com.gamgoon.accounts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamgoon.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gamgoon on 2015. 9. 16..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
//@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = Application.class)
@WebAppConfiguration
public class AccountControllerTest {

    @Autowired
    AccountService service;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    public void createAccount() throws Exception{
        AccountDto.Create createDto = new AccountDto.Create();
        createDto.setUsername("gamgoon");
        createDto.setPassword("password");

        String s = objectMapper.writeValueAsString(createDto);
        System.out.println(s);

        ResultActions result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

        result.andDo(print());
        result.andExpect(status().isCreated());
        //{"id":1,"username":"gamgoon","fullName":null,"joined":1442420939590,"updated":1442420939590}
        result.andExpect(jsonPath("$.username", is("gamgoon")));

        result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.code", is("duplicated.username.exception")));
    }

    @Test
    public void createAccount_BadRequest() throws Exception {
        AccountDto.Create createDto = new AccountDto.Create();
        createDto.setUsername(" ");
        createDto.setPassword("1234");

        ResultActions result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void getAccounts() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
        service.createAccount(createDto);

        ResultActions result = mockMvc.perform(get("/accounts"));

        result.andDo(print());
        result.andExpect(status().isOk());
    }

    private AccountDto.Create accountCreateDto(){
        AccountDto.Create createDto = new AccountDto.Create();
        createDto.setUsername("gamgoon");
        createDto.setPassword("password");
        return createDto;
    }

    @Test
    public void getAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
        Account account = service.createAccount(createDto);

        ResultActions result = mockMvc.perform(get("/accounts/" + account.getId()));

        result.andDo(print());
        result.andExpect(status().isOk());
    }

    @Test
    public void updateAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
        Account account = service.createAccount(createDto);

        AccountDto.Update updateDto = new AccountDto.Update();
        updateDto.setFullName("updated gamgoon");
        updateDto.setPassword("pass");

        ResultActions result = mockMvc.perform(put("/accounts/" + account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.fullName", is("updated gamgoon")));
//        result.andExpect(jsonPath("$.password", is("pass"))); // 응답에 password 항목이 없다.

    }

    @Test
    public void deleteAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
        Account account = service.createAccount(createDto);


        ResultActions result = mockMvc.perform(delete("/accounts/12342343")
            .with(httpBasic(createDto.getUsername(), createDto.getPassword())));
        result.andDo(print());
        result.andExpect(status().isBadRequest());



        result = mockMvc.perform(delete("/accounts/" + account.getId())
            .with(httpBasic(createDto.getUsername(), createDto.getPassword())));
        result.andDo(print());
        result.andExpect(status().isNoContent());

    }
}