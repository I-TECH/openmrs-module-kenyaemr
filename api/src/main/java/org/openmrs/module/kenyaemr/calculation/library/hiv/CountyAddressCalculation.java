package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the patient address
 *
 */
public class CountyAddressCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();

		PersonService personService = Context.getPersonService();

		for(Integer ptId : cohort) {
			Person person = personService.getPerson(ptId);
			PersonAddress address = person.getPersonAddress();
			String county = "";
			if(address != null) {
				county = address.getCountry();
			}
			ret.put(ptId, new SimpleResult(county, this));
		}

		return ret;
	}
}
