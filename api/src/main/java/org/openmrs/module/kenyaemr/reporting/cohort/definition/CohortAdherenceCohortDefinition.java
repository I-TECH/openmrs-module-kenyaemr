package org.openmrs.module.kenyaemr.reporting.cohort.definition;

import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Cohort analysis for 6 months adherence
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.CohortAdherenceCohortDefinition")
public class CohortAdherenceCohortDefinition extends BaseCohortDefinition {

}
