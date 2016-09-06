package org.openmrs.module.kenyaemr.calculation.library.models;

import java.util.Date;

/**
 * Pojo for CD4 and VL result
 */
public class CD4VLValueAndDate {

    private String concept;
    private Double value;
    private Date date;

    public CD4VLValueAndDate(){}

    public CD4VLValueAndDate(String concept, Double value, Date date) {
        this.concept = concept;
        this.value = value;
        this.date = date;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
