package org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * HEIGivenNVPCTX6_8WeeksDataDefinition Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class HEIGivenNVPCTX6_8WeeksDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public HEIGivenNVPCTX6_8WeeksDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public HEIGivenNVPCTX6_8WeeksDataDefinition(String name) {
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
