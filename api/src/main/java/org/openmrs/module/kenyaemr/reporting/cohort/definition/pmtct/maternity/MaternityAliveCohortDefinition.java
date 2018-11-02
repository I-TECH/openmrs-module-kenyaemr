package org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

import java.util.Date;

/**
 * HTS Register cohort definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ANCRegisterCohortDefinition")
public class MaternityAliveCohortDefinition extends BaseQuery<Encounter> implements EncounterQuery {

    @ConfigurationProperty
    private Date asOfDate;

    public MaternityAliveCohortDefinition() {
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }
}
