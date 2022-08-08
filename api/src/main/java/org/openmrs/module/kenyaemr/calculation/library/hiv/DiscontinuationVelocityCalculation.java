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
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import java.util.*;


public class DiscontinuationVelocityCalculation extends BaseEmrCalculation {

    protected static final Log log = LogFactory.getLog(DiscontinuationVelocityCalculation.class);
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);
        StringBuilder sb = new StringBuilder();
        for (Integer ptId : cohort) {
            Date artStartDat = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);

            Long artStartDate = null;
            Long enrollmentDate = null;

            ProgramWorkflowService service = Context.getProgramWorkflowService();
            List<PatientProgram> programs = service.getPatientPrograms(Context.getPatientService().getPatient(ptId), hivProgram, null, null, null,null, true);
            if (programs.size() > 0) {
                enrollmentDate = programs.get(0).getDateEnrolled().getTime();
            }
            if(artStartDat != null) {
                artStartDate = artStartDat.getTime();
            }

            sb.append("artStartDate:").append(artStartDate).append(",");
            sb.append("enrollmentDate:").append(enrollmentDate);

            ret.put(ptId, new SimpleResult(sb.toString(), this, context));
        }
        return ret;
    }

}