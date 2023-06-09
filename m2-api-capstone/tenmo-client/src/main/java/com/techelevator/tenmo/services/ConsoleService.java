package com.techelevator.tenmo.services;


import com.techelevator.tenmo.models.Transfer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleService {

    private NumberFormat currency = NumberFormat.getCurrencyInstance();
    private PrintWriter out;
    private Scanner in;

    public ConsoleService(InputStream input, OutputStream output) {
        this.out = new PrintWriter(output, true);
        this.in = new Scanner(input);
    }

    public void showWelcomeBanner() {
        out.println("*********************");
        out.println("* Welcome to TEnmo! *");
        out.println("*********************");
        out.flush();
    }

    public void displayMessage(String message) {
        out.println(message);
        out.flush();
    }

    public void showRegistrationFailed(String message) {
        out.println("REGISTRATION ERROR: " + message);
        out.println("Please attempt to register again.");
        out.flush();
    }

    public void showLoginFailed(String message) {
        out.println("LOGIN ERROR: " + message);
        out.println("Please attempt to login again.");
        out.flush();
    }

    public Object getChoiceFromOptions(Object[] options) {
        Object choice = null;
        while (choice == null) {
            displayMenuOptions(options);
            choice = getChoiceFromUserInput(options);
        }
        out.println();
        return choice;
    }

    private Object getChoiceFromUserInput(Object[] options) {
        Object choice = null;
        String userInput = in.nextLine();
        try {
            int selectedOption = Integer.valueOf(userInput);
            if (selectedOption > 0 && selectedOption <= options.length) {
                choice = options[selectedOption - 1];
            }
        } catch (NumberFormatException e) {
            // eat the exception, an error message will be displayed below since choice will be null
        }
        if (choice == null) {
            out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
        }
        return choice;
    }

    private void displayMenuOptions(Object[] options) {
        out.println();
        for (int i = 0; i < options.length; i++) {
            int optionNum = i + 1;
            out.println(optionNum + ") " + options[i]);
        }
        out.print(System.lineSeparator() + "Please choose an option >>> ");
        out.flush();
    }

    public String getUserInput(String prompt) {
        out.print(prompt + ": ");
        out.flush();
        return in.nextLine();
    }

    public Integer getUserInputInteger(String prompt) {
        Integer result = null;
        do {
            out.print(prompt + ": ");
            out.flush();
            String userInput = in.nextLine();
            try {
                result = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
            }
        } while (result == null);
        return result;
    }

    public void displayAccountBalance(double balance) {
        System.out.println("Current balance is: " + currency.format(balance));
    }

    public Long displayAvailableUsersAndGetUserToTransferTo(Map<Long, String> availableUsers) {
        System.out.println("-----------------------------");
        System.out.println("User's");
        System.out.printf("%-8s %-30s %n", "ID", "Name");
        System.out.println("-----------------------------");
        for (Map.Entry<Long, String> entry : availableUsers.entrySet()) {
            System.out.printf("%-8s %-30s %n", entry.getKey(), entry.getValue());
        }
        System.out.println("-----------------------------");
        System.out.println("Enter ID of the user you are sending to (0 to cancel):");
        Long id = in.nextLong();
        in.nextLine();
        return id;
    }

    public double askUserHowMuchToTransfer() {
        System.out.println("Enter amount:");
        double transferAmount = in.nextDouble();
        in.nextLine();
        return transferAmount;
    }

    public void displayTransferSuccess() {
        System.out.println();
        System.out.println("Transfer successful.");
    }

    public void insufficientFundsMessage() {
        System.out.println();
        System.out.println("Insufficient funds.");
    }

    public void displayTransfers(List<Transfer> transfers, String username) {
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.println("Transfers");
        System.out.printf("%-10s %-30s %-15s %n", "ID", "From/To", "Amount");
        System.out.println("-------------------------------------------------");
        for (Transfer transfer : transfers) {
            if (transfer.getSenderName().equals(username)) {
                System.out.printf("%-10s %-30s %-15s %n", transfer.getTransferId(), "To: " + transfer.getReceiverName(),
                        currency.format(transfer.getAmount()));
            } else {
                System.out.printf("%-10s %-30s %-15s %n", transfer.getTransferId(), "From: " + transfer.getSenderName(),
                        currency.format(transfer.getAmount()));
            }

        }

    }

    public Long askUserToSeeTransferDetails() {
        System.out.println();
        System.out.println("Please enter transfer ID to view details (0 to cancel): ");
        Long id = in.nextLong();
        in.nextLine();
        return id;
    }

    public void displayTransferDetails(Transfer transfer) {
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("-------------------------------------------------");
        System.out.println("Id: " + transfer.getTransferId());
        System.out.println("From: " + transfer.getSenderName());
        System.out.println("To: " + transfer.getReceiverName());
        System.out.println("Type: " + transfer.getTypeName());
        System.out.println("Status: " + transfer.getStatusName());
        System.out.println("Amount: " + currency.format(transfer.getAmount()));
    }

    public void invalidInputMessage() {
        System.out.println();
        System.out.println("Invalid Input. Please try again");
    }

    public Long askUserWhichTransferToApproveOrReject() {
        System.out.println();
        System.out.println("Please enter transfer ID to approve/reject (0 to cancel):");
        Long id = in.nextLong();
        in.nextLine();
        return id;
    }

    public int askUserToApproveOrReject(){
        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("---------------------------");
        System.out.println("Please choose an option:");

        int userChoice = in.nextInt();
        in.nextLine();
        return userChoice;
    }

    public void cannotApproveMessage(){
        System.out.println();
        System.out.println("You cannot approve your own request. Nice try.");
    }


}
