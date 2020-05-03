//
// Copyright (C) 2019 - Banco Davivienda S.A. y sus filiales.
//
// Controlador Rest del Microservicio.
package com.puma.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.puma.gestiondocumental.Gestion;
import com.puma.model.RequestModel;
import com.puma.model.ResponseModel;




@RestController
@RequestMapping("/")
public class RestControllerApi
{
    private Logger logger = LoggerFactory.getLogger(RestControllerApi.class);
     
    @PostMapping(path = "/cargar")
    public ResponseModel post(@RequestBody RequestModel saludo)
    {
    	ResponseModel response = new ResponseModel();
    	Gestion gestion = new Gestion();
    	try {
    		gestion.procesarData(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        return response;
    }

    @PostMapping(path = "/file2partidas")
    public ResponseModel file2partidas(@RequestBody RequestModel saludo)
    {
    	ResponseModel response = new ResponseModel();
    	Gestion gestion = new Gestion();
    	try {
    		gestion.procesarFile2Partidas(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        return response;
    }
    
    @PostMapping(path = "/crear-partidas")
    public ResponseModel crearPartidas(@RequestBody RequestModel saludo)
    {
    	ResponseModel response = new ResponseModel();
    	Gestion gestion = new Gestion();
    	try {
    		gestion.crearPartidas(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        return response;
    }
}