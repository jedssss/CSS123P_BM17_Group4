package com.javaswing.practicedb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminMenu extends JFrame {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        Theme.applyGlobalDefaults();
        EventQueue.invokeLater(() -> {
            try {
                new AdminMenu().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AdminMenu() {
        initialize();
    }

    private void initialize() {
        setTitle("Admin Dashboard");
        setSize(480, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        // ---- Top Bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_PANEL);
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoRow.setOpaque(false);
        JLabel dot = new JLabel("● ");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dot.setForeground(Theme.ACCENT_LIGHT);
        JLabel title = Theme.titleLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoRow.add(dot);
        logoRow.add(title);
        topBar.add(logoRow, BorderLayout.WEST);

        JLabel userTag = new JLabel("  ADMIN  ");
        userTag.setFont(Theme.FONT_SMALL);
        userTag.setForeground(Theme.ACCENT_LIGHT);
        userTag.setBackground(new Color(99, 102, 241, 40));
        userTag.setOpaque(true);
        userTag.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        topBar.add(userTag, BorderLayout.EAST);

        getContentPane().add(topBar, BorderLayout.NORTH);

        // ---- Cards Grid ----
        JPanel grid = new JPanel(new GridLayout(3, 1, 0, 12));
        grid.setBackground(Theme.BG_DARK);
        grid.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        grid.add(makeNavCard("📦", "Manage Suppliers",
                "View, add, update and delete suppliers",
                () -> new SupplierManager().setVisible(true)));

        grid.add(makeNavCard("🏷️", "Manage Products",
                "View, add, update and delete products",
                () -> new ProductManager().setVisible(true)));

        grid.add(makeNavCard("📊", "Restock Products",
                "Update stock quantities for products",
                () -> new InventoryManagerFrame().setVisible(true)));

        getContentPane().add(grid, BorderLayout.CENTER);

        // ---- Footer ----
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 10));
        footer.setBackground(Theme.BG_PANEL);
        JButton btnLogout = Theme.dangerButton("Logout");
        btnLogout.setPreferredSize(new Dimension(100, 32));
        btnLogout.addActionListener(e -> {
            dispose();
            PublicView pv = new PublicView();
            pv.setVisible(true);
        });
        footer.add(btnLogout);
        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    private JPanel makeNavCard(String icon, String title, String subtitle, Runnable action) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new Theme.RoundedBorder(Theme.BORDER, 12));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        ico.setBounds(16, 14, 40, 40);
        card.add(ico);

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(Theme.FONT_BOLD);
        lTitle.setForeground(Theme.TEXT_PRIMARY);
        lTitle.setBounds(64, 12, 260, 22);
        card.add(lTitle);

        JLabel lSub = new JLabel(subtitle);
        lSub.setFont(Theme.FONT_SMALL);
        lSub.setForeground(Theme.TEXT_SECONDARY);
        lSub.setBounds(64, 34, 280, 18);
        card.add(lSub);

        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 22));
        arrow.setForeground(Theme.ACCENT_LIGHT);
        arrow.setBounds(370, 14, 30, 40);
        card.add(arrow);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(Theme.BG_HOVER); card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(Theme.BG_CARD);  card.repaint(); }
        });

        return card;
    }
}
