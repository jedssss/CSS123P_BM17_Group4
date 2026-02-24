package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.sql.*;

public class SupplierManager extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtId, txtName, txtContact;
    private JTable table;
    private DefaultTableModel tableModel;
    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static String query;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SupplierManager window = new SupplierManager();
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

    public SupplierManager() {
        initialize();
        dbConnect();
        loadTable();
    }

    private void initialize() {
        setTitle("Supplier Management");
        setBounds(100, 100, 650, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblId = new JLabel("Supplier ID:");
        lblId.setBounds(10, 10, 100, 25);
        getContentPane().add(lblId);
        txtId = new JTextField();
        txtId.setBounds(120, 10, 150, 25);
        getContentPane().add(txtId);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(10, 40, 100, 25);
        getContentPane().add(lblName);
        txtName = new JTextField();
        txtName.setBounds(120, 40, 150, 25);
        getContentPane().add(txtName);

        JLabel lblContact = new JLabel("Contact Info:");
        lblContact.setBounds(10, 70, 100, 25);
        getContentPane().add(lblContact);
        txtContact = new JTextField();
        txtContact.setBounds(120, 70, 150, 25);
        getContentPane().add(txtContact);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(10, 110, 80, 25);
        btnAdd.addActionListener(e -> addSupplier());
        getContentPane().add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(100, 110, 80, 25);
        btnUpdate.addActionListener(e -> updateSupplier());
        getContentPane().add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(190, 110, 80, 25);
        btnDelete.addActionListener(e -> deleteSupplier());
        getContentPane().add(btnDelete);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(280, 110, 80, 25);
        btnRefresh.addActionListener(e -> loadTable());
        getContentPane().add(btnRefresh);

        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Supplier ID", "Name", "Contact Info"}, 0);
        table.setModel(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtContact.setText(tableModel.getValueAt(row, 2).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(280, 10, 330, 90);
        getContentPane().add(scrollPane);
    }

    private void loadTable() {
        try {
            query = "SELECT * FROM supplier";
            rs = stmt.executeQuery(query);
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("supplierId"),
                        rs.getString("name"),
                        rs.getString("contactInfo")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSupplier() {
        String id = txtId.getText();
        String name = txtName.getText();
        String contact = txtContact.getText();
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID and Name are required.");
            return;
        }
        try {
            query = "INSERT INTO supplier (supplierId, name, contactInfo) VALUES ('"
                    + id + "','" + name + "','" + contact + "')";
            stmt.executeUpdate(query);
            loadTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Supplier added.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateSupplier() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a supplier to update.");
            return;
        }
        String id = txtId.getText();
        String name = txtName.getText();
        String contact = txtContact.getText();
        try {
            query = "UPDATE supplier SET name='" + name + "', contactInfo='" + contact
                    + "' WHERE supplierId='" + id + "'";
            stmt.executeUpdate(query);
            loadTable();
            JOptionPane.showMessageDialog(this, "Supplier updated.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteSupplier() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete supplier " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                query = "DELETE FROM supplier WHERE supplierId='" + id + "'";
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
        txtContact.setText("");
    }
}