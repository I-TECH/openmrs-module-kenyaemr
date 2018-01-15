package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.library.models.TransferOutAndDate;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by codehub on 27/08/15.
 */
public class TransferOutAndDateConverter implements DataConverter {
    private String what;

    public TransferOutAndDateConverter(String what) {
        this.what = what;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    @Override
    public Object convert(Object o) {
        if(o == null) {
            return "";
        }
        Object value = ((CalculationResult) o).getValue();
        TransferOutAndDate stateAndDate = (TransferOutAndDate) value;

        if(stateAndDate == null) {
            return "";
        }
        if(what.equals("date")) {
            if(stateAndDate.getDate() == null) {
                return "";
            }
            return formatDate(stateAndDate.getDate());
        }
        if(what.equals("state")) {
            return stateAndDate.getState();

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
