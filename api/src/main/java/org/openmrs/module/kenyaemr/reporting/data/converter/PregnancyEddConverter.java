package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Calendar;
import java.util.Date;

/**
 * Converts the results of pregnancy and edd.
 */
public class PregnancyEddConverter implements DataConverter {

    private String status;

    public PregnancyEddConverter(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override

    public Object convert(Object obj) {
        KenyaUiUtils kenyaui = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);

        if (obj == null) {
            return "N";
        }

        Object value = ((CalculationResult) obj).getValue();

        if (value instanceof String) {

            if(status.equals("date")){
                return kenyaui.formatDate(stringToDate(((String) value).split("=")[1]));
            }

            if(status.equals("status")){
                return ((String) value).split("=")[0];
            }
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

    private Date stringToDate(String date) {
        String datePart = date.split(" ")[0];
        String[] dateParts = datePart.split("-");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(dateParts[0]), (Integer.parseInt(dateParts[1])-1), Integer.parseInt(dateParts[2]));
        Date dateRequired = calendar.getTime();
        return dateRequired;
    }

}
