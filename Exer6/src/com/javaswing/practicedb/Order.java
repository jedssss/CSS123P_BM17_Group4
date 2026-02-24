package com.javaswing.practicedb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private String orderId;
    private Customer customer;
    private Map<Product, Integer> products = new HashMap<>();
    private Date orderDate;
    private double totalCost;

    public Order() {}
    public Order(String orderId, Customer customer, Date orderDate) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderDate = orderDate;
    }

    public void addProduct(Product p, int qty) throws InvalidQuantityException {
        if (qty <= 0) throw new InvalidQuantityException("Quantity must be positive.");
        products.put(p, products.getOrDefault(p, 0) + qty);
        calculateTotal();
    }
    public void removeProduct(Product p) {
        products.remove(p);
        calculateTotal();
    }
    private void calculateTotal() {
        totalCost = products.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }
    public double getTotalCost() { return totalCost; }
    public String getReceiptDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Customer: ").append(customer.getCustomerName()).append("\n");
        sb.append("Date: ").append(orderDate).append("\n");
        sb.append("Products:\n");
        products.forEach((p, qty) -> sb.append(p.getName()).append(" x").append(qty)
                .append(" @ ").append(p.getPrice()).append(" = ").append(p.getPrice()*qty).append("\n"));
        sb.append("Total: ").append(totalCost);
        return sb.toString();
    }
    public void generateReceipt() {
        System.out.println(getReceiptDetails());
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Map<Product, Integer> getProducts() { return products; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
}