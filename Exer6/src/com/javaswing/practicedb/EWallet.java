package com.javaswing.practicedb;

import java.util.Date;

public class EWallet extends Payment {
    private String walletId;
    private String provider;

    public EWallet(double amount, Date paymentDate, String walletId, String provider) {
        super(amount, paymentDate);
        this.walletId = walletId;
        this.provider = provider;
    }

    @Override
    public boolean processPayment() {
        // Assume payment always succeeds if walletId is present
        return walletId != null && !walletId.isEmpty();
    }

    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}