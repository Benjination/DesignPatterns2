package com.designpatters.uc2;



class MinPurchaseCondition implements Condition {
    private double minPurchase;

    public MinPurchaseCondition(double minPurchase) {
        this.minPurchase = minPurchase;
    }

    @Override
    public boolean evaluate(Order order) {
        return order.getTotalCost() >= minPurchase;
    }
}

class PercentageDiscountCondition implements Condition {
    private double percentage;

    public PercentageDiscountCondition(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public boolean evaluate(Order order) {
        // Always true for percentage discounts
        return true;
    }

    public double getDiscountAmount(Order order) {
        return order.getTotalCost() * (percentage / 100);
    }
}

public class AndCondition implements Condition {
    private Condition left;
    private Condition right;

    public AndCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Order order) {
        return left.evaluate(order) && right.evaluate(order);
    }
}