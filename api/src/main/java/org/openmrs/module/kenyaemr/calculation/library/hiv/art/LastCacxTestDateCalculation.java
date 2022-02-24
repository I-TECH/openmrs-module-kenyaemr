/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.module.kenyacore.calculation.Filters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the date on which a patient had their last cervical cancer test
 */
public class LastCacxTestDateCalculation extends BaseEmrCalculation {
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
    /**
     * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     * @should return null for patients who have no cacx test
     * @should return last cacx date for patients who have exsisting cacx tests
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        CalculationResultMap ret = new CalculationResultMap();
        // check for last screening results
        CalculationResultMap cacxScreenResult = Calculations.lastObs(Context.getConceptService().getConcept(165196), cohort, context);
        // check for last cacx assesment
        CalculationResultMap cacxLastAssmnet = Calculations.lastObs(Context.getConceptService().getConcept(164934), cohort, context);
        //get a list of all the cacx screening
        CalculationResultMap cacxAssmntList = Calculations.allObs(Context.getConceptService().getConcept(164934), aliveAndFemale, context);

        for (Integer ptId : cohort) {
            SimpleObject object = null;
            Obs cacxScreenResultObs = EmrCalculationUtils.obsResultForPatient(cacxScreenResult, ptId);
            Obs lastCacxAssmntObs = EmrCalculationUtils.obsResultForPatient(cacxLastAssmnet, ptId);

            //assesed but not screened
            if(lastCacxAssmntObs != null && cacxScreenResultObs == null ) {
                object = SimpleObject.create("lastCacxAssmnet", lastCacxAssmntObs.getValueCoded(), "lastCacxAssmntDate", lastCacxAssmntObs.getObsDatetime());
            }

            //screened but not assesed
            if(lastCacxAssmntObs == null && cacxScreenResultObs != null ) {
                object = SimpleObject.create("lastCacxResult", cacxScreenResultObs.getValueCoded(), "lastCacxDate", cacxScreenResultObs.getObsDatetime());
            }

            // screened and assesed
            if(lastCacxAssmntObs != null && cacxScreenResultObs != null ) {
                object = SimpleObject.create("lastCacxAssmnet", lastCacxAssmntObs.getValueCoded(), "lastCacxResult", cacxScreenResultObs.getValueCoded(), "lastCacxAssmntDate", lastCacxAssmntObs.getObsDatetime(),  "lastCacxResultDate", cacxScreenResultObs.getObsDatetime());
            }


            ret.put(ptId, new SimpleResult(object, this));
        }
        return ret;
    }
}