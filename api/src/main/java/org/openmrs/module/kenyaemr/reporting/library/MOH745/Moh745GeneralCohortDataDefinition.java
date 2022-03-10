/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.MOH745;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Visit ID Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
//public class Moh745GeneralCohortDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
public class Moh745GeneralCohortDataDefinition extends BaseDataDefinition {
    @ConfigurationProperty
    private String visitType;


    @ConfigurationProperty
    private String indicatorVal;

    public Moh745GeneralCohortDataDefinition() {
       super();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Constructor to populate name only
     */
    public Moh745GeneralCohortDataDefinition(String name, String visitType, String indicatorVal) {
        super(name);
        this.visitType = visitType;
        this.indicatorVal = indicatorVal;
    }

    //***** INSTANCE METHODS *****

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getIndicatorVal() {
        return indicatorVal;
    }

    public void setIndicatorVal(String indicatorVal) {
        this.indicatorVal = indicatorVal;
    }


    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
}
