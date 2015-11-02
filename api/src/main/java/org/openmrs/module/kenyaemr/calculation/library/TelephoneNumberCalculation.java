package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        for(Integer ptId: cohort){
            String phoneNumber = null;
           List<PersonAttribute> allPatientsAttribute =  personService.getPerson(ptId).getActiveAttributes();
            for(PersonAttribute personAttribute:allPatientsAttribute){
               if(personAttribute.getAttributeType().getUuid().equals(CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT)){
                   phoneNumber = personAttribute.getValue();
                   break;
               }
            }
            ret.put(ptId, new SimpleResult(phoneNumber, this));
        }
        return ret;
    }
}
