package com.sv.uues.semana7.clase.controller;

import com.sv.uues.semana7.clase.abstraction.Order;
import com.sv.uues.semana7.clase.implementation.BurgerOrder;
import com.sv.uues.semana7.clase.implementation.PizzaOrder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentController {

    static void main(String[] args) {


        Order[] orders = {
                new PizzaOrder(1),
                new BurgerOrder(2),
                new BurgerOrder(3),
                new PizzaOrder(4),
                new PizzaOrder(5)
        };
       // PizzaOrder[] pizzaOrders = {};
       // BurgerOrder[] burgerOrders = {};

        ExecutorService chefs = Executors.newFixedThreadPool(5);

        for (Order order : orders) {
            chefs.submit(order);
        }

        chefs.shutdown();

    }
}
