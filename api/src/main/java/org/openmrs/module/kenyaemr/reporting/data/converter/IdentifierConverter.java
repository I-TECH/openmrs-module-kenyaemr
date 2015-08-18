package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.List;

/**
 * Created by codehub on 18/08/15.
 */
public class IdentifierConverter implements DataConverter {

    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * returns all patient identifiers split by the common inter-cell split character
     */
    @Override
    public Object convert(Object original) {
        if (original == null)
            return "";

        List<PatientIdentifier> piList = (List<PatientIdentifier>) original;
        if(piList.size() > 0) {
            return piList.get(0).toString();
        }

        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return List.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }

}
