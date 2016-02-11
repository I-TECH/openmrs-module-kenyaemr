package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 10/23/15.
 * Returns patients phone number
 * return nukk if missing
 */
public class TelephoneNumberCalculation extends AbstractPatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        PersonService personService = Context.getPersonService();
        PersonAttributeType personAttributeType = personService.getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        PersonAttributeCohortDefinition cd = new PersonAttributeCohortDefinition();
        cd.setAttributeType(personAttributeType);

        EvaluatedCohort peopleWithPhoneNumbersCohort = CalculationUtils.evaluateWithReporting(cd, cohort, null, context);

        PersonAttributeDataDefinition personAttributeDataDefinition = new PersonAttributeDataDefinition();
        personAttributeDataDefinition.setPersonAttributeType(personAttributeType);

        CalculationResultMap data = CalculationUtils.evaluateWithReporting(personAttributeDataDefinition, cohort, map, null, context);

        for(Integer ptId: cohort){
            String phoneNumber = null;
            if(peopleWithPhoneNumbersCohort.contains(ptId)) {
                phoneNumber =data.get(ptId).toString();
            }
            ret.put(ptId, new SimpleResult(phoneNumber, this));
        }
        return ret;
    }
}
