package com.techelevator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.techelevator.tenmo.auth.dao.JdbcUserDAO;
import com.techelevator.tenmo.auth.dao.UserDAO;
import com.techelevator.tenmo.auth.model.User;
import com.techelevator.tenmo.dao.JDBCAccountDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

public class JDBCAccountDAOIntegrationTests extends DAOIntegrationTest {

    private JDBCAccountDAO jdbcAccountDAO;
    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;

    @Before
    public void setup() {
        this.jdbcTemplate = new JdbcTemplate(this.getDataSource());
        this.jdbcAccountDAO = new JDBCAccountDAO(jdbcTemplate);
        this.userDAO = new JdbcUserDAO(jdbcTemplate);
    }


    @Test
    public void get_balance_by_user_id() {
        Double expectedResult = 1000.00;
        jdbcTemplate.update("TRUNCATE users CASCADE");
        User testUserOne = new User();
        testUserOne.setUsername("Michael Scott");
        testUserOne.setPassword("bestboss");
        testUserOne = insertTestUser(testUserOne);
        Account testAccountOne = new Account();
        testAccountOne.setAccountBalance(1000.00);
        testAccountOne.setUserId(testUserOne.getId());
        testAccountOne = insertTestAccount(testAccountOne);
        int userId = userDAO.findIdByUsername(testUserOne.getUsername());
        Double actualResult = jdbcAccountDAO.getBalanceByUserId(userId);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllUsers() {
        jdbcTemplate.update("TRUNCATE users CASCADE");
        User testUserOne = new User();
        testUserOne.setUsername("Michael Scott");
        testUserOne.setPassword("bestboss");
        testUserOne = insertTestUser(testUserOne);
        User testUserTwo = new User();
        testUserTwo.setUsername("Dwight Schrute");
        testUserTwo.setPassword("jimsucks");
        testUserTwo = insertTestUser(testUserTwo);

        Map<Long, String> expectedResult = new LinkedHashMap<Long, String>();
        expectedResult.put(testUserOne.getId(), testUserOne.getUsername());
        expectedResult.put(testUserTwo.getId(), testUserTwo.getUsername());

        Map<Long, String> actualResult = jdbcAccountDAO.getAllUsers();
        Assert.assertEquals(expectedResult, actualResult);

    }

    private User insertTestUser(User user) {
        String sql = "INSERT INTO users VALUES (DEFAULT, ?, ?) RETURNING user_id";
        Long userId = jdbcTemplate.queryForObject(sql, Long.class, user.getUsername(), user.getPassword());
        user.setId(userId);
        return user;

    }

    @Test
    public void create_transfer() {
        jdbcTemplate.update("TRUNCATE users CASCADE");
        User testUserOne = new User();
        testUserOne.setUsername("Michael Scott");
        testUserOne.setPassword("bestboss");
        testUserOne = insertTestUser(testUserOne);
        User testUserTwo = new User();
        testUserTwo.setUsername("Dwight Schrute");
        testUserTwo.setPassword("jimsucks");
        testUserTwo = insertTestUser(testUserTwo);


        Account testAccountOne = new Account();
        testAccountOne.setAccountBalance(1000.00);
        testAccountOne.setUserId(testUserOne.getId());
        testAccountOne = insertTestAccount(testAccountOne);

        Account testAccountTwo = new Account();
        testAccountTwo.setAccountBalance(1000.00);
        testAccountTwo.setUserId(testUserTwo.getId());
        testAccountTwo = insertTestAccount(testAccountTwo);


        Transfer testTransfer = new Transfer();
        testTransfer.setAccountFrom(testAccountOne.getAccountId());
        testTransfer.setAccountTo(testAccountTwo.getAccountId());
        testTransfer.setAmount(500.00);
        testTransfer.setTransferStatusId(Long.valueOf(2));
        testTransfer.setTransferTypeId(Long.valueOf(2));
        testTransfer = jdbcAccountDAO.createTransfer(testTransfer);
        Assert.assertNotNull(testTransfer.getTransferId());
    }

    private Account insertTestAccount(Account account) {
        String sql = "INSERT INTO accounts (account_id, user_id, balance) VALUES (default, ?, ?) RETURNING account_id";
        Long accountId = jdbcTemplate.queryForObject(sql, Long.class, account.getUserId(), account.getAccountBalance());
        account.setAccountId(accountId);

        return account;
    }


}
