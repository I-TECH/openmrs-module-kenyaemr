/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.otz;

import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

/**
 * Visit ID Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.OTZPatientStillinProgramCohortDefinition")
public class OTZPatientStillinProgramCohortDefinition extends BaseCohortDefinition {


    @ConfigurationProperty
    private Integer month;

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public OTZPatientStillinProgramCohortDefinition() {
        super();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Constructor to populate name only
     */
    public OTZPatientStillinProgramCohortDefinition(Integer month) {
        super();
        this.month = month;
    }


    //***** INSTANCE METHODS *****


    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }


    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Date.class;
    }
}
