//
// Copyright (C) 2019 - Banco Davivienda S.A. y sus filiales.
//
// modelo del response del Microservicio.
package com.puma.model;

import java.io.Serializable;

public class ResponseModel implements Serializable
{
    
    
    private static final long serialVersionUID = 1L;
    private String data;
    private String resultado;
    private String codError;
    private String messageError;

    public String getData(){
        return data;
    }

    public void setData(String dataIn){
        data = dataIn;
    }

    public String getCodError() {
        return codError;
    }

    public void setCodError(String codError) {
        this.codError = codError;
    }

    public String getMessageError() {
        return messageError;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    
}