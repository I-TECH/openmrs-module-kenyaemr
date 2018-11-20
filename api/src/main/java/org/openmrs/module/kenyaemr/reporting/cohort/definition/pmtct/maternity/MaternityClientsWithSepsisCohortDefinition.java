package org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity;

import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Maternity Register cohort definition
 */

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.MaternityClientsWithSepsisCohortDefinition")
public class MaternityClientsWithSepsisCohortDefinition extends BaseCohortDefinition {

}