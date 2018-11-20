package org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

import java.util.Date;
/**
 * Maternity Register cohort definition
 */

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.AssistedVaginalDeliveriesCohortDefinition")
public class AssistedVaginalDeliveriesCohortDefinition extends BaseCohortDefinition {

}