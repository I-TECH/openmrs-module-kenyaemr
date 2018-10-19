package org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Admission number Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class MaternityVDRLRPRResultsDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public MaternityVDRLRPRResultsDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public MaternityVDRLRPRResultsDataDefinition(String name) {
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
