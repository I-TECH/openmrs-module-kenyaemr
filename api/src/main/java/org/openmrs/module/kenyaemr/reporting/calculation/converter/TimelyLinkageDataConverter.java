package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Created by codehub on 18/06/15.
 */
public class TimelyLinkageDataConverter implements DataConverter {

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "Unknown";
        }
        Object value = ((CalculationResult) obj).getValue();
        if(value instanceof Integer) {
            if (((Integer) value) <= 90) {
                return "Yes";
            }
            else if (((Integer) value) > 90) {
                return "No";
            }
        }
        else {
            return "Unknown";
        }

        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return CalculationResult.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }
}
