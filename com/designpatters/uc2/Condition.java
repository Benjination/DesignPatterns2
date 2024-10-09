package com.designpatters.uc2;


import com.designpatters.uc2.Condition;


public interface Condition {
    boolean evaluate(Order order);
}