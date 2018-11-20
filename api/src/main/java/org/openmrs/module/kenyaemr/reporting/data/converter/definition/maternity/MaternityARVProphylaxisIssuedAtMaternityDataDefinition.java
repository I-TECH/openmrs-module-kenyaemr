package org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * ARV Prophylaxis issued at maternity Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class MaternityARVProphylaxisIssuedAtMaternityDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public MaternityARVProphylaxisIssuedAtMaternityDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public MaternityARVProphylaxisIssuedAtMaternityDataDefinition(String name) {
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
