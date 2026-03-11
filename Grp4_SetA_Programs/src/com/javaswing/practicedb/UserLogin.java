package com.javaswing.practicedb;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;

    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    static String query;

    public static void main(String[] args) {
        Theme.applyGlobalDefaults();
        EventQueue.invokeLater(() -> {
            try { new UserLogin().setVisible(true); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public UserLogin() {
        initialize();
        dbConnect();
    }

    private void initialize() {
        setTitle("Login");
        setSize(440, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_DARK);
        getContentPane().setLayout(new GridBagLayout());

        // Card
        JPanel card = new JPanel(null);
        card.setBackground(Theme.BG_PANEL);
        card.setPreferredSize(new Dimension(380, 360));
        card.setBorder(new Theme.RoundedBorder(Theme.BORDER, 16));

        // Gradient top strip
        JPanel strip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Theme.ACCENT, getWidth(), 0, Theme.ACCENT_LIGHT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);
                g2.dispose();
            }
        };
        strip.setBounds(0, 0, 380, 6);
        card.add(strip);

        JLabel ico = new JLabel("🔒");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        ico.setBounds(150, 28, 80, 40);
        ico.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(ico);

        JLabel heading = Theme.titleLabel("Welcome Back");
        heading.setBounds(0, 72, 380, 34);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(heading);

        JLabel sub = Theme.label("Sign in to continue");
        sub.setBounds(0, 108, 380, 22);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(sub);

        JLabel lblUser = Theme.label("Username");
        lblUser.setBounds(40, 148, 300, 18);
        card.add(lblUser);
        txtUsername = Theme.textField();
        txtUsername.setBounds(40, 168, 300, 36);
        card.add(txtUsername);

        JLabel lblPass = Theme.label("Password");
        lblPass.setBounds(40, 215, 300, 18);
        card.add(lblPass);
        txtPassword = Theme.passwordField();
        txtPassword.setBounds(40, 235, 300, 36);
        card.add(txtPassword);

        chkShowPassword = new JCheckBox("Show password");
        chkShowPassword.setBounds(40, 278, 180, 24);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFont(Theme.FONT_SMALL);
        chkShowPassword.setForeground(Theme.TEXT_SECONDARY);
        chkShowPassword.addActionListener(e -> txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '*'));
        card.add(chkShowPassword);

        JButton btnSubmit = Theme.primaryButton("Sign In");
        btnSubmit.setBounds(40, 312, 140, 36);
        btnSubmit.addActionListener(e -> authenticate());
        card.add(btnSubmit);

        JButton btnClear = Theme.ghostButton("Clear");
        btnClear.setBounds(200, 312, 140, 36);
        btnClear.addActionListener(e -> {
            txtUsername.setText(""); txtPassword.setText("");
            chkShowPassword.setSelected(false); txtPassword.setEchoChar('*');
        });
        card.add(btnClear);

        getContentPane().add(card);
    }

    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        }
    }

    private void authenticate() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password."); return;
        }
        try {
            query = "SELECT * FROM tbluser WHERE uname='" + user + "' AND pword='" + pass + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                new AdminMenu().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
