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