package com.gamgoon.accounts;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by gamgoon on 2015. 9. 16..
 */
@Service
@Transactional
public class AccountService {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public Account createAccount(AccountDto.Create dto) {
//        Account account = new Account();
//        account.setUsername(dto.getUsername());
//        account.setPassword(dto.getPassowrd());

        Account account = modelMapper.map(dto, Account.class);

//        Account account = new Account();
//        BeanUtils.copyProperties(dto, account);

        // TODO 유효한 username인지 판단
        // TODO password 해싱

        Date now = new Date();
        account.setJoined(now);
        account.setUpdated(now);

        return repository.save(account);

    }
}
