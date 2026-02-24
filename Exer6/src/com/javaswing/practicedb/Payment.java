package com.javaswing.practicedb;

import java.util.Date;

public abstract class Payment {
    protected double amount;
    protected Date paymentDate;

    public Payment(double amount, Date paymentDate) {
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public abstract boolean processPayment();

    public double getAmount() { return amount; }
    public Date getPaymentDate() { return paymentDate; }
}