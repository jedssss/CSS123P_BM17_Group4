package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.sql.*;

public class CustomerManager extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtId, txtName, txtEmail, txtPhone;
    private JTable table;
    private DefaultTableModel tableModel;
    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static String query;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                CustomerManager window = new CustomerManager();
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

    public CustomerManager() {
        initialize();
        dbConnect();
        loadTable();
    }

    private void initialize() {
        setTitle("Customer Management");
        setBounds(100, 100, 700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblId = new JLabel("ID:");
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

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(10, 70, 80, 25);
        getContentPane().add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(100, 70, 150, 25);
        getContentPane().add(txtEmail);

        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setBounds(10, 100, 80, 25);
        getContentPane().add(lblPhone);
        txtPhone = new JTextField();
        txtPhone.setBounds(100, 100, 150, 25);
        getContentPane().add(txtPhone);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(10, 140, 80, 25);
        btnAdd.addActionListener(e -> addCustomer());
        getContentPane().add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(100, 140, 80, 25);
        btnUpdate.addActionListener(e -> updateCustomer());
        getContentPane().add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(190, 140, 80, 25);
        btnDelete.addActionListener(e -> deleteCustomer());
        getContentPane().add(btnDelete);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(280, 140, 80, 25);
        btnRefresh.addActionListener(e -> loadTable());
        getContentPane().add(btnRefresh);

        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0);
        table.setModel(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtEmail.setText(tableModel.getValueAt(row, 2).toString());
                txtPhone.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(280, 10, 380, 120);
        getContentPane().add(scrollPane);
    }

    private void loadTable() {
        try {
            query = "SELECT * FROM customer";
            rs = stmt.executeQuery(query);
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("customerId"),
                        rs.getString("customerName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addCustomer() {
        String id = txtId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID and Name are required.");
            return;
        }
        try {
            query = "INSERT INTO customer (customerId, customerName, email, phoneNumber) VALUES ('"
                    + id + "','" + name + "','" + email + "','" + phone + "')";
            stmt.executeUpdate(query);
            loadTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Customer added.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to update.");
            return;
        }
        String id = txtId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        try {
            query = "UPDATE customer SET customerName='" + name + "', email='" + email
                    + "', phoneNumber='" + phone + "' WHERE customerId='" + id + "'";
            stmt.executeUpdate(query);
            loadTable();
            JOptionPane.showMessageDialog(this, "Customer updated.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete customer " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                query = "DELETE FROM customer WHERE customerId='" + id + "'";
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
        txtEmail.setText("");
        txtPhone.setText("");
    }
}