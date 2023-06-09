package com.techelevator.tenmo.models;

public class Transfer {

    private Long transferId;
    private Long transferTypeId;
    private Long transferStatusId;
    private Long accountFrom;
    private Long accountTo;
    private double amount;
    private String senderName;
    private String receiverName;

    public String getTypeName(){
        if(transferTypeId.equals(Long.valueOf(1))){
            return "Request";
        }else {
            return "Send";
        }
    }

    public String getStatusName(){
        if(transferStatusId.equals(Long.valueOf(1))){
            return "Pending";
        }else if(transferStatusId.equals(Long.valueOf(2))){
            return "Approved";
        }else{
            return "Rejected";
        }
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(Long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public Long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(Long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public Long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Long accountTo) {
        this.accountTo = accountTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
