package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;
import java.util.Map;

public interface AccountDAO {

    public Double getBalanceByUserId(int userId);
    public Map<Long, String> getAllUsers();
    public Transfer createTransfer (Transfer transfer);
    public void updateBalances(Transfer transfer);
    public Account getAccountByAccountID(Long id);
    public Account getAccountByUserId(Long id);
    public List<Transfer> getPastTransfersByUserId(Long id);
    public List<Transfer> getPendingTransfersByUserId(Long id);
    public void updateTransfer(Transfer transfer, Long id);
}
