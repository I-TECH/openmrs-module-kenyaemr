/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Created by codehub on 11/03/15.
 */
public class ObsNumericConverter implements DataConverter {
	@Override
	public Object convert(Object original) {

		Obs o = (Obs) original;

		if (o == null)
			return "";

		if (o.getValueNumeric() == null)
			return "";


		ConceptNumeric cn;
		if (o.getConcept() instanceof ConceptNumeric) {
			cn = (ConceptNumeric) o.getConcept();
		} else {
			cn = Context.getConceptService().getConceptNumeric(o.getConcept().getConceptId());
		}

		if (cn == null)
			return "";

		return  o.getValueNumeric().toString();
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
