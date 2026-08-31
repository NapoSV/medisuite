package com.sv.uues.semana7.clase.implementation;

import com.sv.uues.semana7.clase.abstraction.Order;
import com.sv.uues.semana7.clase.shared.DeliveryCounter;

public class BurgerOrder extends Order {


    public BurgerOrder(int idOrder) {
        super(idOrder);
    }

    @Override
    public void prepare() {
        System.out.println("[Chef " + Thread.currentThread().getName() + "] Started to roast Burger #" + idOrder);
        try { Thread.sleep(3000); } catch (InterruptedException e) {} // cook time simulated
        DeliveryCounter.deliver("Burger #" + idOrder);
    }
}
