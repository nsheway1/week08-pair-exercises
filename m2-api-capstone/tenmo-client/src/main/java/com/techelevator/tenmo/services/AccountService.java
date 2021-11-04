package com.techelevator.tenmo.services;

import com.techelevator.tenmo.auth.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

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
        double balance = restTemplate.exchange(baseUrl + "/account_balance", HttpMethod.GET, entity, Double.class).getBody();
        return balance;
    }


    public Transfer createTransfer(Long id, double amount) {
        Transfer transfer = makeNewTransfer(id, amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        transfer = restTemplate.postForObject(baseUrl + "/transfers", entity, Transfer.class);
        return transfer;

    }

    private Transfer makeNewTransfer(Long id, double amount){
        Transfer transfer = new Transfer();
        transfer.setAccountTo(id);
        transfer.setAmount(amount);
        transfer.setAccountFrom(Long.valueOf(currentUser.getUser().getId()));
        transfer.setTransferStatusId(Long.valueOf(2));
        transfer.setTransferTypeId(Long.valueOf(2));
        return transfer;
    }

    private HttpHeaders makeAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return headers;
    }


}
