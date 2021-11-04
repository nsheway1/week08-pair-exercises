package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

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
        Map<Long, String> allUsers = new LinkedHashMap<Long, String>();
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

}
