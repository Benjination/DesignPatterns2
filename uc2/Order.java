package uc2;
// Order.java



import uc2.Order;

public class Order {
    private int numberOfItems;
    private double totalCost;
    private boolean isNewCustomer;

    public Order(int numberOfItems, double totalCost, boolean isNewCustomer) {
        this.numberOfItems = numberOfItems;
        this.totalCost = totalCost;
        this.isNewCustomer = isNewCustomer;
    }

    public int getNumberOfItems() { return numberOfItems; }
    public double getTotalCost() { return totalCost; }
    public boolean isNewCustomer() { return isNewCustomer; }
}