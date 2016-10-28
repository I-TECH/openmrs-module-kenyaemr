package org.openmrs.module.kenyaemr.reporting.library.moh510;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class Moh510CohortLibrary {

	/**
	 * Children enrolled in the Child Welfare Clinic (CWC) between ${onOrAfter} and ${onOrBefore}
	 * @return the cohort definition
	 */
	public CohortDefinition enrolledInCWC() {
		EncounterCohortDefinition enrolledInCWC = new EncounterCohortDefinition();
		
		enrolledInCWC.setName("enrolled in CWC");
		enrolledInCWC.addParameter(new Parameter("onOrAfter", "After date", Date.class));
		enrolledInCWC.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
		enrolledInCWC.setEncounterTypeList(Arrays.asList(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_ENROLLMENT)));
		enrolledInCWC.setFormList(Arrays.asList(MetadataUtils.existing(Form.class, MchMetadata._Form.MCHCS_ENROLLMENT)));
		
		return enrolledInCWC;
	}

}
