package com.javaswing.practicedb;

public class InventoryManager {
    private Inventory inventory;

    public InventoryManager(Inventory inventory) {
        this.inventory = inventory;
    }

    public void restockProduct(String productId, int qty) throws InvalidQuantityException {
        if (qty <= 0) throw new InvalidQuantityException("Restock quantity must be positive.");
        Product p = inventory.findProduct(productId);
        if (p != null) {
            p.setStockQuantity(p.getStockQuantity() + qty);
        } else {
            throw new IllegalArgumentException("Product not found.");
        }
    }

    public void processOrder(Order o) throws InsufficientStockException, PaymentFailedException {
        // Check stock for each product
        for (Product p : o.getProducts().keySet()) {
            int requestedQty = o.getProducts().get(p);
            Product stockProduct = inventory.findProduct(p.getProductId());
            if (stockProduct == null || stockProduct.getStockQuantity() < requestedQty) {
                throw new InsufficientStockException("Insufficient stock for product: " + p.getName());
            }
        }

        // Simulate payment (in real system, payment would be processed elsewhere)
        // Here we assume payment is already done; if not, we could throw PaymentFailedException
        // For demo, we'll just proceed.

        // Update inventory
        for (Product p : o.getProducts().keySet()) {
            int requestedQty = o.getProducts().get(p);
            Product stockProduct = inventory.findProduct(p.getProductId());
            try {
                stockProduct.setStockQuantity(stockProduct.getStockQuantity() - requestedQty);
            } catch (InvalidQuantityException e) {
                // Should not happen because we checked stock, but wrap as runtime for safety
                throw new RuntimeException(e);
            }
        }
    }
}