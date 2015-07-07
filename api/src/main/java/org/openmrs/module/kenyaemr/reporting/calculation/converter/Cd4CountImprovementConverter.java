package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Created by codehub on 06/07/15.
 */
public class Cd4CountImprovementConverter implements DataConverter {

    @Override
    public Object convert(Object obj) {
        if (obj == null) {
            return "";
        }

        Object value = ((CalculationResult) obj).getValue();

        if (value instanceof Boolean) {
            return (Boolean) value ? "Yes" : "missing";
        }
        return  null;
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
