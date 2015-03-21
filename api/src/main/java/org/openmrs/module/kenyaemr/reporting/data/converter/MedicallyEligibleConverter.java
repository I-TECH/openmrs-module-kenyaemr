package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

		if (value instanceof String) {
            String [] reasonStr = ((String) value).split("=");
			if(which.equals("date")){
				return formatDate(stringToDate(((String) value).split("=")[0]));
			}

			if(which.equals("reason")){

				return reasonStr[1] != null ? reasonStr[1] : null ;
			}
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

	private Date stringToDate(String date) {
		String datePart = date.split(" ")[0];
		String[] dateParts = datePart.split("-");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(dateParts[0]), (Integer.parseInt(dateParts[1])-1), Integer.parseInt(dateParts[2]));
		Date dateRequired = calendar.getTime();
		return dateRequired;
	}

    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }
}
