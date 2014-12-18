package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.Encounter;
import org.openmrs.module.kenyaemr.reporting.RDQAReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter to get obsDatetime from an encounter
 */
public class EncounterDatetimeConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		Encounter e = (Encounter) original;

		if (e == null)
			return null;

		return RDQAReportUtils.formatdates(e.getEncounterDatetime(), RDQAReportUtils.DATE_FORMAT);
	}

	@Override
	public Class<?> getInputDataType() {
		return Encounter.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
