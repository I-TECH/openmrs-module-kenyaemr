/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Returns IPT outcome
 */
public class IPTOutcomeDataConverter implements DataConverter {

	private Log log = LogFactory.getLog(getClass());

	public IPTOutcomeDataConverter() {}

	private String report;

	public IPTOutcomeDataConverter(String report) {
		this.report = report;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	/**
	 * @should return a pre-established labels for IPT outcome
	 */
	@Override
	public Object convert(Object original) {

		if (original == null) {
			return "";
		}

		Object value = ((CalculationResult) original).getValue();
		Concept o = (Concept) value;

		if (o == null)
			return "";

		Integer answer = o.getConceptId();

		if (answer == null)
			return "";

		String label = "";
		if (report != null && report.equals("rdqa")){
			label = "Missing";
		}
		switch (answer){
			case 1267:
				label = "Treatment completed";
				break;
			case 5240:
				label = "Lost to followup";
				break;
			case 159836:
				label = "Discontinued";
				break;
			case 160034:
				label = "Died";
				break;
			case 159492:
				label = "Transferred Out";
				break;
			case 112141:
				label = "Active TB Disease - ATB";
				break;
			case 102:
				label = "Adverse drug reaction - ADR";
				break;
			case 159598:
				label = "Poor adherence - PA";
				break;
			case 5622:
				label = "Others - OTR";
				break;
			default:
				label = "";
		}

		return label;
	}

	@Override
	public Class<?> getInputDataType() {
		return Concept.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}



}
