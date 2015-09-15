package com.gamgoon.accounts;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by gamgoon on 2015. 9. 16..
 */
@RestController
public class AccountController {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AccountService service;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public ResponseEntity createAccount(@RequestBody @Valid AccountDto.Create account,
                                        BindingResult result) {
        if (result.hasErrors()){
            // TODO 에러응답 본문 추가히기
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Account newAccount =  service.createAccount(account);


        return new ResponseEntity<>(modelMapper.map(newAccount, AccountDto.Response.class), HttpStatus.CREATED);

    }
}
