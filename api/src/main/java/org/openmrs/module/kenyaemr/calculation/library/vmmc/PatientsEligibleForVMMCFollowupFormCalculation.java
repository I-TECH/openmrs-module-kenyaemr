/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.vmmc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.VMMCMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.VmmcConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Checks if a patient has:
 * Enrolled to VMMC
 * Has vmmc prodedure form
 * Has chosen a procedure method
 * Has immediate post operation procedure form
 */
public class PatientsEligibleForVMMCFollowupFormCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(PatientsEligibleForVMMCFollowupFormCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        PatientService patientService = Context.getPatientService();
        Set<Integer> alive = Filters.alive(cohort, context);
        Program vmmcProgram = MetadataUtils.existing(Program.class, VMMCMetadata._Program.VMMC);
        Set<Integer> inVmmcProgram = Filters.inProgram(vmmcProgram, alive, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort) {
            Patient patient = patientService.getPatient(ptId);

            boolean eligible = false;
            Encounter lastVmmcPostOperationEnc = EmrUtils.lastEncounter(patient, VmmcConstants.vmmcImmediatePostOperationEncType, VmmcConstants.vmmcImmediatePostOperationForm);

            if (inVmmcProgram.contains(ptId) && lastVmmcPostOperationEnc != null) {
                    eligible = true;
              }
                ret.put(ptId, new BooleanResult(eligible, this));
            }
            return ret;
        }
   }