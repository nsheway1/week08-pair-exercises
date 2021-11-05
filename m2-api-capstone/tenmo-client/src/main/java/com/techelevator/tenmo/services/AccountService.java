package com.techelevator.tenmo.services;

import com.techelevator.tenmo.auth.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AccountService {

    private RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;
    private AuthenticatedUser currentUser;

    public AccountService(String baseUrl, AuthenticatedUser currentUser) {
        this.baseUrl = baseUrl;
        this.currentUser = currentUser;
    }

    public double getBalance() {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        double balance = restTemplate.exchange(baseUrl + "/account/balance", HttpMethod.GET, entity, Double.class).getBody();
        return balance;
    }

    public Map<Long, String> getAvailableUsers() {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        Map<Long, String> availableUsers = restTemplate.exchange(baseUrl + "/availableUsers",
                HttpMethod.GET, entity, Map.class).getBody();
        return availableUsers;
    }

    public Transfer createTransfer(Long id, double amount) {
        if (amount > getBalance()) {
            return null;
        }
        Transfer transfer = makeNewTransfer(id, amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        transfer = restTemplate.postForObject(baseUrl + "/sendTEnmoBucks", entity, Transfer.class);
        return transfer;
    }

    private Transfer makeNewTransfer(Long id, double amount) {
        Transfer transfer = new Transfer();
        Account accountTo = getAccount(id);
        Account accountFrom = getAccount(Long.valueOf(currentUser.getUser().getId()));
        transfer.setAccountTo(accountTo.getAccountId());
        transfer.setAmount(amount);
        transfer.setAccountFrom(accountFrom.getAccountId());
        transfer.setTransferStatusId(Long.valueOf(2));
        transfer.setTransferTypeId(Long.valueOf(2));
        return transfer;
    }

    private Account getAccount(Long id) {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        Account account = restTemplate.exchange(baseUrl + "/" + id + "/account", HttpMethod.GET,
                entity, Account.class).getBody();
        return account;
    }

    private HttpHeaders makeAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return headers;
    }

    public List<Transfer> viewTransfers() {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        List<Transfer> transfers = restTemplate.exchange(baseUrl+ "/transfers", HttpMethod.GET,
                entity, List.class).getBody();
        return transfers;
    }


}
