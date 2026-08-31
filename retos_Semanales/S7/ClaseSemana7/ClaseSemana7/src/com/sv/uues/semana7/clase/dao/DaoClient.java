package com.sv.uues.semana7.clase.dao;


import com.sv.uues.semana7.clase.interfaces.IDao;
import com.sv.uues.semana7.clase.interfaces.IParameter;

import java.util.ArrayList;

public class DaoClient implements IDao, IParameter {

    ArrayList<IParameter> data = null;
    DaoFile file = new DaoFile("Clients.dat");
    @Override
    public void insert(IParameter parameter) {
        data=file.readFile();
        data.add(parameter);
        file.saveFile(data);
    }

    @Override
    public void update(IParameter parameter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(IParameter parameter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayList<IParameter> read() {
        return file.readFile();
    }
}
