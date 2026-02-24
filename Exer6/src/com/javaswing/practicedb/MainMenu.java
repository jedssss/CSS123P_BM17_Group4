package com.javaswing.practicedb;

import javax.swing.*;
import java.awt.EventQueue;

public class MainMenu extends JFrame {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainMenu window = new MainMenu();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainMenu() {
        initialize();
    }

    private void initialize() {
        setTitle("Order Management System");
        setBounds(100, 100, 300, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JButton btnCustomers = new JButton("Manage Customers");
        btnCustomers.setBounds(50, 30, 180, 25);
        btnCustomers.addActionListener(e -> new CustomerManager().setVisible(true));
        getContentPane().add(btnCustomers);

        JButton btnSuppliers = new JButton("Manage Suppliers");
        btnSuppliers.setBounds(50, 60, 180, 25);
        btnSuppliers.addActionListener(e -> new SupplierManager().setVisible(true));
        getContentPane().add(btnSuppliers);

        JButton btnProducts = new JButton("Manage Products");
        btnProducts.setBounds(50, 90, 180, 25);
        btnProducts.addActionListener(e -> new ProductManager().setVisible(true));
        getContentPane().add(btnProducts);

        JButton btnOrders = new JButton("Place Order");
        btnOrders.setBounds(50, 120, 180, 25);
        btnOrders.addActionListener(e -> new OrderManager().setVisible(true));
        getContentPane().add(btnOrders);

        JButton btnInventory = new JButton("Inventory");
        btnInventory.setBounds(50, 150, 180, 25);
        btnInventory.addActionListener(e -> new InventoryManagerFrame().setVisible(true));
        getContentPane().add(btnInventory);

        JButton btnExit = new JButton("Exit");
        btnExit.setBounds(50, 180, 180, 25);
        btnExit.addActionListener(e -> System.exit(0));
        getContentPane().add(btnExit);
    }
}