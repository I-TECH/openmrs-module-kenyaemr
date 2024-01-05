/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

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
import java.util.Map;

/**
 * Take the nearest land mark as Physical Address

 */
public class PhysicalAddressCalculation extends AbstractPatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        PersonService personService = Context.getPersonService();
        PersonAttributeType personAttributeType = personService.getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.NEAREST_HEALTH_CENTER);

        PersonAttributeCohortDefinition cd = new PersonAttributeCohortDefinition();
        cd.setAttributeType(personAttributeType);

        EvaluatedCohort physicalAddressCohort = CalculationUtils.evaluateWithReporting(cd, cohort, null, context);

        PersonAttributeDataDefinition personAttributeDataDefinition = new PersonAttributeDataDefinition();
        personAttributeDataDefinition.setPersonAttributeType(personAttributeType);

        CalculationResultMap data = CalculationUtils.evaluateWithReporting(personAttributeDataDefinition, cohort, map, null, context);

        for(Integer ptId: cohort){
            String physicalAddress = null;
            if(physicalAddressCohort.contains(ptId)) {
                physicalAddress =data.get(ptId).toString();
            }
            ret.put(ptId, new SimpleResult(physicalAddress, this));
        }
        return ret;
    }
}
