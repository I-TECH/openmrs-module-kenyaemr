/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

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
@Builds({"kenyaemr.hiv.report.decliningCd4"})
public class DecliningCd4ReportBuilder extends CalculationReportBuilder {

	@Override
	protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {
		Concept concept = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -180);
		Date onOrBefore = calendar.getTime();

		addStandardColumns(report, dsd);

		dsd.addColumn("Previous CD4", new ObsForPersonDataDefinition("Previous CD4", TimeQualifier.LAST, concept, onOrBefore, null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Double.class;
			}

			@Override
			public Object convert(Object input) {
				return ((Obs) input).getValueNumeric();
			}
		});

		dsd.addColumn("Current CD4", new ObsForPersonDataDefinition("Current CD4", TimeQualifier.LAST, concept, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Double.class;
			}

			@Override
			public Object convert(Object input) {
				return ((Obs) input).getValueNumeric();
			}
		});
	}
}