/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.indicator;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.indicator.BaseIndicator;

import java.util.Date;

/**
 * HIV care visit indicator type
 */
public class HivCareVisitsIndicator extends BaseIndicator {

	public enum Filter {
		FEMALES_18_AND_OVER,
		SCHEDULED,
		UNSCHEDULED
	}

	@ConfigurationProperty(group = "when")
	private Date startDate;

	@ConfigurationProperty(group = "when")
	private Date endDate;

	@ConfigurationProperty
	private Filter filter;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}
}