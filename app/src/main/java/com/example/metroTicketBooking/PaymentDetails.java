package com.example.metroTicketBooking;
public class PaymentDetails {
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String username;
    public String paymentId;
    public int amount;
    public long timestamp;
    public PaymentDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(PaymentDetails.class)
    }
    public PaymentDetails(String username, String paymentId, int amount, long timestamp) {
        this.username = username;
        this.paymentId = paymentId;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
