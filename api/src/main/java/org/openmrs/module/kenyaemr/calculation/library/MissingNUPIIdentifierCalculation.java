/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.identifier.IdentifierManager;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Missing the NUPI identifier
 */
public class MissingNUPIIdentifierCalculation extends AbstractPatientCalculation {
    protected static final Log log = LogFactory.getLog(MissingNUPIIdentifierCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {
            boolean missingNUPIIdentifier = true;
            PatientService patientService = Context.getPatientService();

            PatientIdentifierType nationalUniquePatientIdentifier = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);

            List<PatientIdentifier> patientRegIdentifier = patientService.getPatientIdentifiers(null, Arrays.asList(nationalUniquePatientIdentifier), null, Arrays.asList(patientService.getPatient(ptId)), false);

            if(patientRegIdentifier.size() > 0){
                missingNUPIIdentifier = false;
            }
            ret.put(ptId, new BooleanResult(missingNUPIIdentifier, this, context));
        }
        return ret;
    }
}