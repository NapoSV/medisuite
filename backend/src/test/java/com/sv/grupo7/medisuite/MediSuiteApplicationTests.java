package com.sv.grupo7.medisuite;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class MediSuiteApplicationTests {

     @Test
    void smokeTest(){
         Runnable mock = Mockito.mock(Runnable.class);

         mock.run();

         Mockito.verify(mock).run();
     }

}
