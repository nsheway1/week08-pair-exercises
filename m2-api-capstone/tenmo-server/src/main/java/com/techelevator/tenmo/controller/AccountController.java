package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.auth.dao.JdbcUserDAO;
import com.techelevator.tenmo.auth.dao.UserDAO;
import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.JDBCAccountDAO;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.security.Principal;
import java.util.Map;

@RestController
public class AccountController {

    private AccountDAO accountDAO;
    private UserDAO userDAO;

    public AccountController(AccountDAO accountDAO, UserDAO userDAO) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }


    @RequestMapping(path = "/account_balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal) {
        int userId = userDAO.findIdByUsername(principal.getName());
        double balance = accountDAO.getBalanceByUserId(userId);

        return balance;
    }

    @RequestMapping(path = "/availableUsers", method = RequestMethod.GET)
    public Map<Long, String> getAvailableUsers(Principal principal) {
        Map<Long, String> availableUsers = accountDAO.getAllUsers();
        Long userId = Long.valueOf(userDAO.findIdByUsername(principal.getName()));
        availableUsers.remove(userId);

        return availableUsers;
    }

    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer createTransfer(@RequestBody Transfer transfer) {
        transfer = accountDAO.createTransfer(transfer);
        return transfer;
    }


}
