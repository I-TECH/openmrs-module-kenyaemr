package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.reporting.model.DocumentedPregnanciesIn2012;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        if (obj == null) {
            return "";
        }

        Object value = ((CalculationResult) obj).getValue();

        DocumentedPregnanciesIn2012 documentedPregnanciesIn2012 = (DocumentedPregnanciesIn2012) value;

        if (documentedPregnanciesIn2012 == null) {
            return "";
        }

        if (status.equals("status")){
            return documentedPregnanciesIn2012.isPregnant()? "Y" : "N";

        } else if (status.equals("date")) {
            return formatDate(documentedPregnanciesIn2012.getEdd());
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
