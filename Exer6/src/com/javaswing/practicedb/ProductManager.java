package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.sql.*;

public class ProductManager extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtId, txtName, txtPrice, txtStock;
    private JComboBox<String> cmbSupplier;
    private JTable table;
    private DefaultTableModel tableModel;
    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static String query;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ProductManager window = new ProductManager();
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

    public ProductManager() {
        initialize();
        dbConnect();
        loadSuppliers();
        loadTable();
    }

    private void initialize() {
        setTitle("Product Management");
        setBounds(100, 100, 800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblId = new JLabel("Product ID:");
        lblId.setBounds(10, 10, 80, 25);
        getContentPane().add(lblId);
        txtId = new JTextField();
        txtId.setBounds(100, 10, 150, 25);
        getContentPane().add(txtId);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(10, 40, 80, 25);
        getContentPane().add(lblName);
        txtName = new JTextField();
        txtName.setBounds(100, 40, 150, 25);
        getContentPane().add(txtName);

        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setBounds(10, 70, 80, 25);
        getContentPane().add(lblPrice);
        txtPrice = new JTextField();
        txtPrice.setBounds(100, 70, 150, 25);
        getContentPane().add(txtPrice);

        JLabel lblStock = new JLabel("Stock:");
        lblStock.setBounds(10, 100, 80, 25);
        getContentPane().add(lblStock);
        txtStock = new JTextField();
        txtStock.setBounds(100, 100, 150, 25);
        getContentPane().add(txtStock);

        JLabel lblSupplier = new JLabel("Supplier:");
        lblSupplier.setBounds(10, 130, 80, 25);
        getContentPane().add(lblSupplier);
        cmbSupplier = new JComboBox<>();
        cmbSupplier.setBounds(100, 130, 150, 25);
        getContentPane().add(cmbSupplier);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(10, 170, 80, 25);
        btnAdd.addActionListener(e -> addProduct());
        getContentPane().add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(100, 170, 80, 25);
        btnUpdate.addActionListener(e -> updateProduct());
        getContentPane().add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(190, 170, 80, 25);
        btnDelete.addActionListener(e -> deleteProduct());
        getContentPane().add(btnDelete);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(280, 170, 80, 25);
        btnRefresh.addActionListener(e -> loadTable());
        getContentPane().add(btnRefresh);

        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Stock", "Supplier"}, 0);
        table.setModel(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString());
                txtStock.setText(tableModel.getValueAt(row, 3).toString());
                cmbSupplier.setSelectedItem(tableModel.getValueAt(row, 4).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(280, 10, 480, 150);
        getContentPane().add(scrollPane);
    }

    private void loadSuppliers() {
        try {
            query = "SELECT supplierId, name FROM supplier";
            rs = stmt.executeQuery(query);
            cmbSupplier.removeAllItems();
            while (rs.next()) {
                cmbSupplier.addItem(rs.getString("supplierId") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void addProduct() {
        String id = txtId.getText();
        String name = txtName.getText();
        String priceStr = txtPrice.getText();
        String stockStr = txtStock.getText();
        String supplierItem = (String) cmbSupplier.getSelectedItem();
        String supplierId = supplierItem != null ? supplierItem.split(" - ")[0] : null;

        if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields except supplier are required.");
            return;
        }
        double price;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numbers.");
            return;
        }

        try {
            query = "INSERT INTO product (productId, name, price, stockQuantity, supplierId) VALUES ('"
                    + id + "','" + name + "'," + price + "," + stock + ",'" + supplierId + "')";
            stmt.executeUpdate(query);
            loadTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product added.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
            return;
        }
        String id = txtId.getText();
        String name = txtName.getText();
        String priceStr = txtPrice.getText();
        String stockStr = txtStock.getText();
        String supplierItem = (String) cmbSupplier.getSelectedItem();
        String supplierId = supplierItem != null ? supplierItem.split(" - ")[0] : null;

        double price;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numbers.");
            return;
        }

        try {
            query = "UPDATE product SET name='" + name + "', price=" + price + ", stockQuantity=" + stock
                    + ", supplierId='" + supplierId + "' WHERE productId='" + id + "'";
            stmt.executeUpdate(query);
            loadTable();
            JOptionPane.showMessageDialog(this, "Product updated.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete product " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                query = "DELETE FROM product WHERE productId='" + id + "'";
                stmt.executeUpdate(query);
                loadTable();
                clearFields();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        cmbSupplier.setSelectedIndex(-1);
    }
}