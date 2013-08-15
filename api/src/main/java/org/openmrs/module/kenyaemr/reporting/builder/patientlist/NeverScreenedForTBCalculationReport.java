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

import org.openmrs.module.kenyaemr.calculation.library.tb.NeverScreenedForTbCalculation;
import org.springframework.stereotype.Component;

/**
 * Never screened for TB report
 */
@Component
public class NeverScreenedForTBCalculationReport extends BasePatientCalculationReportBuilder {

	public NeverScreenedForTBCalculationReport() {
		super(new NeverScreenedForTbCalculation());
	}

	/**
	 * @see org.openmrs.module.kenyacore.report.ReportBuilder#getName()
	 */
	@Override
	public String getName() {
		return "Patients who have never been screened for TB";
	}

	/**
	 * @see org.openmrs.module.kenyacore.report.ReportBuilder#getTags()
	 */
	@Override
	public String[] getTags() {
		return new String[] { "facility", "hiv" };
	}
}