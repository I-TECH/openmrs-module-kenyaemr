package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by codehub on 06/07/15.
 */
public class CurrentCd4Converter implements DataConverter {

    private String what;

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public CurrentCd4Converter(String what) {
        this.what = what;
    }

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        Object value = ((CalculationResult) obj).getValue();
        Cd4ValueAndDate cd4ValueAndDate = (Cd4ValueAndDate) value;

        if(cd4ValueAndDate == null) {
            return  "";
        }
        if(what.equals("date")) {
            return formatDate(cd4ValueAndDate.getCd4Date());
        }
        if(what.equals("value")) {
            return cd4ValueAndDate.getCd4Value();

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

    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }
}
