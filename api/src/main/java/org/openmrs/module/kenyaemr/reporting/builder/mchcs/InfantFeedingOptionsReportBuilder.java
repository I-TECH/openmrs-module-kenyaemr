/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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