package com.designpatters.uc2;


public class DiscountPolicy {
    private String name;
    private String discountType;
    private double discountValue;
    private double minPurchase;

    public DiscountPolicy(String name, String discountType, double discountValue, double minPurchase) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minPurchase = minPurchase;
    }


    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }

    public double getMinPurchase() { return minPurchase; }
    public void setMinPurchase(double minPurchase) { this.minPurchase = minPurchase; }

    public double calculateDiscount(double totalAmount) {
        if (totalAmount < minPurchase) {
            return 0;
        }
        
        if (discountType.equals("Fixed Amount")) {
            return Math.min(discountValue, totalAmount);
        } else if (discountType.equals("Percentage")) {
            return totalAmount * (discountValue / 100);
        }
        
        return 0; // For other types or if no discount applies
    }

    @Override
    public String toString() {
        return String.format("%s - %s: %.2f (Min Purchase: $%.2f)", name, discountType, discountValue, minPurchase);
    }
}
