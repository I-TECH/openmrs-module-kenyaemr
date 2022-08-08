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
 * Missing all registration identifiers
 * Registration identifiers are:
 * ************************
 * Patient Clinic Number
 * National ID Number
 * Passport Number
 * Huduma Number
 * Birth Certificate Number
 */
public class MissingAllRegistrationIdentifiersCalculation extends AbstractPatientCalculation {
	protected static final Log log = LogFactory.getLog(MissingAllRegistrationIdentifiersCalculation.class);

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean missingAllIdentifiers = true;
			PatientService patientService = Context.getPatientService();

			PatientIdentifierType nationalID = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
			PatientIdentifierType passPortNo = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER);
			PatientIdentifierType hudumaNo = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.HUDUMA_NUMBER);
			PatientIdentifierType birthCertNo = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);
			PatientIdentifierType alienID = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.ALIEN_ID_NUMBER);
			PatientIdentifierType drivingLicenseNo = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.DRIVING_LICENSE);

			List<PatientIdentifier> patientRegIdentifiers = patientService.getPatientIdentifiers(null, Arrays.asList(nationalID,passPortNo,hudumaNo,birthCertNo,alienID,drivingLicenseNo), null, Arrays.asList(patientService.getPatient(ptId)), false);
			if(patientRegIdentifiers.size() > 0){
				missingAllIdentifiers = false;
			}
			ret.put(ptId, new BooleanResult(missingAllIdentifiers, this, context));
		}

		return ret;
	}
}