package com.javaswing.practicedb;

import java.util.Date;

public class Cash extends Payment {
    private double cashReceived;

    public Cash(double amount, Date paymentDate, double cashReceived) {
        super(amount, paymentDate);
        this.cashReceived = cashReceived;
    }

    @Override
    public boolean processPayment() {
        return cashReceived >= amount;
    }

    public double getCashReceived() { return cashReceived; }
    public void setCashReceived(double cashReceived) { this.cashReceived = cashReceived; }
}