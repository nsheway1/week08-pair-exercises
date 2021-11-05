package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.auth.dao.UserDAO;
import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class AccountController {

    private AccountDAO accountDAO;
    private UserDAO userDAO;

    public AccountController(AccountDAO accountDAO, UserDAO userDAO) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }


    @RequestMapping(path = "/accounts/balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal) {
        int userId = userDAO.findIdByUsername(principal.getName());
        double balance = accountDAO.getBalanceByUserId(userId);
        return balance;
    }

    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable Long id) {
        Account account = accountDAO.getAccountByUserId(id);
        return account;
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
        if (transfer.getStatusName().equals("Approved")) {
            accountDAO.updateBalances(transfer);
        }

        return transfer;
    }

    @RequestMapping(path = "/transfers/pastTransfers", method = RequestMethod.GET)
    public List<Transfer> viewPastTransfers(Principal principal) {
        Long userId = Long.valueOf(userDAO.findIdByUsername(principal.getName()));
        List<Transfer> transfers = accountDAO.getPastTransfersByUserId(userId);
        return transfers;
    }

    @RequestMapping(path = "/transfers/pendingTransfers", method = RequestMethod.GET)
    public List<Transfer> viewPendingTransfers(Principal principal) {
        Long userId = Long.valueOf(userDAO.findIdByUsername(principal.getName()));
        List<Transfer> transfers = accountDAO.getPendingTransfersByUserId(userId);
        return transfers;
    }

    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
    public Transfer updateTransfer(@PathVariable Long id, @RequestBody Transfer transfer){
        accountDAO.updateTransfer(transfer, id);
        if(transfer.getStatusName().equals("Approved")){
            accountDAO.updateBalances(transfer);

        }
        return transfer;
    }
}
