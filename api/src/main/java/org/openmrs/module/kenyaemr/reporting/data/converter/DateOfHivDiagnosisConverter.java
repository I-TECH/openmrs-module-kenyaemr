package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.Obs;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by codehub on 19/03/15.
 */
public class DateOfHivDiagnosisConverter implements DataConverter {

    @Override
    public Object convert(Object original) {

        Obs o = (Obs) original;

        if (o == null)
            return " ";

        return formatDate(o.getValueDatetime());
    }

    @Override
    public Class<?> getInputDataType() {
        return Obs.class;
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