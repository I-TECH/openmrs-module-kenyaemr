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

package org.openmrs.module.kenyaemr.fragment.controller.developer;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.groovy.GroovyUtil;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility actions for developers
 */
public class DeveloperUtilsFragmentController {

	/**
	 * Executes a groovy script
	 * @param script the script
	 * @return the result as a simple object {result, output, stacktrace}
	 */
	@AppAction(EmrConstants.APP_DEVELOPER)
	public SimpleObject executeGroovy(@RequestParam("script") String script) {
		String[] result = GroovyUtil.getService().evaluate(script);
		return SimpleObject.create("result", result[0], "output", result[1], "stacktrace", result[2]);
	}

	/**
	 * Gets whether report profiling is enabled
	 */
	@AppAction(EmrConstants.APP_DEVELOPER)
	public SimpleObject getReportProfilingEnabled() {
		return SimpleObject.create("enabled", Level.TRACE.equals(LogManager.getLogger(EvaluationProfiler.class).getLevel()));
	}

	/**
	 * Enables profiling of reports
	 */
	@AppAction(EmrConstants.APP_DEVELOPER)
	public void setReportProfilingEnabled(@RequestParam("enabled") boolean enabled) {
		if (enabled) {
			LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.TRACE);
			LogManager.getLogger("org.openmrs.api").setLevel(Level.WARN); // Switch off general service call logging
		}
		else {
			LogManager.getLogger(EvaluationProfiler.class).setLevel(null);
			LogManager.getLogger("org.openmrs.api").setLevel(Level.INFO); // Switch on general service call logging
		}
	}

	/**
	 * Validate patient records
	 */
	@AppAction(EmrConstants.APP_DEVELOPER)
	public List<SimpleObject> validatePatients(UiUtils ui) {
		List<SimpleObject> problems = new ArrayList<SimpleObject>();

		for (Patient patient : Context.getPatientService().getAllPatients()) {
			BindException errors = new BindException(patient, "");
			Context.getAdministrationService().validate(patient, errors);

			if (errors.hasErrors()) {
				SimpleObject problem = new SimpleObject();
				problem.put("patient", ui.simplifyObject(patient));
				problem.put("errors", uniqueErrorMessages(errors));
				problem.put("cause", errors.getCause());
				problems.add(problem);
			}
		}

		return problems;
	}

	/**
	 * Helper method to extract unique error messages from a bind exception and format them
	 * @param errors the bind exception
	 * @return the messages
	 */
	protected Set<String> uniqueErrorMessages(BindException errors) {
		Set<String> messages = new LinkedHashSet<String>();
		for (Object objerr : errors.getAllErrors()) {
			ObjectError error = (ObjectError) objerr;
			String message = Context.getMessageSourceService().getMessage(error.getCode());

			if (error instanceof FieldError) {
				message = ((FieldError) error).getField() + ": " + message;
			}

			messages.add(message);
		}

		return messages;
	}
}