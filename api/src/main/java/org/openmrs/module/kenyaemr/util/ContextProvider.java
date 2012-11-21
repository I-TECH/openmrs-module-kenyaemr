/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.util;

import javax.servlet.ServletContext;

import org.openmrs.api.context.Context;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

/**
 * Utility class to give non bean classes access to the Spring application context
 * and the servlet context
 */
public class ContextProvider implements ApplicationContextAware, ServletContextAware {

	protected static ApplicationContext appContext = null;
	protected static ServletContext srvContext = null;
	
	/**
	 * Sets the Spring application context
	 * @param ctx the application context
	 */
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		appContext = ctx;
	}

	/**
	 * Gets the Spring application context
	 * @return the application context
	 */
	public static ApplicationContext getApplicationContext() {
		return appContext;
	}

	/**
	 * Sets the servlet context
	 * @param ctx the servlet context
	 */
	public void setServletContext(ServletContext ctx) {
		srvContext = ctx;
	}
	
	/**
	 * Gets the servlet context
	 * @return the servlet context
	 */
	public static ServletContext getServletContext() {
		return srvContext;
	}
}