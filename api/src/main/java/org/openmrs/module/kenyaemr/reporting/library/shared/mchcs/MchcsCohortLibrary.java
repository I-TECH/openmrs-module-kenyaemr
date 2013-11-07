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

package org.openmrs.module.kenyaemr.reporting.library.shared.mchcs;



import org.openmrs.Concept;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of MCH-MS related cohort definitions
*/
@Component
public class MchcsCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	public CohortDefinition pcrWithin2Months() {

		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept detected = Dictionary.getConcept(Dictionary.DETECTED);
		Concept equivocal = Dictionary.getConcept(Dictionary.EQUIVOCAL);
		Concept inhibitory = Dictionary.getConcept(Dictionary.INHIBITORY);
		Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		return commonCohorts.hasObs(pcrTest,detected,equivocal,inhibitory,poorSampleQuality);
	}

	public CohortDefinition pcrInitialTest() {
		Concept contexualStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS);
		Concept initial = Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL);
		return commonCohorts.hasObs(contexualStatus,initial);
	}

	public CohortDefinition  pcrInitialWithin2Months() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addSearch("pcrWithin2Months", ReportUtils.map(pcrWithin2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.addSearch("pcrInitialTest", ReportUtils.map(pcrInitialTest(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cd.setCompositionString("pcrWithin2Months AND pcrInitialTest");
		return cd;
	}
}

