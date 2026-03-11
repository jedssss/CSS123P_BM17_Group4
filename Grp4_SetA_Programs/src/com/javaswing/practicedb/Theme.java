package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class Theme {
    // Color Palette - Dark Mode with Indigo Accent
    public static final Color BG_DARK       = new Color(18, 18, 30);       // deep navy-black
    public static final Color BG_PANEL      = new Color(28, 28, 45);       // panel bg
    public static final Color BG_CARD       = new Color(36, 36, 58);       // card / input bg
    public static final Color BG_HOVER      = new Color(48, 48, 75);       // hover state
    public static final Color ACCENT        = new Color(99, 102, 241);     // indigo-500
    public static final Color ACCENT_HOVER  = new Color(79, 70, 229);      // indigo-600
    public static final Color ACCENT_LIGHT  = new Color(129, 140, 248);    // indigo-400
    public static final Color DANGER        = new Color(239, 68, 68);      // red-500
    public static final Color DANGER_HOVER  = new Color(220, 38, 38);      // red-600
    public static final Color SUCCESS       = new Color(34, 197, 94);      // green-500
    public static final Color TEXT_PRIMARY  = new Color(241, 245, 249);    // slate-100
    public static final Color TEXT_SECONDARY= new Color(148, 163, 184);    // slate-400
    public static final Color BORDER        = new Color(55, 55, 85);       // subtle border
    public static final Color TABLE_HEADER  = new Color(45, 45, 70);
    public static final Color TABLE_ALT     = new Color(32, 32, 52);
    public static final Color TABLE_SEL     = new Color(99, 102, 241, 80); // accent translucent

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 12);

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("OptionPane.background", BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("OptionPane.messageFont", FONT_LABEL);
        UIManager.put("Button.font", FONT_BUTTON);
        UIManager.put("Label.font", FONT_LABEL);
        UIManager.put("TextField.font", FONT_LABEL);
        UIManager.put("PasswordField.font", FONT_LABEL);
        UIManager.put("ComboBox.font", FONT_LABEL);
        UIManager.put("Table.font", FONT_LABEL);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("ScrollPane.background", BG_PANEL);
    }

    /** Styled primary (accent) button */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? ACCENT_HOVER : ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, TEXT_PRIMARY);
        return btn;
    }

    /** Styled danger (delete/logout) button */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? DANGER_HOVER : DANGER;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, Color.WHITE);
        return btn;
    }

    /** Styled ghost/secondary button */
    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? BG_HOVER : BG_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, TEXT_PRIMARY);
        return btn;
    }

    private static void styleButton(JButton btn, Color fg) {
        btn.setForeground(fg);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Styled text field */
    public static JTextField textField() {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleField(f);
        return f;
    }

    /** Styled password field */
    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleField(f);
        return f;
    }

    private static void styleField(JTextField f) {
        f.setFont(FONT_LABEL);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(BG_CARD);
        f.setCaretColor(ACCENT_LIGHT);
        f.setOpaque(false);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER, 8),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    /** Styled combo box */
    public static JComboBox<String> comboBox() {
        JComboBox<String> c = new JComboBox<>();
        c.setFont(FONT_LABEL);
        c.setForeground(TEXT_PRIMARY);
        c.setBackground(BG_CARD);
        c.setBorder(new RoundedBorder(BORDER, 8));
        c.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT : BG_CARD);
                setForeground(TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        return c;
    }

    /** Styled table */
    public static void styleTable(JTable table) {
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_LABEL);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
    }

    /** Styled scroll pane */
    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_PANEL);
        sp.getViewport().setBackground(BG_PANEL);
        sp.setBorder(new RoundedBorder(BORDER, 10));
        return sp;
    }

    /** Section label */
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    /** Title label */
    public static JLabel titleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    /** Accent title label */
    public static JLabel accentTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(ACCENT_LIGHT);
        return l;
    }

    /** Rounded border utility */
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        public RoundedBorder(Color color, int radius) { this.color = color; this.radius = radius; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,8,4,8); }
    }

    /** Alternating row renderer */
    public static class AlternatingRowRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            if (isSelected) {
                setBackground(ACCENT);
                setForeground(Color.WHITE);
            } else {
                setBackground(row % 2 == 0 ? BG_PANEL : TABLE_ALT);
                setForeground(TEXT_PRIMARY);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return this;
        }
    }
}
