package com.designpatters.uc2;


import com.designpatters.uc2.DPolicy;

public class DPolicy {
    private String name;
    private String discountType;
    private double discountValue;
    private double minPurchase;

    public DPolicy(String name, String discountType, double discountValue, double minPurchase) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minPurchase = minPurchase;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public String getDiscountType() {
        return discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public double getMinPurchase() {
        return minPurchase;
    }

    @Override
    public String toString() {
        return name + " - " + discountType + ": " + discountValue + " (Min Purchase: " + minPurchase + ")";
    }
}
