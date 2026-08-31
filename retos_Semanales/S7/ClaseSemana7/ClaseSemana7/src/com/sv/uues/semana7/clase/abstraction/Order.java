package com.sv.uues.semana7.clase.abstraction;

public abstract class Order implements Runnable {

    protected int idOrder;

    public Order(int idOrder){
        this.idOrder=idOrder;
    }

    public abstract void prepare();

    @Override
    public void run() {
        System.out.println("Executing....");
        prepare();
    }
}
