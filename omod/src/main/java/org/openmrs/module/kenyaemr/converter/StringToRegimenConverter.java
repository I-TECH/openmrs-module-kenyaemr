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

package org.openmrs.module.kenyaemr.converter;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.kenyaemr.regimen.DrugReference;
import org.openmrs.module.kenyaemr.regimen.Regimen;
import org.openmrs.module.kenyaemr.regimen.RegimenComponent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Converts String to Regimen, interpreting it as a | separated list of components
 */
@Component
public class StringToRegimenConverter implements Converter<String, Regimen> {

	private StringToDrugReferenceConverter drugReferenceConverter = new StringToDrugReferenceConverter();

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
    public Regimen convert(String source) {
		String[] tokens = source.split("\\|", -1);
		Queue<String> tokenQueue = new LinkedList<String>(Arrays.asList(tokens));

		Regimen regimen = new Regimen();

		while (tokenQueue.size() >= 4) {
			String drugRefStr = tokenQueue.remove().trim();
			String doseStr = tokenQueue.remove().trim();
			String units = tokenQueue.remove().trim();
			String frequency = tokenQueue.remove().trim();

			DrugReference drugRef = StringUtils.isNotEmpty(drugRefStr) ? drugReferenceConverter.convert(drugRefStr) : null;
			Double dose = StringUtils.isNotEmpty(doseStr) ? Double.parseDouble(doseStr) : null;
			units = StringUtils.isNotEmpty(units) ? units : null;
			frequency = StringUtils.isNotEmpty(frequency) ? frequency : null;

			regimen.getComponents().add(new RegimenComponent(drugRef, dose, units, frequency));
		}

		return regimen;
    }
}