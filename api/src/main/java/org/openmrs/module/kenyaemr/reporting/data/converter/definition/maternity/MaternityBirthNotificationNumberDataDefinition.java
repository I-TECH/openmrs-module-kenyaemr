package org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Maternity Birth Notification Number Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class MaternityBirthNotificationNumberDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public MaternityBirthNotificationNumberDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public MaternityBirthNotificationNumberDataDefinition(String name) {
        super(name);
    }

    //***** INSTANCE METHODS *****

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Double.class;
    }
}
