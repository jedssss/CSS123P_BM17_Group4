package com.javaswing.practicedb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnSubmit, btnClear;

    // Database objects
    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    static String query;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UserLogin frame = new UserLogin();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public UserLogin() {
        initialize();
        dbConnect();
    }

    private void initialize() {
        setTitle("Login - Sample Database");
        setBounds(100, 100, 389, 290);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(new Font("Dialog", Font.BOLD, 18));
        lblLogin.setBounds(141, 25, 68, 26);
        getContentPane().add(lblLogin);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Dialog", Font.PLAIN, 14));
        lblUsername.setBounds(78, 79, 100, 17);
        getContentPane().add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Dialog", Font.PLAIN, 14));
        txtUsername.setBounds(167, 67, 126, 29);
        getContentPane().add(txtUsername);
        txtUsername.setColumns(10);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Dialog", Font.PLAIN, 14));
        lblPassword.setBounds(78, 113, 100, 14);
        getContentPane().add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Dialog", Font.PLAIN, 14));
        txtPassword.setBounds(167, 98, 126, 29);
        getContentPane().add(txtPassword);

        chkShowPassword = new JCheckBox("show password");
        chkShowPassword.setFont(new Font("Dialog", Font.PLAIN, 14));
        chkShowPassword.setBounds(167, 134, 126, 23);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected())
                txtPassword.setEchoChar((char) 0);
            else
                txtPassword.setEchoChar('*');
        });
        getContentPane().add(chkShowPassword);

        btnSubmit = new JButton("SUBMIT");
        btnSubmit.setFont(new Font("Dialog", Font.PLAIN, 14));
        btnSubmit.setBounds(73, 169, 105, 41);
        btnSubmit.addActionListener(e -> authenticate());
        getContentPane().add(btnSubmit);

        btnClear = new JButton("CLEAR");
        btnClear.setFont(new Font("Dialog", Font.PLAIN, 14));
        btnClear.setBounds(188, 169, 105, 41);
        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            chkShowPassword.setSelected(false);
            txtPassword.setEchoChar('*');
        });
        getContentPane().add(btnClear);
    }

    // Database connection method
    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void authenticate() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        try {
            // Simple query – for learning only. In production, use PreparedStatement.
            query = "SELECT * FROM tbluser WHERE uname='" + user + "' AND pword='" + pass + "'";
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                // Open Main Menu
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                dispose(); // close login window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}