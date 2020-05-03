//
// Copyright (C) 2019 - Banco Davivienda S.A. y sus filiales.
//
// Validador de propiedades del Microservicio.
package com.puma.service.util;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("*")
@Validated
public class PropertiesValidator {

    @Value("${spring.mvc.servlet.path}")
    @NotBlank
    private String springMvcServletPath;

    @Value("${spring.profiles.active}")
    @NotBlank
    private String springProfileActive;

}
