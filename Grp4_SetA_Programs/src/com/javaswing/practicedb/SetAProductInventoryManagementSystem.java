package com.javaswing.practicedb;

import javax.swing.SwingUtilities;

public class SetAProductInventoryManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PublicView publicView = new PublicView();
            publicView.setVisible(true);
        });
    }
}