/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 23/06/15.
 */
public class LastReturnVisitDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {

        Integer latestTCA = 5096;
        CalculationResultMap ret = new CalculationResultMap();

        Form pocHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
        Form rdeHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
        EncounterType hivFollowup = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);

        for (Integer ptId : cohort) {
            Date returnVisitDate = null;

            Encounter lastFollowUpEncounter = EmrUtils.lastEncounter(Context.getPatientService().getPatient(ptId), hivFollowup,Arrays.asList(pocHivFollowup, rdeHivFollowup));   //last hiv followup form
            if (lastFollowUpEncounter != null) {
                for (Obs obs : lastFollowUpEncounter.getObs()) {
                    if (obs.getConcept().getConceptId().equals(latestTCA)) {

                        returnVisitDate = obs.getValueDatetime();
                        ret.put(ptId, new SimpleResult(returnVisitDate, this));
                    }
                }
            }
        }
        return ret;
    }

}

