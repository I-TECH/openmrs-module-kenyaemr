/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Definition for patients starting ART
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class DarCurrentOnArtDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

    public static final long serialVersionUID = 1L;
    @ConfigurationProperty
    private Integer minAge;

    @ConfigurationProperty
    private Integer maxAge;

    @ConfigurationProperty
    private String sex;

    /**
     * Default Constructor
     */
    public DarCurrentOnArtDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public DarCurrentOnArtDataDefinition(String name) {
        super(name);
    }

    public DarCurrentOnArtDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        super(name);
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.sex = sex;
    }
//***** INSTANCE METHODS *****

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }
}
