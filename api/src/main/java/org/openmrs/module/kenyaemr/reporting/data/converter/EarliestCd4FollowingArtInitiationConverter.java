package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.reporting.model.EarliestCd4;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Coverts the results gotten from { EarliestCd4FollowingArtInitiationCalculation }
 */
public class EarliestCd4FollowingArtInitiationConverter implements DataConverter {

    private String which;

    public EarliestCd4FollowingArtInitiationConverter(String which) {
        this.which = which;
    }

    public String getWhich() {
        return which;
    }

    public void setWhich(String which) {
        this.which = which;
    }

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        Object value = ((CalculationResult) obj).getValue();

        EarliestCd4 results = (EarliestCd4) value;

        if(results == null) {
            return "";
        }

        if(which.equals("date")) {
           return formatDate(results.getObsDate());
        }

        if(which.equals("value")) {
            return results.getValue();
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


    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }
}
