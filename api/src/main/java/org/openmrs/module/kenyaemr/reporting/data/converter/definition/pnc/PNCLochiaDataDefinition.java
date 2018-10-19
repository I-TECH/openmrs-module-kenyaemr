package org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * ANC Parity Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class PNCLochiaDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public PNCLochiaDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public PNCLochiaDataDefinition(String name) {
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
