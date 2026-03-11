package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        Theme.applyGlobalDefaults();
        EventQueue.invokeLater(() -> {
            try { new SupplierManager().setVisible(true); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public static void dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public SupplierManager() {
        initialize();
        dbConnect();
        loadTable();
    }

    private void initialize() {
        setTitle("Supplier Management");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_DARK);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // ---- Top Bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_PANEL);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel title = Theme.titleLabel("Supplier Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topBar.add(title, BorderLayout.WEST);
        getContentPane().add(topBar, BorderLayout.NORTH);

        // ---- Main Content ----
        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left form panel
        JPanel formCard = new JPanel(null);
        formCard.setBackground(Theme.BG_PANEL);
        formCard.setBorder(new Theme.RoundedBorder(Theme.BORDER, 12));
        formCard.setPreferredSize(new Dimension(260, 0));

        JLabel formTitle = Theme.label("SUPPLIER DETAILS");
        formTitle.setBounds(20, 20, 220, 20);
        formCard.add(formTitle);

        int y = 55;
        formCard.add(fieldLabel("Supplier ID", 20, y)); y += 20;
        txtId = Theme.textField();
        txtId.setBounds(20, y, 220, 36); formCard.add(txtId); y += 52;

        formCard.add(fieldLabel("Name", 20, y)); y += 20;
        txtName = Theme.textField();
        txtName.setBounds(20, y, 220, 36); formCard.add(txtName); y += 52;

        formCard.add(fieldLabel("Contact Info", 20, y)); y += 20;
        txtContact = Theme.textField();
        txtContact.setBounds(20, y, 220, 36); formCard.add(txtContact); y += 52;

        JButton btnAdd = Theme.primaryButton("Add Supplier");
        btnAdd.setBounds(20, y, 220, 36);
        btnAdd.addActionListener(e -> addSupplier());
        formCard.add(btnAdd); y += 48;

        JButton btnUpdate = Theme.ghostButton("Update Selected");
        btnUpdate.setBounds(20, y, 220, 36);
        btnUpdate.addActionListener(e -> updateSupplier());
        formCard.add(btnUpdate); y += 48;

        JButton btnDelete = Theme.dangerButton("Delete Selected");
        btnDelete.setBounds(20, y, 220, 36);
        btnDelete.addActionListener(e -> deleteSupplier());
        formCard.add(btnDelete);

        content.add(formCard, BorderLayout.WEST);

        // Right table panel
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(Theme.BG_PANEL);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            new Theme.RoundedBorder(Theme.BORDER, 12),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel tblLabel = Theme.label("ALL SUPPLIERS");
        tableTop.add(tblLabel, BorderLayout.WEST);
        JButton btnRefresh = Theme.ghostButton("⟳  Refresh");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.addActionListener(e -> loadTable());
        tableTop.add(btnRefresh, BorderLayout.EAST);
        tableCard.add(tableTop, BorderLayout.NORTH);

        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Supplier ID", "Name", "Contact Info"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(tableModel);
        Theme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtContact.setText(tableModel.getValueAt(row, 2).toString());
            }
        });
        tableCard.add(Theme.scrollPane(table), BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);
        getContentPane().add(content, BorderLayout.CENTER);
    }

    private JLabel fieldLabel(String text, int x, int y) {
        JLabel l = Theme.label(text);
        l.setBounds(x, y, 220, 18);
        return l;
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
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void addSupplier() {
        String id = txtId.getText().trim(), name = txtName.getText().trim(), contact = txtContact.getText().trim();
        if (id.isEmpty() || name.isEmpty()) { JOptionPane.showMessageDialog(this, "ID and Name are required."); return; }
        try {
            query = "INSERT INTO supplier (supplierId, name, contactInfo) VALUES ('" + id + "','" + name + "','" + contact + "')";
            stmt.executeUpdate(query);
            loadTable(); clearFields();
            JOptionPane.showMessageDialog(this, "Supplier added successfully.");
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void updateSupplier() {
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Select a supplier to update."); return; }
        try {
            query = "UPDATE supplier SET name='" + txtName.getText() + "', contactInfo='" + txtContact.getText()
                + "' WHERE supplierId='" + txtId.getText() + "'";
            stmt.executeUpdate(query);
            loadTable();
            JOptionPane.showMessageDialog(this, "Supplier updated.");
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteSupplier() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = tableModel.getValueAt(row, 0).toString();
        try {
            rs = stmt.executeQuery("SELECT COUNT(*) FROM product WHERE supplierId = '" + id + "'");
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Cannot delete: products are linked to this supplier.", "Supplier In Use", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException ex) { ex.printStackTrace(); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete supplier \"" + id + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                stmt.executeUpdate("DELETE FROM supplier WHERE supplierId='" + id + "'");
                loadTable(); clearFields();
            } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    private void clearFields() { txtId.setText(""); txtName.setText(""); txtContact.setText(""); }
}
