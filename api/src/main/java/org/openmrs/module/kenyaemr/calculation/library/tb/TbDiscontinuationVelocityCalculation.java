/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.*;


public class TbDiscontinuationVelocityCalculation extends BaseEmrCalculation {

    protected static final Log log = LogFactory.getLog(TbDiscontinuationVelocityCalculation.class);
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Set<Integer> alive = Filters.alive(cohort,context);
        Program iptProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap tbStartDate = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), alive, context);
        StringBuilder sb = new StringBuilder();
        for (Integer ptId : cohort) {
            Date dateStartedTb = EmrCalculationUtils.datetimeObsResultForPatient(tbStartDate, ptId);
            Long startDate = null;
            Long tbEnrollmentDate = null;

            ProgramWorkflowService service = Context.getProgramWorkflowService();
            List<PatientProgram> programs = service.getPatientPrograms(Context.getPatientService().getPatient(ptId), iptProgram, null, null, null,null, true);
            if (programs.size() > 0) {
                tbEnrollmentDate = programs.get(0).getDateEnrolled().getTime();
            }
            if(dateStartedTb != null) {
                startDate = dateStartedTb.getTime();
            }

            sb.append("tbStartDate:").append(startDate).append(",");
            sb.append("enrollmentDate:").append(tbEnrollmentDate);

            ret.put(ptId, new SimpleResult(sb.toString(), this, context));
        }
        return ret;
    }

}