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

package org.openmrs.module.kenyaemr.reporting.builder.mchcs;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@Builds({"kenyaemr.mchcs.report.feedingOption"})
public class InfantFeedingOptionsReportBuilder extends CalculationReportBuilder {

	@Override
	protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {

		Concept concept = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		Calendar calendar = Calendar.getInstance();
		Date onOrBefore = calendar.getTime();

		addStandardColumns(report, dsd);
		dsd.addColumn("Feeding Option", new ObsForPersonDataDefinition("Feeding Option", TimeQualifier.LAST, concept, onOrBefore, null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return String.class;
			}

			@Override
			public Object convert(Object input) {
				return ((Obs) input).getValueCoded().getName();
			}
		});
	}
}