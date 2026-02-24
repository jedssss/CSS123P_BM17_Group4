package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.sql.*;

public class InventoryManagerFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtProductId, txtQuantity;
    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static String query;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                InventoryManagerFrame window = new InventoryManagerFrame();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InventoryManagerFrame() {
        initialize();
        dbConnect();
        loadTable();
    }

    private void initialize() {
        setTitle("Inventory Management");
        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblProductId = new JLabel("Product ID:");
        lblProductId.setBounds(10, 10, 80, 25);
        getContentPane().add(lblProductId);
        txtProductId = new JTextField();
        txtProductId.setBounds(100, 10, 150, 25);
        getContentPane().add(txtProductId);

        JLabel lblQuantity = new JLabel("Quantity to add:");
        lblQuantity.setBounds(10, 40, 100, 25);
        getContentPane().add(lblQuantity);
        txtQuantity = new JTextField();
        txtQuantity.setBounds(120, 40, 100, 25);
        getContentPane().add(txtQuantity);

        JButton btnRestock = new JButton("Restock");
        btnRestock.setBounds(10, 70, 100, 25);
        btnRestock.addActionListener(e -> restock());
        getContentPane().add(btnRestock);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(120, 70, 100, 25);
        btnRefresh.addActionListener(e -> loadTable());
        getContentPane().add(btnRefresh);

        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Price", "Stock", "Supplier"}, 0);
        table.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 110, 550, 200);
        getContentPane().add(scrollPane);
    }

    private void loadTable() {
        try {
            query = "SELECT p.*, s.name AS supplierName FROM product p LEFT JOIN supplier s ON p.supplierId = s.supplierId";
            rs = stmt.executeQuery(query);
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("productId"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stockQuantity"),
                        rs.getString("supplierName") != null ? rs.getString("supplierName") : ""
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void restock() {
        String productId = txtProductId.getText();
        String qtyStr = txtQuantity.getText();
        if (productId.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter product ID and quantity.");
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
            return;
        }

        try {
            query = "UPDATE product SET stockQuantity = stockQuantity + " + qty + " WHERE productId='" + productId + "'";
            int affected = stmt.executeUpdate(query);
            if (affected > 0) {
                loadTable();
                txtProductId.setText("");
                txtQuantity.setText("");
                JOptionPane.showMessageDialog(this, "Stock updated.");
            } else {
                JOptionPane.showMessageDialog(this, "Product ID not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}