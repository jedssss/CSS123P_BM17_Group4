package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        Theme.applyGlobalDefaults();
        EventQueue.invokeLater(() -> {
            try { new ProductManager().setVisible(true); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ProductManager() {
        initialize();
        dbConnect();
        loadSuppliers();
        loadTable();
    }

    private void initialize() {
        setTitle("Product Management");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        // ---- Top Bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_PANEL);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel title = Theme.titleLabel("Product Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topBar.add(title, BorderLayout.WEST);
        getContentPane().add(topBar, BorderLayout.NORTH);

        // ---- Content ----
        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left form
        JPanel formCard = new JPanel(null);
        formCard.setBackground(Theme.BG_PANEL);
        formCard.setBorder(new Theme.RoundedBorder(Theme.BORDER, 12));
        formCard.setPreferredSize(new Dimension(270, 0));

        JLabel formTitle = Theme.label("PRODUCT DETAILS");
        formTitle.setBounds(20, 20, 230, 20);
        formCard.add(formTitle);

        int y = 55;
        formCard.add(fieldLabel("Product ID", 20, y)); y += 20;
        txtId = Theme.textField(); txtId.setBounds(20, y, 230, 36); formCard.add(txtId); y += 52;

        formCard.add(fieldLabel("Name", 20, y)); y += 20;
        txtName = Theme.textField(); txtName.setBounds(20, y, 230, 36); formCard.add(txtName); y += 52;

        formCard.add(fieldLabel("Price", 20, y)); y += 20;
        txtPrice = Theme.textField(); txtPrice.setBounds(20, y, 230, 36); formCard.add(txtPrice); y += 52;

        formCard.add(fieldLabel("Stock Quantity", 20, y)); y += 20;
        txtStock = Theme.textField(); txtStock.setBounds(20, y, 230, 36); formCard.add(txtStock); y += 52;

        formCard.add(fieldLabel("Supplier", 20, y)); y += 20;
        cmbSupplier = Theme.comboBox(); cmbSupplier.setBounds(20, y, 230, 36); formCard.add(cmbSupplier); y += 52;

        JButton btnAdd = Theme.primaryButton("Add Product");
        btnAdd.setBounds(20, y, 230, 36); btnAdd.addActionListener(e -> addProduct()); formCard.add(btnAdd); y += 48;

        JButton btnUpdate = Theme.ghostButton("Update Selected");
        btnUpdate.setBounds(20, y, 230, 36); btnUpdate.addActionListener(e -> updateProduct()); formCard.add(btnUpdate); y += 48;

        JButton btnDelete = Theme.dangerButton("Delete Selected");
        btnDelete.setBounds(20, y, 230, 36); btnDelete.addActionListener(e -> deleteProduct()); formCard.add(btnDelete);

        content.add(formCard, BorderLayout.WEST);

        // Right table
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(Theme.BG_PANEL);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            new Theme.RoundedBorder(Theme.BORDER, 12),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel tblLabel = Theme.label("ALL PRODUCTS");
        tableTop.add(tblLabel, BorderLayout.WEST);
        JButton btnRefresh = Theme.ghostButton("⟳  Refresh");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.addActionListener(e -> { loadSuppliers(); loadTable(); });
        tableTop.add(btnRefresh, BorderLayout.EAST);
        tableCard.add(tableTop, BorderLayout.NORTH);

        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Stock", "Supplier"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(tableModel);
        Theme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString().replace("₱", ""));
                txtStock.setText(tableModel.getValueAt(row, 3).toString());
                cmbSupplier.setSelectedItem(tableModel.getValueAt(row, 4).toString());
            }
        });
        tableCard.add(Theme.scrollPane(table), BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);
        getContentPane().add(content, BorderLayout.CENTER);
    }

    private JLabel fieldLabel(String text, int x, int y) {
        JLabel l = Theme.label(text); l.setBounds(x, y, 230, 18); return l;
    }

    private void loadSuppliers() {
        try {
            query = "SELECT supplierId, name FROM supplier";
            rs = stmt.executeQuery(query);
            cmbSupplier.removeAllItems();
            while (rs.next()) cmbSupplier.addItem(rs.getString("supplierId") + " - " + rs.getString("name"));
        } catch (SQLException e) { e.printStackTrace(); }
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
                    String.format("₱%.2f", rs.getDouble("price")),
                    rs.getInt("stockQuantity"),
                    rs.getString("supplierName") != null ? rs.getString("supplierName") : "—"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void addProduct() {
        String id = txtId.getText().trim(), name = txtName.getText().trim();
        String priceStr = txtPrice.getText().trim(), stockStr = txtStock.getText().trim();
        String supplierItem = (String) cmbSupplier.getSelectedItem();
        String supplierId = supplierItem != null ? supplierItem.split(" - ")[0] : null;
        if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields except supplier are required."); return;
        }
        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            query = "INSERT INTO product (productId, name, price, stockQuantity, supplierId) VALUES ('"
                + id + "','" + name + "'," + price + "," + stock + ",'" + supplierId + "')";
            stmt.executeUpdate(query);
            loadTable(); clearFields();
            JOptionPane.showMessageDialog(this, "Product added.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numbers.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateProduct() {
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Select a product to update."); return; }
        String supplierItem = (String) cmbSupplier.getSelectedItem();
        String supplierId = supplierItem != null ? supplierItem.split(" - ")[0] : null;
        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            query = "UPDATE product SET name='" + txtName.getText() + "', price=" + price + ", stockQuantity=" + stock
                + ", supplierId='" + supplierId + "' WHERE productId='" + txtId.getText() + "'";
            stmt.executeUpdate(query);
            loadTable();
            JOptionPane.showMessageDialog(this, "Product updated.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numbers.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete product \"" + id + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                stmt.executeUpdate("DELETE FROM product WHERE productId='" + id + "'");
                loadTable(); clearFields();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtId.setText(""); txtName.setText(""); txtPrice.setText(""); txtStock.setText("");
        if (cmbSupplier.getItemCount() > 0) cmbSupplier.setSelectedIndex(0);
    }
}
