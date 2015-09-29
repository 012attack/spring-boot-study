package com.gamgoon.accounts;

import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by gamgoon on 2015. 9. 16..
 */
@Service
@Transactional
@Slf4j
public class AccountService {

//    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public Account createAccount(AccountDto.Create dto) {
//        Account account = new Account();
//        account.setUsername(dto.getUsername());
//        account.setPassword(dto.getPassowrd());

        Account account = modelMapper.map(dto, Account.class);

        String username = dto.getUsername();
        if (repository.findByUsername(username) != null) {
            log.error("user duplicated exception, {}", username);
            throw new UserDuplicatedException(username);  // 부가적인 정보를 넘길 수 있다.
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));
//        Account account = new Account();
//        BeanUtils.copyProperties(dto, account);

        // TODO password 해싱



        Date now = new Date();
        account.setJoined(now);
        account.setUpdated(now);

        return repository.save(account);

    }

    public Account updateAccount(Long id, AccountDto.Update updateDto) {
        Account account = getAccount(id);
        account.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        account.setFullName(updateDto.getFullName());
        return repository.save(account);
    }

    public Account getAccount(Long id) {
        Account account = repository.findOne(id);
        if (account == null){
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    public void deleteAccount(Long id) {
        repository.delete(getAccount(id));
    }
}
