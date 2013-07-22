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

package org.openmrs.module.kenyaemr.reporting.builder.patientlist;

import org.openmrs.module.kenyaemr.calculation.library.hiv.art.EligibleForArtCalculation;
import org.springframework.stereotype.Component;

/**
 * Eligible for ART report
 */
@Component
public class EligibleForArtReport extends BasePatientCalculationReportBuilder {
	
	public EligibleForArtReport() {
		super(new EligibleForArtCalculation());
	}

	/**
	 * @see org.openmrs.module.kenyacore.reporting.ReportBuilder#getName()
	 */
	@Override
	public String getName() {
		return "Patients eligible for ART";
	}

	/**
	 * @see org.openmrs.module.kenyacore.reporting.ReportBuilder#getTags()
	 */
	@Override
	public String[] getTags() {
		return new String[] { "facility", "hiv" };
	}
}