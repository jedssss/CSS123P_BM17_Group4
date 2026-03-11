package com.javaswing.practicedb;

public class Product {
    private String productId;
    private String name;
    private double price;
    private int stockQuantity;
    private Supplier supplier;

    public Product() {}

    public Product(String productId, String name, double price, int stockQuantity, Supplier supplier) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.supplier = supplier;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) throws InvalidPriceException {
        if (price <= 0) throw new InvalidPriceException("Price must be positive.");
        this.price = price;
    }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) throws InvalidQuantityException {
        if (stockQuantity < 0) throw new InvalidQuantityException("Stock cannot be negative.");
        this.stockQuantity = stockQuantity;
    }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}