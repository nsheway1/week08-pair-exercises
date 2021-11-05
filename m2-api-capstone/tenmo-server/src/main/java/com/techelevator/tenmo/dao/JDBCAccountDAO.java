package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JDBCAccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCAccountDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Double getBalanceByUserId(int userId) {
        String sql = "SELECT balance FROM accounts WHERE user_id = ?";
        Double balance = jdbcTemplate.queryForObject(sql, Double.class, userId);
        return balance;
    }

    public Map<Long, String> getAllUsers() {
        Map<Long, String> allUsers = new HashMap<Long, String>();
        String sql = "SELECT user_id, username FROM users";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Long userId = results.getLong("user_id");
            String username = results.getString("username");
            allUsers.put(userId, username);
        }
        return allUsers;
    }

    public Transfer createTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id," +
                " account_from, account_to, amount) VALUES (default, ?, ?, ?, ?, ?) RETURNING transfer_id";
        Long transferId = jdbcTemplate.queryForObject(sql, Long.class, transfer.getTransferTypeId(),
                transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        transfer.setTransferId(transferId);
        return transfer;
    }

    public void updateBalances(Transfer transfer) {
        Account senderAccount = getAccountByAccountID(transfer.getAccountFrom());
        Account receiverAccount = getAccountByAccountID(transfer.getAccountTo());
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql, senderAccount.getAccountBalance() - transfer.getAmount(), transfer.getAccountFrom());
        jdbcTemplate.update(sql, receiverAccount.getAccountBalance() + transfer.getAmount(), transfer.getAccountTo());
    }

    public Account getAccountByAccountID(Long id) {
        Account account = new Account();
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()) {
            account.setAccountId(results.getLong("account_id"));
            account.setUserId(results.getLong("user_id"));
            account.setAccountBalance(results.getDouble("balance"));
        }
        return account;
    }

    public Account getAccountByUserId(Long id) {
        Account account = new Account();
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()) {
            account.setAccountId(results.getLong("account_id"));
            account.setUserId(results.getLong("user_id"));
            account.setAccountBalance(results.getDouble("balance"));
        }
        return account;
    }

    public List<Transfer> getPastTransfersByUserId(Long id) {
        List<Transfer> transfers = new ArrayList<Transfer>();
        Account account = getAccountByUserId(id);
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to," +
                " amount FROM transfers WHERE account_from = ? OR account_to = ? AND transfer_status_id !=1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account.getAccountId(), account.getAccountId());
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);

            transfers.add(transfer);
        }
        return transfers;
    }

    public List<Transfer> getPendingTransfersByUserId(Long id) {
        List<Transfer> transfers = new ArrayList<Transfer>();
        Account account = getAccountByUserId(id);
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, " +
                "account_to, amount FROM transfers WHERE transfer_status_id =1 AND account_from = ? OR account_to = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account.getAccountId(), account.getAccountId());
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);

            transfers.add(transfer);
        }
        return transfers;
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getLong("transfer_type_id"));
        transfer.setTransferStatusId(results.getLong("transfer_status_id"));
        transfer.setAccountFrom(results.getLong("account_from"));
        transfer.setAccountTo(results.getLong("account_to"));
        transfer.setAmount(results.getDouble("amount"));
        transfer.setSenderName(getUserNameByAccountId(transfer.getAccountFrom()));
        transfer.setReceiverName(getUserNameByAccountId(transfer.getAccountTo()));
        return transfer;
    }

    private String getUserNameByAccountId(Long id) {
        String sql = "SELECT username FROM users WHERE user_id IN (SELECT user_id From accounts WHERE account_id =?)";
        String username = jdbcTemplate.queryForObject(sql, String.class, id);
        return username;

    }

    public void updateTransfer(Transfer transfer, Long id) {
        String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transfer.getTransferStatusId(), id);

    }

}
