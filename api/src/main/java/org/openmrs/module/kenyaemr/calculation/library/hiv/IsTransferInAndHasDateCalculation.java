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
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.TransferInAndDate;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by codehub on 27/08/15.
 */
public class IsTransferInAndHasDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Concept transferInStatus = Dictionary.getConcept(Dictionary.TRANSFER_IN);
        Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap enrollmentDateMap = Calculations.firstEnrollments(hivProgram, cohort, context);

        CalculationResultMap transferInStatusMap = Calculations.lastObs(transferInStatus, cohort, context);
        CalculationResultMap transferInDateMap = Calculations.lastObs(transferInDate, cohort, context);

        for(Integer ptId : cohort) {
            TransferInAndDate transferInAndDate = null;
            Obs transferInObs = EmrCalculationUtils.obsResultForPatient(transferInStatusMap, ptId);
            Obs transferInDateObs = EmrCalculationUtils.obsResultForPatient(transferInDateMap, ptId);
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(enrollmentDateMap, ptId);

            if(patientProgram != null) {
                if(transferInDateObs != null){
                    transferInAndDate = new TransferInAndDate("Yes", transferInDateObs.getValueDatetime());
                }
                else if(transferInObs != null && transferInObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))) {
                    transferInAndDate = new TransferInAndDate("Yes", transferInObs.getObsDatetime());
                }
                else {
                    transferInAndDate = new TransferInAndDate("No", null);
                }
            }

            ret.put(ptId, new SimpleResult(transferInAndDate, this));

        }
        return ret;
    }
}
