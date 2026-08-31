package com.sv.uues.semana7.clase.dao;


import com.sv.uues.semana7.clase.abstraction.FileOperations;
import com.sv.uues.semana7.clase.interfaces.IParameter;

import java.io.*;
import java.util.ArrayList;

public class DaoFile extends FileOperations {

    FileOutputStream fos;
    ObjectOutputStream oos;
    FileInputStream fis;
    ObjectInputStream ois;
    private String fileName;

    DaoFile(String fileName){
        this.fileName = fileName;
    }

    @Override
    public void saveFile(ArrayList<IParameter> almacen) {
        try {
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(almacen);
            oos.flush();
            oos.close();
        }  catch (IOException e) {
            System.out.println(e.getMessage());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ArrayList<IParameter> readFile() {
        {
            ArrayList<IParameter> almacen=null;//arraylist es un vector de objetos
            try {
                fis = new FileInputStream(fileName);
                ois = new ObjectInputStream(fis);
                almacen = (ArrayList<IParameter>) ois.readObject();
            } catch (IOException e) {
                almacen = new ArrayList<IParameter>();
                return almacen;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return almacen;
        }
    }
}
