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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientLastEncounterDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.text.SimpleDateFormat;
import java.util.*;


public class DiscontinuationVelocityCalculation extends BaseEmrCalculation {

    protected static final Log log = LogFactory.getLog(DiscontinuationVelocityCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap lastEncounterDateResMap = calculate(new PatientLastEncounterDateCalculation(), cohort, context);

        Form pocHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
        Form rdeHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
        EncounterType hivFollowup = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);

        StringBuilder sb = new StringBuilder();
        for (Integer ptId : cohort) {
            PatientService patientService = Context.getPatientService();
            Date artStartDat = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);
            Date lastEncDate = EmrCalculationUtils.datetimeResultForPatient(lastEncounterDateResMap, ptId);
            Long artStartDate = null;
            Long enrollmentDate = null;
            Long discDate = null;
            Long lastEncounterDate = null;
            Long lastHIVEncounterDate = null;
            String lastTcaDate = "";
            int tcaConcept = 5096;

            ProgramWorkflowService service = Context.getProgramWorkflowService();
            List<PatientProgram> programs = service.getPatientPrograms(Context.getPatientService().getPatient(ptId), hivProgram, null, null, null, null, true);
            if (programs.size() > 0) {
                enrollmentDate = programs.get(0).getDateEnrolled().getTime();
                discDate = programs.get(0).getDateCompleted() != null ? programs.get(0).getDateCompleted().getTime() : null;
            }
            if (artStartDat != null) {
                artStartDate = artStartDat.getTime();
            }
            if (lastEncDate != null) {
                lastEncounterDate = lastEncDate.getTime();
            }
            //last greencard followup encounter
            Encounter lastHivEncounter = EmrUtils.lastEncounter(patientService.getPatient(ptId), hivFollowup, Arrays.asList(pocHivFollowup, rdeHivFollowup));
            lastHIVEncounterDate = lastHivEncounter != null ? lastHivEncounter.getEncounterDatetime().getTime() : null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            if (lastHivEncounter != null) {
                for (Obs obs : lastHivEncounter.getObs()) {
                    if (obs.getConcept().getConceptId() == tcaConcept) {
                        lastTcaDate = formatter.format(obs.getValueDatetime());
                    }
                }

            }

            sb.append("artStartDate:").append(artStartDate).append(",");
            sb.append("enrollmentDate:").append(enrollmentDate).append(",");
            sb.append("discDate:").append(discDate).append(",");
            sb.append("lastEncounterDate:").append(lastEncounterDate).append(",");
            sb.append("lastHIVEncounterDate:").append(lastHIVEncounterDate).append(",");
            sb.append("lastTcaDate:").append(lastTcaDate);

            ret.put(ptId, new SimpleResult(sb.toString(), this, context));
        }
        return ret;
    }

}