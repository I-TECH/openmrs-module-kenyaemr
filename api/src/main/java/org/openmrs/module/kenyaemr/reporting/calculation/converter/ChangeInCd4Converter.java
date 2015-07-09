package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Created by codehub on 06/07/15.
 */
public class ChangeInCd4Converter implements DataConverter {

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "Missing";
        }

        return  ((CalculationResult) obj).getValue();

    }

    @Override
    public Class<?> getInputDataType() {
        return CalculationResult.class;
    }

    @Override
    public Class<?> getDataType() {
        return Double.class;
    }
}
