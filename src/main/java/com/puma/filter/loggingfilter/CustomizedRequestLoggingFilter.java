//
// Copyright (C) 2019 - Banco Davivienda S.A. y sus filiales.
//
// Clase para customizar el comportamiento del RequestLoggingFilter
package com.puma.filter.loggingfilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

public class CustomizedRequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Override
    protected void beforeRequest(HttpServletRequest httpServletRequest, String message) {
    }

    @Override
    protected void afterRequest(HttpServletRequest httpServletRequest, String message) {
        this.logger.debug(message);
    }

    @Override
	protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
		StringBuilder msg = new StringBuilder();

        msg.append("REQUEST method=[").append(request.getMethod()).append("] ");
        msg.append("path=[").append(request.getRequestURI());

		if (isIncludeQueryString()) {
			String queryString = request.getQueryString();
			if (queryString != null) {
				msg.append('?').append(queryString);
			}
        }
        msg.append("] ");

		if (isIncludeClientInfo()) {
			String client = request.getRemoteAddr();
			if (StringUtils.hasLength(client)) {
				msg.append("ipclient=[").append(client).append("] ");
			}
			HttpSession session = request.getSession(false);
			if (session != null) {
				msg.append("session=[").append(session.getId()).append("] ");
			}
			String user = request.getRemoteUser();
			if (user != null) {
				msg.append("user=[").append(user).append("] ");
			}
		}

		if (isIncludeHeaders()) {
			msg.append(" headers=[").append(new ServletServerHttpRequest(request).getHeaders()).append("] ");
		}

		if (isIncludePayload()) {
			String payload = getMessagePayload(request);
			if (payload != null) {
				msg.append(" body=[").append(payload).append("] ");
			}
		}

		return msg.toString();
	}    
}