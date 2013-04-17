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

package org.openmrs.module.kenyaemr.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Report manager
 */
@Component
public class ReportManager {

	protected static final Log log = LogFactory.getLog(ReportManager.class);

	private Map<String, ReportBuilder> reportBuilders = new LinkedHashMap<String, ReportBuilder>();

	/**
	 * Clears all reports
	 */
	public synchronized void clear() {
		reportBuilders.clear();
	}

	/**
	 * Refreshes the list of report builders from application context
	 */
	public synchronized void refreshReportBuilders() {
		clear();

		for (ReportBuilder builder : Context.getRegisteredComponents(ReportBuilder.class)) {
			reportBuilders.put(builder.getClass().getName(), builder);

			log.info("Found report builder class :" + builder.getClass().getName());
		}
	}

	/**
	 * Gets the report manager with the given name
	 * @param className the report builder class name
	 * @return the report builder
	 */
	public ReportBuilder getReportBuilder(String className) {
		return reportBuilders.get(className);
	}

	/**
	 * Gets all report builders
	 * @@return the list of report builders
	 */
	public List<ReportBuilder> getAllReportBuilders() {
		return new ArrayList<ReportBuilder>(reportBuilders.values());
	}

	/**
	 * Gets the report builders with the given tag
	 * @param tag the tag
	 * @return the list of report builders
	 */
	public List<ReportBuilder> getReportBuildersByTag(String tag) {
		List<ReportBuilder> ret = new ArrayList<ReportBuilder>();
		for (ReportBuilder candidate : reportBuilders.values()) {
			if (candidate.getTags() != null && Arrays.asList(candidate.getTags()).contains(tag)) {
				ret.add(candidate);
			}
		}
		return ret;
	}
}