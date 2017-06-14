package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import java.util.Collection;
import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

public class PersonAttributeCalculation extends AbstractPatientCalculation {
	private String patientAttributeName;

	public PersonAttributeCalculation(String attributeName) {
		this.patientAttributeName = attributeName;
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			PersonAttribute paObj = null;

			Patient patient = Context.getPatientService().getPatient(ptId);
			paObj = patient.getAttribute(this.patientAttributeName);
			
			if (paObj == null){
				ret.put(ptId, new SimpleResult(paObj, this, context));				
			} else {
				ret.put(ptId, new SimpleResult(paObj.getValue(), this, context));
			}
		}

		return ret;		
		
	}

}
