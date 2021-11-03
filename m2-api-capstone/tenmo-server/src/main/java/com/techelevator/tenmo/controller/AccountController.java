package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.auth.dao.JdbcUserDAO;
import com.techelevator.tenmo.auth.dao.UserDAO;
import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.JDBCAccountDAO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AccountController {

    private AccountDAO accountDAO;
    private UserDAO userDAO;

    public AccountController(AccountDAO accountDAO, UserDAO userDAO){
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }


    @RequestMapping(path="/account_balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal){
       int userId = userDAO.findIdByUsername(principal.getName());
       double balance = accountDAO.getBalanceByUserId(userId);

       return balance;
    }







}
