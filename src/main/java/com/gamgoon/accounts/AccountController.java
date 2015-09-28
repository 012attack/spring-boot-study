package com.gamgoon.accounts;

import com.gamgoon.commons.ErrorResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    // TODO stream() vs parallelStream()
    // TODO HATEOAS
    // TODO 로깅
    // TODO VIEW
    //  NSPA 1. Thymeleaf
    //  SPA  2. React
/*    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public ResponseEntity getAccounts(Pageable pageable) {
        Page<Account> page = repository.findAll(pageable);
        List<AccountDto.Response> content = page.getContent().parallelStream()
                .map(account -> modelMapper.map(account, AccountDto.Response.class))
                .collect(Collectors.toList());
        PageImpl<AccountDto.Response> result = new PageImpl<>(content, pageable, page.getTotalElements());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }*/

    // 응답이 고정적으로 정해지는 경우 ResponseStatus 를 이용
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public PageImpl<AccountDto.Response> getAccounts(Pageable pageable) {
        Page<Account> page = repository.findAll(pageable);
        List<AccountDto.Response> content = page.getContent().parallelStream()
                .map(account -> modelMapper.map(account, AccountDto.Response.class))
                .collect(Collectors.toList());
        PageImpl<AccountDto.Response> result = new PageImpl<>(content, pageable, page.getTotalElements());
        return result;
    }

    @RequestMapping(value="/accounts/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto.Response getAccount(@PathVariable Long id) {
        Account account = service.getAccount(id);
        return modelMapper.map(account, AccountDto.Response.class);
    }

    // 1.전체 업데이트 vs 2.부분 업데이트
    // 1. 요청 body 의 내용을 그대로 업데이트 (변경할 모든 항목이 들어옴) - PUT
    // 2. 부분만 들어오는 body의 내용을 업데이트. (body로 들어오는 항목 유동적) - PATCH
    @RequestMapping(value="/accounts/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateAccount(@PathVariable Long id,
                                        @RequestBody @Valid AccountDto.Update updateDto,
                                        BindingResult result ) {
        if (result.hasErrors()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Account updatedAccount = service.updateAccount(id, updateDto);
        return new ResponseEntity<>(modelMapper.map(updatedAccount, AccountDto.Response.class), HttpStatus.OK);
    }

    /*
    @ExceptionHandler(UserDuplicatedException.class)
    public ResponseEntity handleUserDuplicatedException(UserDuplicatedException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("[" + e.getUsername() + "] 중복된 username 입니다.");
        errorResponse.setCode("duplicated.username.exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
*/
    // ResponseStatus 사용
    @ExceptionHandler(UserDuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserDuplicatedException(UserDuplicatedException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("[" + e.getUsername() + "] 중복된 username 입니다.");
        errorResponse.setCode("duplicated.username.exception");
        return errorResponse;
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccountNotFoundException(AccountNotFoundException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("[" + e.getId() + "]에 해당하는 계정이 없습니다.");
        errorResponse.setCode("account.not.found.exception");
        return errorResponse;
    }


}
