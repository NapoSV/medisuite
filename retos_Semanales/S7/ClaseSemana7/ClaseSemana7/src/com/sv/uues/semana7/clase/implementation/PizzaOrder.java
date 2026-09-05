package com.sv.uues.semana7.clase.implementation;

import com.sv.uues.semana7.clase.abstraction.Order;
import com.sv.uues.semana7.clase.shared.DeliveryCounter;

public class PizzaOrder extends Order {


    public PizzaOrder(int idOrder) {
        super(idOrder);
    }

    @Override
    public void prepare() {
        System.out.println("[Chef " + Thread.currentThread().getName() + "] Started to cook Pizza #" + idOrder);
        try { Thread.sleep(3000); } catch (InterruptedException e) {} // cook time simulated
        DeliveryCounter.deliver("Pizza #" + idOrder);
    }
}
