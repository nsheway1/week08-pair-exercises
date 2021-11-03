package com.techelevator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.techelevator.tenmo.auth.model.User;
import com.techelevator.tenmo.dao.JDBCAccountDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

public class JDBCAccountDAOIntegrationTests extends DAOIntegrationTest{

    private JDBCAccountDAO jdbcAccountDAO;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        this.jdbcTemplate = new JdbcTemplate(this.getDataSource());
        this.jdbcAccountDAO = new JDBCAccountDAO(jdbcTemplate);
    }


    @Test
    public void get_balance_by_user_id(){
        Double expectedResult = 1000.00;
        int userId = (1001);
        Double actualResult = jdbcAccountDAO.getBalanceByUserId(userId);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllUsers(){
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

    private User insertTestUser(User user){
        String sql = "INSERT INTO users VALUES (DEFAULT, ?, ?) RETURNING user_id";
        Long userId = jdbcTemplate.queryForObject(sql, Long.class, user.getUsername(), user.getPassword());
        user.setId(userId);
        return user;

    }



}
