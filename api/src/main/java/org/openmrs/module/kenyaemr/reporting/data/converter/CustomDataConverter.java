package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codehub on 09/03/15.
 */
public class CustomDataConverter implements DataConverter {

	@Override
	public Object convert(Object obj) {

		if (obj == null) {
			return "";
		}

		Concept value = ((Obs) obj).getValueCoded();

		if(value != null && value.getName() != null) {

			return maritalValue(value);
		}

		return null;
	}

	@Override
	public Class<?> getInputDataType() {
		return Object.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

    private Integer maritalValue(Concept value){

        if(value.equals(Dictionary.getConcept(Dictionary.MARRIED_MONOGAMOUS))){
            return 2;
        }

        else if(value.equals(Dictionary.getConcept(Dictionary.MARRIED_POLYGAMOUS))){
            return 1;
        }

        else if(value.equals(Dictionary.getConcept(Dictionary.DIVORCED))){
            return 3;
        }

        else if(value.equals(Dictionary.getConcept(Dictionary.WIDOWED))){
            return 4;
        }

        else if(value.equals(Dictionary.getConcept(Dictionary.LIVING_WITH_PARTNER))){
            return 5;
        }

        else if(value.equals(Dictionary.getConcept(Dictionary.NEVER_MARRIED))){
            return 6;
        }

        return null;
    }
}
