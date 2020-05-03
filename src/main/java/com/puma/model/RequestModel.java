//
// Copyright (C) 2019 - Banco Davivienda S.A. y sus filiales.
//
// Modelo del request del Microservicio.
package com.puma.model;

import java.io.Serializable;

public class RequestModel implements Serializable
{
    
    private static final long serialVersionUID = 2L;
    private String data;

    public String getData(){
        return data;
    }

    public void setData(String dataIn){
        data = dataIn;
    }

    public String toString(){
        return "{ data : " + null==data?"":data+ "}";
    }
}