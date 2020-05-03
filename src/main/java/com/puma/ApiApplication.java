//
// Copyright (C) 2019 - Banco Davivienda S.A. y sus filiales.
//
// Clase main del Microservicio.
package com.puma;

import com.puma.filter.loggingfilter.CustomizedRequestLoggingFilter;
import com.puma.service.util.PropertiesValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ApiApplication {

	@Autowired
	PropertiesValidator validator;

	@Bean
	public CustomizedRequestLoggingFilter requestLoggingFilter() {
		CustomizedRequestLoggingFilter loggingFilter = new CustomizedRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(true);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		loggingFilter.setMaxPayloadLength(10000);
		loggingFilter.setIncludeHeaders(true);

		return loggingFilter;
	}

	public static void main(String[] args) {

		SpringApplication.run(ApiApplication.class, args);
	}

}
