/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.converter;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
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

			ConceptService conceptService = org.openmrs.api.context.Context.getConceptService();
			DrugReference drugRef = StringUtils.isNotEmpty(drugRefStr) ? drugReferenceConverter.convert(drugRefStr) : null;
			Double dose = StringUtils.isNotEmpty(doseStr) ? Double.parseDouble(doseStr) : null;
			Concept funits = StringUtils.isNotEmpty(units) ? conceptService.getConcept(units) : null;
			Concept ffrequency = StringUtils.isNotEmpty(frequency) ? conceptService.getConcept(frequency) : null;

			regimen.getComponents().add(new RegimenComponent(drugRef, dose, funits, ffrequency));
		}

		return regimen;
	}
}