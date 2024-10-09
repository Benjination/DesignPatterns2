package com.designpatters.uc2;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DBmgr {
    private Map<String, DiscountPolicy> policies = new HashMap<>();

    public void store(DiscountPolicy policy) {
        policies.put(policy.getName(), policy);
    }

    public List<DiscountPolicy> getAllPolicies() {
        return new ArrayList<>(policies.values());
    }

    public DiscountPolicy getPolicyByName(String name) {
        return policies.get(name);
    }

    public void deletePolicy(String name) {
        policies.remove(name);
    }

    public DiscountPolicy getFirstApplicablePolicy(Order order) {
        for (DiscountPolicy policy : policies.values()) {
            if (order.getTotalCost() >= policy.getMinPurchase()) {
                return policy;
            }
        }
        return null;
    }
}