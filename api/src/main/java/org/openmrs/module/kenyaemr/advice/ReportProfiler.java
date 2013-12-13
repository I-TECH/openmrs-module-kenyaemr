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

package org.openmrs.module.kenyaemr.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Advice class to wrap around reporting component evaluation methods and log timings
 */
public class ReportProfiler implements MethodInterceptor {

	protected static final Log log = LogFactory.getLog(ReportProfiler.class);

	protected static ThreadLocal<Integer> level = new ThreadLocal<Integer>();

	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (methodInvocation.getMethod().getName().equals("evaluate")) {
			Definition definition = getDefinition(methodInvocation.getArguments()[0]);

			return timed(methodInvocation, definition.getClass().getSimpleName() + " [" + definition.getName() + "]");
		}
		else {
			return methodInvocation.proceed();
		}
	}

	/**
	 * Times the given method invocation and logs the result
	 * @param methodInvocation the method invocation
	 * @param label the label for log output
	 * @return the invocation result
	 */
	protected Object timed(MethodInvocation methodInvocation, String label) throws Throwable {
		int currentLevel = level.get() != null ? level.get() : 1;
		level.set(currentLevel + 1);

		long start = System.currentTimeMillis();
		Object result = methodInvocation.proceed();
		long timeTaken = System.currentTimeMillis() - start;

		level.set(currentLevel);

		log.trace(StringUtils.repeat('>', currentLevel) + " " + timeTaken + " ms to evaluate " + label);

		return result;
	}

	/**
	 * Gets the definition argument of an invocation of evaluate
	 * @param arg a definition or a mapped definition
	 * @return the definition
	 */
	protected Definition getDefinition(Object arg) {
		if (arg instanceof Definition) {
			return (Definition) arg;
		}
		else if (arg instanceof Mapped) {
			return (Definition) ((Mapped) arg).getParameterizable();
		}
		else {
			throw new RuntimeException("Invalid argument passed to evaluate method");
		}
	}
}