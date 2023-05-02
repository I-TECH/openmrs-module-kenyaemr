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

import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.*;

/**
 * Calculate visit before most current visit
 */
public class PreviousHIVClinicalVisitTCACalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        PersonService patientService = Context.getPersonService();
        EncounterService encounterService = Context.getEncounterService();

        CalculationResultMap ret = new CalculationResultMap();

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        Form pocHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
        Form rdeHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
        EncounterType hivFollowup = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);

        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
        Set<Integer> transferredOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));

        for (Integer ptId : cohort) {
            Date encTCADate = null;
            Encounter secondLastEnc = null;
            if (alive.contains(ptId) && !ltfu.contains(ptId) && !transferredOut.contains(ptId)) {
                List<Encounter> encounters = encounterService.getEncounters((Patient) patientService.getPerson(ptId), null, null, null, Arrays.asList(pocHivFollowup, rdeHivFollowup), Collections.singletonList(hivFollowup), null, null, null, false);

                if (encounters.size() == 1) {
                    secondLastEnc = encounters.get(0);
                } else if (encounters.size() > 1) {
                    secondLastEnc = encounters.get(encounters.size() - 2);
                }
                if (secondLastEnc != null) {
                    for (Obs obs : secondLastEnc.getObs()) {
                        if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE))) {
                            encTCADate = obs.getValueDatetime();
                        }
                    }
                }
                ret.put(ptId, new SimpleResult(encTCADate, this));

            }
        }
        return ret;
    }
}
