package com.gamgoon.accounts;

import com.gamgoon.commons.ErrorResponse;
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

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("잘못된 요청입니다.");
            errorResponse.setCode("bad.request");
            // TODO BindingResult 안에 들어있는 에러 정보 사용하기.
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        Account newAccount =  service.createAccount(account);

        /*
        * 1. 리턴 타입으로 판단.
        *       if( newAccount == null ) 절이 필요.
        *       지저분함.
        *       null 의 의미는 무엇이냐? 애매함.
        * 2. 파라미터 이용.
        *       Account newAccount =  service.createAccount(account, result);
        *       if(result.hasErrors()){
        *        // 예외처리
        *       }
        *       1번보다는 조금 더 직관적이다.
        *       result 역활의 객체를 생성해야한다.
        *       이것 역시 번거롭고 지저분하다.
        * 3. 예외를 던진다.
        *       예외를 통해 부가적인 정보도 넘길 수 있다.
        *       service 호출 다음으로 넘어왔다는 것은 오류 없이 잘 처리되었다는 의미.
        *       할일을 하면된다.
        *       컨트롤러 코드가 깔끔해진다.
        *       @ExceptionHander 를 이용하여 에러응답을 보내는 방법.
        * */

        return new ResponseEntity<>(modelMapper.map(newAccount, AccountDto.Response.class), HttpStatus.CREATED);

    }

    @ExceptionHandler(UserDuplicatedException.class)
    public ResponseEntity handleUserDuplicatedException(UserDuplicatedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("[" + e.getUsername() + "] 중복 username 입니다.");
        errorResponse.setCode("duplicated.username.exception");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
