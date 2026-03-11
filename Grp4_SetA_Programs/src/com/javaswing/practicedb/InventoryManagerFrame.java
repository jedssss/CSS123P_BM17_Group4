package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        Theme.applyGlobalDefaults();
        EventQueue.invokeLater(() -> {
            try { new InventoryManagerFrame().setVisible(true); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public InventoryManagerFrame() {
        initialize();
        dbConnect();
        loadTable();
    }

    private void initialize() {
        setTitle("Inventory - Restock");
        setSize(820, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        // ---- Top Bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_PANEL);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel title = Theme.titleLabel("Restock Products");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topBar.add(title, BorderLayout.WEST);
        getContentPane().add(topBar, BorderLayout.NORTH);

        // ---- Content ----
        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left restock form
        JPanel formCard = new JPanel(null);
        formCard.setBackground(Theme.BG_PANEL);
        formCard.setBorder(new Theme.RoundedBorder(Theme.BORDER, 12));
        formCard.setPreferredSize(new Dimension(260, 0));

        JLabel formTitle = Theme.label("RESTOCK FORM");
        formTitle.setBounds(20, 20, 220, 20);
        formCard.add(formTitle);

        // Info banner
        JPanel infoBanner = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99, 102, 241, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.ACCENT);
                g2.fillRoundRect(0, 0, 3, getHeight(), 3, 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        infoBanner.setOpaque(false);
        infoBanner.setBounds(20, 50, 220, 44);
        JLabel infoText = new JLabel("<html><b style='color:#818cf8'>Tip:</b> Enter a Product ID and the quantity to add to its current stock.</html>");
        infoText.setFont(Theme.FONT_SMALL);
        infoText.setForeground(Theme.TEXT_SECONDARY);
        infoText.setBounds(10, 4, 200, 36);
        infoBanner.add(infoText);
        formCard.add(infoBanner);

        int y = 110;
        JLabel l1 = Theme.label("Product ID");
        l1.setBounds(20, y, 220, 18); formCard.add(l1); y += 22;
        txtProductId = Theme.textField();
        txtProductId.setBounds(20, y, 220, 36); formCard.add(txtProductId); y += 52;

        JLabel l2 = Theme.label("Quantity to Add");
        l2.setBounds(20, y, 220, 18); formCard.add(l2); y += 22;
        txtQuantity = Theme.textField();
        txtQuantity.setBounds(20, y, 220, 36); formCard.add(txtQuantity); y += 52;

        JButton btnRestock = Theme.primaryButton("Restock");
        btnRestock.setBounds(20, y, 220, 38);
        btnRestock.addActionListener(e -> restock());
        formCard.add(btnRestock);

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
        JLabel tblLabel = Theme.label("CURRENT STOCK LEVELS");
        tableTop.add(tblLabel, BorderLayout.WEST);
        JButton btnRefresh = Theme.ghostButton("⟳  Refresh");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.addActionListener(e -> loadTable());
        tableTop.add(btnRefresh, BorderLayout.EAST);
        tableCard.add(tableTop, BorderLayout.NORTH);

        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Price", "Stock", "Supplier"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(tableModel);
        Theme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) txtProductId.setText(tableModel.getValueAt(row, 0).toString());
        });
        tableCard.add(Theme.scrollPane(table), BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);
        getContentPane().add(content, BorderLayout.CENTER);
    }

    private void loadTable() {
        try {
            query = "SELECT p.*, s.name AS supplierName FROM product p LEFT JOIN supplier s ON p.supplierId = s.supplierId";
            rs = stmt.executeQuery(query);
            tableModel.setRowCount(0);
            while (rs.next()) {
                int stock = rs.getInt("stockQuantity");
                tableModel.addRow(new Object[]{
                    rs.getString("productId"),
                    rs.getString("name"),
                    String.format("₱%.2f", rs.getDouble("price")),
                    stock,
                    rs.getString("supplierName") != null ? rs.getString("supplierName") : "—"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void restock() {
        String productId = txtProductId.getText().trim();
        String qtyStr = txtQuantity.getText().trim();
        if (productId.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter product ID and quantity."); return;
        }
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be positive."); return; }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number."); return;
        }
        try {
            query = "UPDATE product SET stockQuantity = stockQuantity + " + qty + " WHERE productId='" + productId + "'";
            int affected = stmt.executeUpdate(query);
            if (affected > 0) {
                loadTable();
                txtProductId.setText("");
                txtQuantity.setText("");
                JOptionPane.showMessageDialog(this, "Stock updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Product ID not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
