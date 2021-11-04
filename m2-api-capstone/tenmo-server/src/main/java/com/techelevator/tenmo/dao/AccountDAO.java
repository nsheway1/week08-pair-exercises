package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.Map;

public interface AccountDAO {

    public Double getBalanceByUserId(int userId);
    public Map<Long, String> getAllUsers();
    public Transfer createTransfer (Transfer transfer);
}
