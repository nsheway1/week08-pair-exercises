package com.techelevator;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JDBCAccountDAOIntegrationTests extends DAOIntegrationTest {

    private JDBCAccountDAO jdbcAccountDAO;
    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;
    private User testUserOne;
    private User testUserTwo;
    private Account testAccountOne;
    private Account testAccountTwo;

    @Before
    public void setup() {
        this.jdbcTemplate = new JdbcTemplate(this.getDataSource());
        this.jdbcAccountDAO = new JDBCAccountDAO(jdbcTemplate);
        this.userDAO = new JdbcUserDAO(jdbcTemplate);
        jdbcTemplate.update("TRUNCATE users CASCADE");
        this.testUserOne = new User();
        this.testUserOne.setUsername("Michael Scott");
        this.testUserOne.setPassword("bestboss");
        this.testUserOne = insertTestUser(testUserOne);
        this.testUserTwo = new User();
        this.testUserTwo.setUsername("Dwight Schrute");
        this.testUserTwo.setPassword("jimsucks");
        this.testUserTwo = insertTestUser(testUserTwo);


        this.testAccountOne = new Account();
        this.testAccountOne.setAccountBalance(1000.00);
        this.testAccountOne.setUserId(testUserOne.getId());
        this.testAccountOne = insertTestAccount(testAccountOne);

        this.testAccountTwo = new Account();
        this.testAccountTwo.setAccountBalance(1000.00);
        this.testAccountTwo.setUserId(testUserTwo.getId());
        this.testAccountTwo = insertTestAccount(testAccountTwo);
    }


    @Test
    public void get_balance_by_user_id(){
        Double expectedResult = 1000.00;
        int userId = userDAO.findIdByUsername(testUserOne.getUsername());
        Double actualResult = jdbcAccountDAO.getBalanceByUserId(userId);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void get_all_users() {
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
        Transfer testTransfer = new Transfer();
        testTransfer.setAccountFrom(testAccountOne.getAccountId());
        testTransfer.setAccountTo(testAccountTwo.getAccountId());
        testTransfer.setAmount(500.00);
        testTransfer.setTransferStatusId(Long.valueOf(2));
        testTransfer.setTransferTypeId(Long.valueOf(2));
        testTransfer = jdbcAccountDAO.createTransfer(testTransfer);
        Assert.assertNotNull(testTransfer.getTransferId());
    }

    @Test
    public void update_balances(){
        double amountToTransfer = 500.00;
        double expectedSenderResult = testAccountOne.getAccountBalance() - 500.00;
        double expectedReceiverResult = testAccountTwo.getAccountBalance() + 500.00;
        Transfer testTransfer = new Transfer();
        testTransfer.setAccountFrom(testAccountOne.getAccountId());
        testTransfer.setAccountTo(testAccountTwo.getAccountId());
        testTransfer.setAmount(500.00);
        testTransfer.setTransferStatusId(Long.valueOf(2));
        testTransfer.setTransferTypeId(Long.valueOf(2));
        testTransfer = jdbcAccountDAO.createTransfer(testTransfer);
        jdbcAccountDAO.updateBalances(testTransfer);
        Account senderResultAccount = jdbcAccountDAO.getAccountByAccountID(testAccountOne.getAccountId());
        Account receiverResultAccount = jdbcAccountDAO.getAccountByAccountID(testAccountTwo.getAccountId());
        double actualSenderResult = senderResultAccount.getAccountBalance();
        double actualReceiverResult = receiverResultAccount.getAccountBalance();
        Assert.assertEquals(expectedSenderResult, actualSenderResult, .009);
        Assert.assertEquals(expectedReceiverResult, actualReceiverResult, .009);
    }

    @Test
    public void get_transfers_by_user_id(){
        jdbcTemplate.update("TRUNCATE transfers CASCADE");

        Transfer testTransfer = new Transfer();
        testTransfer.setAccountFrom(testAccountOne.getAccountId());
        testTransfer.setAccountTo(testAccountTwo.getAccountId());
        testTransfer.setAmount(500.00);
        testTransfer.setTransferStatusId(Long.valueOf(2));
        testTransfer.setTransferTypeId(Long.valueOf(2));
        testTransfer.setSenderName(testUserOne.getUsername());
        testTransfer.setReceiverName(testUserTwo.getUsername());
        testTransfer = jdbcAccountDAO.createTransfer(testTransfer);

        Transfer testTransferTwo = new Transfer();
        testTransferTwo.setAccountFrom(testAccountOne.getAccountId());
        testTransferTwo.setAccountTo(testAccountTwo.getAccountId());
        testTransferTwo.setAmount(750.00);
        testTransferTwo.setTransferStatusId(Long.valueOf(2));
        testTransferTwo.setTransferTypeId(Long.valueOf(2));
        testTransferTwo.setSenderName(testUserOne.getUsername());
        testTransferTwo.setReceiverName(testUserTwo.getUsername());
        testTransferTwo = jdbcAccountDAO.createTransfer(testTransferTwo);

        List<Transfer> expectedResult = new ArrayList<Transfer>();
        expectedResult.add(testTransfer);
        expectedResult.add(testTransferTwo);
        List<Transfer> actualResult = jdbcAccountDAO.getTransfersByUserId(testUserOne.getId());

        Assert.assertEquals(expectedResult, actualResult);


    }

    private Account insertTestAccount(Account account) {
        String sql = "INSERT INTO accounts (account_id, user_id, balance) VALUES (default, ?, ?) RETURNING account_id";
        Long accountId = jdbcTemplate.queryForObject(sql, Long.class, account.getUserId(), account.getAccountBalance());
        account.setAccountId(accountId);

        return account;
    }


}
