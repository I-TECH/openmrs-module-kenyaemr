package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.reporting.model.PatientEligibility;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converts the medical DateMedicallyEligibleForARTCalculation
 */
public class MedicallyEligibleConverter implements DataConverter {

	public MedicallyEligibleConverter(String which) {
		this.which = which;
	}

	public String getWhich() {
		return which;
	}

	public void setWhich(String which) {
		this.which = which;
	}

	private String which;
	@Override
	public Object convert(Object obj) {

		if (obj == null) {
			return "";
		}

		Object value = ((CalculationResult) obj).getValue();
		PatientEligibility eligibility = (PatientEligibility) value;

			if (eligibility == null) {
				return null;
			}

			if (which == null){
				return null;
			}

			if(which.equals("date")){
				return eligibility.getEligibilityDate() != null ? formatDate(eligibility.getEligibilityDate()) : null;
			}

			if(which.equals("reason")){
				return eligibility.getCriteria() != null ? eligibility.getCriteria() : null;
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
