package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converter class for PatientProgram
 */
public class DateOfLastEnrollmentConverter implements DataConverter {
    @Override
    public Object convert(Object original) {

        Encounter pp = (Encounter) original;

        if (pp == null)
            return "";

        if (pp.getEncounterDatetime() == null)
            return "";

        return  formatDate(pp.getEncounterDatetime());
    }

    @Override
    public Class<?> getInputDataType() {
        return Encounter.class;
    }

    @Override
    public Class<?> getDataType() {
        return Date.class;
    }
    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }


}
