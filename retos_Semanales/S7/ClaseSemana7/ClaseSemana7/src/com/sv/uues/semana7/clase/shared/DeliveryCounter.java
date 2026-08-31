package com.sv.uues.semana7.clase.shared;

public class DeliveryCounter {

    private static int totalDeliveries=0;

    public static synchronized void deliver(String dishName){
        totalDeliveries++;
        System.out.println("Done..."+dishName+", totalDeliveries:"+totalDeliveries);
    };
}
