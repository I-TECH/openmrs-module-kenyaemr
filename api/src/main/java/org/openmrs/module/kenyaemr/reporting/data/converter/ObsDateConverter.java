package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Created by codehub on 11/03/15.
 */
public class ObsDateConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		KenyaUiUtils kenyaui = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);
		Obs o = (Obs) original;

		if (o == null)
			return " ";

		return kenyaui.formatDate(o.getObsDatetime());
	}

	@Override
	public Class<?> getInputDataType() {
		return Obs.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}