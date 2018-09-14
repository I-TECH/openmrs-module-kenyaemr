package org.openmrs.module.kenyaemr.reporting.data.converter.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * ANC Prophylaxis given Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ANCProphylaxisGivenDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public ANCProphylaxisGivenDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public ANCProphylaxisGivenDataDefinition(String name) {
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
