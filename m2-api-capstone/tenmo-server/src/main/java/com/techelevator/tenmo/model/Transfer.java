package com.techelevator.tenmo.model;

import java.util.Objects;

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
        if(transferTypeId.equals(1)){
            return "Request";
        }else {
            return "Send";
        }
    }

    public String getStatusName(){
        if(transferStatusId.equals(1)){
            return "Pending";
        }else if(transferStatusId.equals(2)){
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Double.compare(transfer.amount, amount) == 0 && Objects.equals(transferId, transfer.transferId) && Objects.equals(transferTypeId, transfer.transferTypeId) && Objects.equals(transferStatusId, transfer.transferStatusId) && Objects.equals(accountFrom, transfer.accountFrom) && Objects.equals(accountTo, transfer.accountTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId, transferTypeId, transferStatusId, accountFrom, accountTo, amount);
    }
}
