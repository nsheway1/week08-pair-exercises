package com.techelevator.tenmo;

import com.techelevator.tenmo.auth.models.AuthenticatedUser;
import com.techelevator.tenmo.auth.models.UserCredentials;
import com.techelevator.tenmo.auth.services.AuthenticationService;
import com.techelevator.tenmo.auth.services.AuthenticationServiceException;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.ConsoleService;

import java.util.List;
import java.util.Map;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
        this.console = console;
        this.authenticationService = authenticationService;
    }

    public void run() {
        console.showWelcomeBanner();
        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        double balance = accountService.getBalance();
        console.displayAccountBalance(balance);
    }

    private void viewTransferHistory() {
        List<Transfer> transfers = accountService.viewPastTransfers();
        console.displayTransfers(transfers, currentUser.getUser().getUsername());
        Long transferId = console.askUserToSeeTransferDetails();
        if (!transferId.equals(Long.valueOf(0))) {
            Transfer chosenTransfer = getUserChoiceTransfer(transfers, transferId);
            if (chosenTransfer == null) {
                console.invalidInputMessage();
            } else {
                console.displayTransferDetails(chosenTransfer);
            }
        }
    }

    private Transfer getUserChoiceTransfer(List<Transfer> transfers, Long transferId) {
        for (Transfer transfer : transfers) {
            if (transfer.getTransferId().equals(transferId)) {
                return transfer;
            }
        }
        return null;
    }

    private void viewPendingRequests() {
        List<Transfer> transfers = accountService.viewPendingTransfers();
        console.displayTransfers(transfers, currentUser.getUser().getUsername());
        Long transferId = console.askUserWhichTransferToApproveOrReject();
        if (!transferId.equals(Long.valueOf(0))) {
            Transfer chosenTransfer = getUserChoiceTransfer(transfers, transferId);
            if (chosenTransfer == null) {
                console.invalidInputMessage();
            } else {
                approveOrReject(chosenTransfer);
            }
        }
    }

    private void approveOrReject(Transfer chosenTransfer) {
        int userChoice = console.askUserToApproveOrReject();
        if (userChoice == 1) {
            if(chosenTransfer.getReceiverName().equals(currentUser.getUser().getUsername())){
                console.cannotApproveMessage();
            }else if(accountService.getBalance() < chosenTransfer.getAmount()){
                console.insufficientFundsMessage();
            }else{
                chosenTransfer.setTransferStatusId(Long.valueOf(2));
                console.displayTransferSuccess();
            }
        } else if (userChoice == 2){
            chosenTransfer.setTransferStatusId(Long.valueOf(3));
        }
        accountService.updateTransfer(chosenTransfer);
    }


    private void sendBucks() {
        boolean isRequest = false;
        Map<Long, String> availableUsers = accountService.getAvailableUsers();
        Long receivingUserId = console.displayAvailableUsersAndGetUserToTransferTo(availableUsers);
        if (!receivingUserId.equals(Long.valueOf(0))) {
            double amountToSend = console.askUserHowMuchToTransfer();
            Transfer transfer = accountService.createTransfer(receivingUserId, amountToSend, isRequest);
            if (transfer == null) {
                console.insufficientFundsMessage();
            } else {
                console.displayTransferSuccess();
            }
        }
    }

    private void requestBucks() {
        boolean isRequest = true;
        Map<Long, String> availableUsers = accountService.getAvailableUsers();
        Long receivingUserId = console.displayAvailableUsersAndGetUserToTransferTo(availableUsers);
        if (!receivingUserId.equals(Long.valueOf(0))) {
            double amountToRequest = console.askUserHowMuchToTransfer();
            Transfer transfer = accountService.createTransfer(receivingUserId, amountToRequest, isRequest);
        }
    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        console.displayMessage("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                console.displayMessage("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                console.showRegistrationFailed(e.getMessage());
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
                accountService = new AccountService(API_BASE_URL, currentUser, console);
            } catch (AuthenticationServiceException e) {
                console.showLoginFailed(e.getMessage());
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
