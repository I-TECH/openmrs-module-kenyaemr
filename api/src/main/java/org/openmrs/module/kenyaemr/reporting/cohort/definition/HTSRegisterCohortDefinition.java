package org.openmrs.module.kenyaemr.reporting.cohort.definition;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;

import java.util.Date;

/**
 * HTS Register cohort definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.HTSRegisterCohortDefinition")
public class HTSRegisterCohortDefinition extends BaseQuery<Encounter> implements EncounterQuery {

    @ConfigurationProperty
    private Date asOfDate;

    public HTSRegisterCohortDefinition() {
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }
}
