package com.sv.uues.semana7.clase.abstraction;

import com.sv.uues.semana7.clase.interfaces.IParameter;

import java.util.ArrayList;

public abstract class FileOperations {

    public abstract void saveFile(ArrayList<IParameter> almacen) ;
    public abstract ArrayList<IParameter> readFile();
    }
