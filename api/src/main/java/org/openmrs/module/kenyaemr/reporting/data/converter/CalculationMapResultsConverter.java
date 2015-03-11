package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 11/03/15.
 */
public class CalculationMapResultsConverter implements DataConverter {

	private String which;
	private Integer level;

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	public CalculationMapResultsConverter(String which, Integer level) {
		this.which = which;
		this.level = level;

	}

	public String getWhich() {
		return which;
	}

	public void setWhich(String which) {
		this.which = which;
	}


	@Override
	public Object convert(Object obj) {
		KenyaUiUtils kenyaui = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);

		if (obj == null) {
			return "";
		}

		Object value = ((CalculationResult) obj).getValue();

		if (value instanceof Map<?,?>) {
			List<String> objItems = new ArrayList<String>();

			for(Object entry : ((Map) value).entrySet()){
				objItems.add(entry.toString());
			}

			if(which.equals("value") && level == 2){
				if(objItems.size() < 3) {
					return objItems.get(0).split("=")[1];
				}
				else {
					return objItems.get(1).split("=")[1];
				}
			}
			if(which.equals("date") && level == 2){
				if(objItems.size() < 3) {
					return kenyaui.formatDate(stringToDate(objItems.get(0).split("=")[0]));
				}
				else {
					return kenyaui.formatDate(stringToDate(objItems.get(1).split("=")[0]));
				}
			}

			if(which.equals("value") && level == 3) {
				if(objItems.size() < 3) {
					return "";
				}
				else {
					return objItems.get(0).split("=")[1];
				}
			}

			if(which.equals("date") && level == 3) {

				if(objItems.size() < 3) {
					return "";
				}
				else {
					return kenyaui.formatDate(stringToDate(objItems.get(0).split("=")[0]));
				}
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

	private Date stringToDate(String date){
		String datePart = date.split(" ")[0];
		String [] dateParts = datePart.split("-");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(dateParts[0]),Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]));
		Date dateRequired = calendar.getTime();
		return dateRequired;
	}
}
