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

package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.reporting.model.CurrentIPTStatus;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentIPTStatusConverter implements DataConverter{
	private String question;

	public CurrentIPTStatusConverter(String question) {
		this.question = question;
	}

	@Override
	public Object convert(Object obj) {

		if (obj == null) {
			return "";
		}

		Object value = ((CalculationResult) obj).getValue();
		CurrentIPTStatus status = (CurrentIPTStatus) value;
		if (status == null) {
			return "";
		}

		if (question.equals("status")){
			return status.isCurrentlyOnTreatment()? "Y" : "N";

		} else if (question.equals("startDate")) {
			return formatDate(status.getCurentTreatmentStartDate());
		}

		return null;
	}

	@Override
	public Class<?> getInputDataType() {
		return CalculationResult.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
}
