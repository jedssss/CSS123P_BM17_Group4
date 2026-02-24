package com.javaswing.practicedb;

import javax.swing.SwingUtilities;

public class SetAProductInventoryManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserLogin login = new UserLogin();
            login.setVisible(true);
        });
    }
}