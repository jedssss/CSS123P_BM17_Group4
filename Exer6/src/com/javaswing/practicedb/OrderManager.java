package com.javaswing.practicedb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderManager extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> cmbCustomer;
    private JComboBox<String> cmbProduct;
    private JTextField txtQuantity, txtTotal;
    private JTable tableCart;
    private DefaultTableModel cartModel;
    private Map<String, Product> productMap = new HashMap<>();
    private Map<Product, Integer> cart = new HashMap<>();
    private String currentOrderId;
    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static String query;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                OrderManager window = new OrderManager();
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

    public OrderManager() {
        initialize();
        dbConnect();
        loadCustomers();
        loadProducts();
        currentOrderId = generateOrderId();
    }

    private void initialize() {
        setTitle("Place Order");
        setBounds(100, 100, 700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblCustomer = new JLabel("Customer:");
        lblCustomer.setBounds(10, 10, 80, 25);
        getContentPane().add(lblCustomer);
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setBounds(100, 10, 200, 25);
        getContentPane().add(cmbCustomer);

        JLabel lblProduct = new JLabel("Product:");
        lblProduct.setBounds(10, 40, 80, 25);
        getContentPane().add(lblProduct);
        cmbProduct = new JComboBox<>();
        cmbProduct.setBounds(100, 40, 200, 25);
        getContentPane().add(cmbProduct);

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setBounds(10, 70, 80, 25);
        getContentPane().add(lblQty);
        txtQuantity = new JTextField();
        txtQuantity.setBounds(100, 70, 100, 25);
        getContentPane().add(txtQuantity);

        JButton btnAdd = new JButton("Add to Cart");
        btnAdd.setBounds(210, 70, 120, 25);
        btnAdd.addActionListener(e -> addToCart());
        getContentPane().add(btnAdd);

        cartModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Price", "Quantity", "Subtotal"}, 0);
        tableCart = new JTable(cartModel);
        JScrollPane scrollCart = new JScrollPane(tableCart);
        scrollCart.setBounds(10, 110, 450, 200);
        getContentPane().add(scrollCart);

        JLabel lblTotal = new JLabel("Total:");
        lblTotal.setBounds(10, 320, 50, 25);
        getContentPane().add(lblTotal);
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setBounds(70, 320, 100, 25);
        getContentPane().add(txtTotal);

        JButton btnPay = new JButton("Process Payment");
        btnPay.setBounds(200, 320, 150, 25);
        btnPay.addActionListener(e -> processPayment());
        getContentPane().add(btnPay);
    }

    private void loadCustomers() {
        try {
            query = "SELECT customerId, customerName FROM customer";
            rs = stmt.executeQuery(query);
            cmbCustomer.removeAllItems();
            while (rs.next()) {
                cmbCustomer.addItem(rs.getString("customerId") + " - " + rs.getString("customerName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        try {
            query = "SELECT * FROM product";
            rs = stmt.executeQuery(query);
            cmbProduct.removeAllItems();
            productMap.clear();
            while (rs.next()) {
                String id = rs.getString("productId");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stockQuantity");
                Product p = new Product();
                p.setProductId(id);
                p.setName(name);
                try {
                    p.setPrice(price);
                    p.setStockQuantity(stock);
                } catch (InvalidPriceException | InvalidQuantityException e) {
                    // This should not happen if database data is valid.
                    // Log the error and skip this product.
                    System.err.println("Invalid data for product " + id + ": " + e.getMessage());
                    continue; // skip adding this product to the dropdown
                }
                productMap.put(id, p);
                cmbProduct.addItem(id + " - " + name + " ($" + price + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void addToCart() {
        String selectedProduct = (String) cmbProduct.getSelectedItem();
        if (selectedProduct == null) return;
        String productId = selectedProduct.split(" - ")[0];
        Product p = productMap.get(productId);
        if (p == null) return;

        int qty;
        try {
            qty = Integer.parseInt(txtQuantity.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
            return;
        }
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be positive.");
            return;
        }
        if (qty > p.getStockQuantity()) {
            JOptionPane.showMessageDialog(this, "Not enough stock. Available: " + p.getStockQuantity());
            return;
        }

        cart.put(p, cart.getOrDefault(p, 0) + qty);
        refreshCartTable();
        txtQuantity.setText("");
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        double total = 0;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            double subtotal = p.getPrice() * qty;
            total += subtotal;
            cartModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getPrice(),
                    qty,
                    subtotal
            });
        }
        txtTotal.setText(String.format("%.2f", total));
    }

    private String generateOrderId() {
        return "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void processPayment() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }
        String customerItem = (String) cmbCustomer.getSelectedItem();
        if (customerItem == null) {
            JOptionPane.showMessageDialog(this, "Select a customer.");
            return;
        }
        String customerId = customerItem.split(" - ")[0];
        double total = Double.parseDouble(txtTotal.getText());

        String[] options = {"Cash", "E-Wallet"};
        int choice = JOptionPane.showOptionDialog(this, "Select payment method", "Payment",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        String paymentId = "PAY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate today = LocalDate.now();

        try {
            // Insert order
            query = "INSERT INTO orders (orderId, customerId, orderDate, totalAmount) VALUES ('"
                    + currentOrderId + "','" + customerId + "','" + today + "'," + total + ")";
            stmt.executeUpdate(query);

            // Insert order_product items
            for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                Product p = entry.getKey();
                int qty = entry.getValue();
                query = "INSERT INTO order_product (orderId, productId, quantity) VALUES ('"
                        + currentOrderId + "','" + p.getProductId() + "'," + qty + ")";
                stmt.executeUpdate(query);

                // Update stock
                query = "UPDATE product SET stockQuantity = stockQuantity - " + qty + " WHERE productId='" + p.getProductId() + "'";
                stmt.executeUpdate(query);
            }

            // Insert payment
            if (choice == 0) { // Cash
                String cashStr = JOptionPane.showInputDialog(this, "Enter cash received:");
                double cash = Double.parseDouble(cashStr);
                query = "INSERT INTO payment (paymentId, orderId, amount, paymentDate, paymentType, cashReceived) VALUES ('"
                        + paymentId + "','" + currentOrderId + "'," + total + ",'" + today + "','CASH'," + cash + ")";
            } else { // E-Wallet
                String walletId = JOptionPane.showInputDialog(this, "Enter wallet ID:");
                String provider = JOptionPane.showInputDialog(this, "Enter provider:");
                query = "INSERT INTO payment (paymentId, orderId, amount, paymentDate, paymentType, walletId, provider) VALUES ('"
                        + paymentId + "','" + currentOrderId + "'," + total + ",'" + today + "','EWALLET','" + walletId + "','" + provider + "')";
            }
            stmt.executeUpdate(query);

            JOptionPane.showMessageDialog(this, "Order placed successfully!\nOrder ID: " + currentOrderId);
            cart.clear();
            refreshCartTable();
            currentOrderId = generateOrderId();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing order: " + e.getMessage());
        }
    }
}