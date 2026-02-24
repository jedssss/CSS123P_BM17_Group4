package com.javaswing.practicedb;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product p) { products.add(p); }
    public void removeProduct(String productId) {
        products.removeIf(p -> p.getProductId().equals(productId));
    }
    public Product findProduct(String productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst().orElse(null);
    }
    public List<Product> listProducts() { return products; }
}