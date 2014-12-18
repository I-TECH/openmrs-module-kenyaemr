package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DecimalFormat;

/**
 * Converter to get valueNumeric from an observation
 */
public class ObsValueNumericConverter implements DataConverter {

	private Integer precision = null;

	private static final DecimalFormat df = new DecimalFormat();

	private Log log = LogFactory.getLog(getClass());

	public ObsValueNumericConverter() {
		// pass
	}

	public ObsValueNumericConverter(Integer precision) {
		this.setPrecision(precision);
	}

	/**
	 * @should return a blank string if valueNumeric is null
	 */
	@Override
	public Object convert(Object original) {

		Obs o = (Obs) original;

		if (o == null)
			return "Missing";

		if (o.getValueNumeric() == null)
			return "Missing";


		ConceptNumeric cn;
		if (o.getConcept() instanceof ConceptNumeric) {
			cn = (ConceptNumeric) o.getConcept();
		} else {
			cn = Context.getConceptService().getConceptNumeric(o.getConcept().getConceptId());
		}

		if (cn == null)
			return "Missing";

		String units = cn.getUnits();

		String value;
		if (precision != null) {
			value = df.format(o.getValueNumeric());
		} else {
			value = o.getValueNumeric().toString();
		}
		return  value;
		//return String.format("%s %s", value, units);
	}

	@Override
	public Class<?> getInputDataType() {
		return Obs.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
		df.setMinimumFractionDigits(precision);
		df.setMaximumFractionDigits(precision);
	}

}
