package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.library.models.CD4VLValueAndDate;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * handles last CD4 and/or VL result
 */
public class Cd4OrVLValueAndDateConverter implements DataConverter {

    private String what;

    public Cd4OrVLValueAndDateConverter(){

    }

    public Cd4OrVLValueAndDateConverter(String what) {
        this.what = what;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        Object value = ((CalculationResult) obj).getValue();
        CD4VLValueAndDate cd4VLValueAndDate = (CD4VLValueAndDate) value;

        if(cd4VLValueAndDate == null) {
            return  "Missing";
        }
        if(what.equals("date") && cd4VLValueAndDate.getConcept()=="vl") {
            return formatDate(cd4VLValueAndDate.getDate());
        }
        if(what.equals("date") && cd4VLValueAndDate.getConcept()=="cd4") {
            return formatDate(cd4VLValueAndDate.getDate());
        }
        if(what.equals("value") && cd4VLValueAndDate.getConcept()=="vl") {
            return cd4VLValueAndDate.getValue();

        }
        if(what.equals("value") && cd4VLValueAndDate.getConcept()=="cd4") {
            return cd4VLValueAndDate.getValue() + "**";

        }
        return  "Missing";

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
