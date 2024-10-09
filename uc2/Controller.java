package uc2;

import java.util.List;

public class Controller {
    private DBmgr DB;

    public Controller() {
        this.DB = new DBmgr();
    }

    public void update(List<DiscountPolicy> policies) {
        if (policies == null || policies.isEmpty()) {
            throw new IllegalArgumentException("Policies list cannot be null or empty");
        }

        for (DiscountPolicy policy : policies) {
            DB.store(policy);
        }
    }



    public List<DiscountPolicy> getAllPolicies() {
        return DB.getAllPolicies();
    }

    public DiscountPolicy getPolicyByName(String name) {
        return DB.getPolicyByName(name);
    }

    public void createPolicy(DiscountPolicy policy) {
        DB.store(policy);
    }

    public void deletePolicy(String name) {
        DB.deletePolicy(name);
    }

    public double applyDiscount(Order order) {
        DiscountPolicy applicablePolicy = DB.getFirstApplicablePolicy(order);
        if (applicablePolicy != null) {
            return applicablePolicy.calculateDiscount(order.getTotalCost());
        }
        return 0.0;
    }
}