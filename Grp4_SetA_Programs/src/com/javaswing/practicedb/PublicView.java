package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PublicView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTable productTable;
    private DefaultTableModel tableModel;

    private JPanel publicPanel, loginPanel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;

    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static String query;

    public static void main(String[] args) {
        Theme.applyGlobalDefaults();
        EventQueue.invokeLater(() -> {
            try {
                PublicView frame = new PublicView();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PublicView() {
        initialize();
        dbConnect();
        loadProducts();
    }

    private void initialize() {
        setTitle("Inventory System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 540);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new CardLayout());
        getContentPane().setBackground(Theme.BG_DARK);

        buildPublicPanel();
        buildLoginPanel();

        getContentPane().add(publicPanel, "Public");
        getContentPane().add(loginPanel, "Login");
        showPublicPanel();
    }

    private void buildPublicPanel() {
        publicPanel = new JPanel(new BorderLayout(0, 0));
        publicPanel.setBackground(Theme.BG_DARK);

        // ---- Top Bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_PANEL);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoArea.setOpaque(false);
        JLabel dot = new JLabel("● ");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dot.setForeground(Theme.ACCENT_LIGHT);
        JLabel title = Theme.titleLabel("Product Inventory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoArea.add(dot);
        logoArea.add(title);
        topBar.add(logoArea, BorderLayout.WEST);

        JButton btnLogin = Theme.primaryButton("Admin Login →");
        btnLogin.setPreferredSize(new Dimension(130, 34));
        btnLogin.addActionListener(e -> showLoginPanel());
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(btnLogin);
        topBar.add(btnWrap, BorderLayout.EAST);

        publicPanel.add(topBar, BorderLayout.NORTH);

        // ---- Table Area ----
        JPanel centerArea = new JPanel(new BorderLayout());
        centerArea.setBackground(Theme.BG_DARK);
        centerArea.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JLabel tblTitle = Theme.label("ALL PRODUCTS");
        tblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        centerArea.add(tblTitle, BorderLayout.NORTH);

        productTable = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Price", "Stock", "Supplier"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable.setModel(tableModel);
        Theme.styleTable(productTable);

        JScrollPane sp = Theme.scrollPane(productTable);
        centerArea.add(sp, BorderLayout.CENTER);
        publicPanel.add(centerArea, BorderLayout.CENTER);

        // ---- Bottom Bar ----
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomBar.setBackground(Theme.BG_PANEL);
        JButton btnRefresh = Theme.ghostButton("⟳  Refresh");
        btnRefresh.setPreferredSize(new Dimension(110, 32));
        btnRefresh.addActionListener(e -> loadProducts());
        bottomBar.add(btnRefresh);
        publicPanel.add(bottomBar, BorderLayout.SOUTH);
    }

    private void buildLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Theme.BG_DARK);

        // Card
        JPanel card = new JPanel(null);
        card.setBackground(Theme.BG_PANEL);
        card.setPreferredSize(new Dimension(400, 370));
        card.setBorder(new Theme.RoundedBorder(Theme.BORDER, 16));

        // Accent top strip
        JPanel strip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0, Theme.ACCENT, getWidth(),0, Theme.ACCENT_LIGHT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.fillRect(0, getHeight()/2, getWidth(), getHeight()/2);
                g2.dispose();
            }
        };
        strip.setBounds(0, 0, 400, 6);
        card.add(strip);

        JLabel ico = new JLabel("⚙");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        ico.setForeground(Theme.ACCENT_LIGHT);
        ico.setBounds(160, 30, 80, 45);
        ico.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(ico);

        JLabel heading = Theme.titleLabel("Admin Login");
        heading.setBounds(0, 80, 400, 35);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(heading);

        JLabel sub = Theme.label("Sign in to manage inventory");
        sub.setBounds(0, 115, 400, 22);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(sub);

        JLabel lblUser = Theme.label("Username");
        lblUser.setBounds(50, 155, 300, 20);
        card.add(lblUser);
        txtUsername = Theme.textField();
        txtUsername.setBounds(50, 177, 300, 36);
        card.add(txtUsername);

        JLabel lblPass = Theme.label("Password");
        lblPass.setBounds(50, 222, 300, 20);
        card.add(lblPass);
        txtPassword = Theme.passwordField();
        txtPassword.setBounds(50, 244, 300, 36);
        card.add(txtPassword);

        chkShowPassword = new JCheckBox("Show password");
        chkShowPassword.setBounds(50, 285, 180, 24);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFont(Theme.FONT_SMALL);
        chkShowPassword.setForeground(Theme.TEXT_SECONDARY);
        chkShowPassword.addActionListener(e -> txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '*'));
        card.add(chkShowPassword);

        JButton btnSubmit = Theme.primaryButton("Sign In");
        btnSubmit.setBounds(50, 320, 140, 36);
        btnSubmit.addActionListener(e -> authenticate());
        card.add(btnSubmit);

        JButton btnBack = Theme.ghostButton("← Back");
        btnBack.setBounds(210, 320, 140, 36);
        btnBack.addActionListener(e -> showPublicPanel());
        card.add(btnBack);

        loginPanel.add(card);
    }

    private void showPublicPanel() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Public");
    }

    private void showLoginPanel() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Login");
    }

    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        }
    }

    private void loadProducts() {
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
        } catch (SQLException e) {
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
