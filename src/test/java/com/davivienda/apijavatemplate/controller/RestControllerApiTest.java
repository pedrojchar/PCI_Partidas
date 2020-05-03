//
// Copyright (C) 2019 - Banco Davivienda S.A. y sus filiales.
//
// Tester del Controlador Rest del Microservicio.
package com.davivienda.apijavatemplate.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puma.model.RequestModel;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RestControllerApiTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();
    }
    

    @Test
    public void successfulGet() throws Exception {

        RequestModel request = new RequestModel();
        request.setData("Mensaje de Prueba");

        mvc.perform(get("/"))
            .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void successfulPost() throws Exception {

        RequestModel request = new RequestModel();
        request.setData("Mensaje de Prueba");

        mvc.perform(post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().is2xxSuccessful());
    }

}
