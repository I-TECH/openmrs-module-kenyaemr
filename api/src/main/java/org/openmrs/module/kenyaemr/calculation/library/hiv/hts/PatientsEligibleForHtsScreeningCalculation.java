/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.hts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Checks if a patient is negative, not enrolled and has no previous linkage encounters
 */
public class PatientsEligibleForHtsScreeningCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(PatientsEligibleForHtsScreeningCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        EncounterService encounterService = Context.getEncounterService();
        PatientService patientService = Context.getPatientService();

        Form htsInitialForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_INITIAL_TEST);
        Form htsRetestForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_CONFIRMATORY_TEST);
        Form htsLinkageForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.REFERRAL_AND_LINKAGE);


        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){
            Patient patient = patientService.getPatient(ptId);
            boolean notEnrolled = false;

            List<Encounter> enrollmentEncounters = encounterService.getEncounters(
                    Context.getPatientService().getPatient(ptId),
                    null,
                    null,
                    null,
                    null,
                    Arrays.asList(Context.getEncounterService().getEncounterTypeByUuid("de78a6be-bfc5-4634-adc3-5f1a280455cc")),
                    null,
                    null,
                    null,
                    false
            );

            List<Encounter> linkageEncounters = Context.getEncounterService().getEncounters(patient, null, null, null, Arrays.asList(htsLinkageForm), null, null, null, null, false);

            if(enrollmentEncounters.size() <= 0 && linkageEncounters.size() <= 0) {
                notEnrolled = true;
            }

            ret.put(ptId, new BooleanResult(notEnrolled, this));
        }
        return ret;
    }

}