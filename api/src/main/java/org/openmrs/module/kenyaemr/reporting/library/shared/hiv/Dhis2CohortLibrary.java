package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.EncounterType;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of Dhis2 related cohort definitions
 */
@Component
public class Dhis2CohortLibrary {
	@Autowired
	CommonCohortLibrary commonCohortLibrary;
	/**
	 * Providing a dummy definition for the missing pieces in dhis
	 * @return cohort definition
	 */
	public CohortDefinition dummyCohortDefinitionMethod() {

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Need to be changed with correct logic - just a place holder");
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		return commonCohortLibrary.hasEncounter(MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.CONSULTATION));
	}
}
