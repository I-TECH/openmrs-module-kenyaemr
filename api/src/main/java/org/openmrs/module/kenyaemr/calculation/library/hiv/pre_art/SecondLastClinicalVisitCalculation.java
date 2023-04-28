/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art;

import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
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
public class SecondLastClinicalVisitCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        ObsService obsService = Context.getObsService();
        PersonService patientService = Context.getPersonService();
        EncounterService encounterService = Context.getEncounterService();

        CalculationResultMap ret = new CalculationResultMap();
        /*List<Concept> vlConcepts = new ArrayList<Concept>();
        vlConcepts.add(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE));
        vlConcepts.add(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));*/

        //	List<Encounter> encounters = new ArrayList<Encounter>();

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        Form pocHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
        Form rdeHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
        EncounterType hivFollowup = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);

        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
        Set<Integer> transferredOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));

        for (Integer ptId : cohort) {
            Date encDate = null;
            Date encTCADate = null;
            Encounter secondLastEnc = null;
            if (alive.contains(ptId) && !ltfu.contains(ptId) && !transferredOut.contains(ptId)) {
                List<Encounter> encounters = encounterService.getEncounters((Patient) patientService.getPerson(ptId), null, null, null, Arrays.asList(pocHivFollowup, rdeHivFollowup), Collections.singletonList(hivFollowup), null, null, null, false);

                for (Encounter encounter : encounters) {
                    System.out.println("++++++++++++++encounter:" + encounter.getEncounterId() + " Dated: " + encounter.getEncounterDatetime());

                }
                if (encounters.size() == 1) {
                    encDate = encounters.get(0).getEncounterDatetime();
                    secondLastEnc = encounters.get(0);
                    System.out.println("encDate->encounters.get(0).getEncounterDatetime(): " + encounters.get(0).getEncounterDatetime());
                } else if (encounters.size() > 1) {
                    encDate = encounters.get(encounters.size() - 2).getEncounterDatetime();
                    secondLastEnc = encounters.get(encounters.size() - 2);
                    System.out.println("encDate->encounters.get(encounters.size() - 2).getEncounterDatetime(): " + encounters.get(encounters.size() - 2).getEncounterDatetime());
                    System.out.println("secondLastEnc.getEncounterId(): " + secondLastEnc.getEncounterId()+" secondLastEnc.getEncounterDatetime(): "+secondLastEnc.getEncounterDatetime());
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
