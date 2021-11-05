package com.techelevator.tenmo.services;

import com.techelevator.tenmo.auth.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.Console;
import java.util.*;

public class AccountService {

    private RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;
    private AuthenticatedUser currentUser;
    private ConsoleService console;

    public AccountService(String baseUrl, AuthenticatedUser currentUser, ConsoleService console) {
        this.baseUrl = baseUrl;
        this.currentUser = currentUser;
        this.console = console;
    }

    public double getBalance() {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        double balance = restTemplate.exchange(baseUrl + "/accounts/balance", HttpMethod.GET, entity, Double.class).getBody();
        return balance;
    }

    public Map<Long, String> getAvailableUsers() {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        Map<Long, String> availableUsers = restTemplate.exchange(baseUrl + "/availableUsers",
                HttpMethod.GET, entity, Map.class).getBody();
        return availableUsers;
    }

    public Transfer createTransfer(Long id, double amount, boolean isRequest) {
        if(!isRequest) {
            if (amount > getBalance()) {
                return null;
            }
        }
        Transfer transfer = makeNewTransfer(id, amount, isRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        transfer = restTemplate.postForObject(baseUrl + "/transfers", entity, Transfer.class);
        return transfer;
    }

    public Transfer updateTransfer (Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        try {
            restTemplate.put(baseUrl + "/transfers/" + transfer.getTransferId(), entity);
        } catch (RestClientResponseException ex) {
            console.displayMessage(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            console.displayMessage(ex.getMessage());
        }
        return transfer;
    }

    private Transfer makeNewTransfer(Long id, double amount, boolean isRequest) {
        Transfer transfer = new Transfer();
        Account accountTo;
        Account accountFrom;
        if(isRequest){
            accountTo = getAccount(Long.valueOf(currentUser.getUser().getId()));
            accountFrom = getAccount(id);
            transfer.setTransferTypeId(Long.valueOf(1));
            transfer.setTransferStatusId(Long.valueOf(1));
        }else {
            accountTo = getAccount(id);
            accountFrom = getAccount(Long.valueOf(currentUser.getUser().getId()));
            transfer.setTransferTypeId(Long.valueOf(2));
            transfer.setTransferStatusId(Long.valueOf(2));
        }
        transfer.setAccountTo(accountTo.getAccountId());
        transfer.setAmount(amount);
        transfer.setAccountFrom(accountFrom.getAccountId());
        return transfer;
    }

    private Account getAccount(Long id) {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        Account account = restTemplate.exchange(baseUrl + "/accounts/" + id, HttpMethod.GET,
                entity, Account.class).getBody();
        return account;
    }

    private HttpHeaders makeAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return headers;
    }

    public List<Transfer> viewPastTransfers() {
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        Transfer[] transfers = restTemplate.exchange(baseUrl+ "/transfers/pastTransfers", HttpMethod.GET,
                entity, Transfer[].class).getBody();
        return Arrays.asList(transfers);
    }

    public List<Transfer> viewPendingTransfers(){
        HttpEntity entity = new HttpEntity<>(makeAuthHeader());
        Transfer[] transfers = restTemplate.exchange(baseUrl+ "/transfers/pendingTransfers", HttpMethod.GET,
                entity, Transfer[].class).getBody();
        return Arrays.asList(transfers);
    }
}
