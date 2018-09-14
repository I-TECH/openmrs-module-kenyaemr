package org.openmrs.module.kenyaemr.reporting.data.converter.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * ANC VL Test Results column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ANCVLTestResultsDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public ANCVLTestResultsDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public ANCVLTestResultsDataDefinition(String name) {
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
