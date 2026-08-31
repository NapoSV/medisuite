package com.sv.uues.semana7.clase.interfaces;

import java.util.ArrayList;

public interface IDao {
    public void insert(IParameter parameter);
    public void update(IParameter parameter);
    public void delete(IParameter parameter);
    public ArrayList<IParameter> read();

}