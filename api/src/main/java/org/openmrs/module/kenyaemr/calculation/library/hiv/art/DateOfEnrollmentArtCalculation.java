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

import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculate the date of enrollment into HIV Program
 */
public class DateOfEnrollmentArtCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		CalculationResultMap enrolledHere = Calculations.allEnrollments(hivProgram, cohort, context);
		CalculationResultMap tiEnrollmentDates = Calculations.firstObs(Dictionary.getConcept(Dictionary.DATE_ENROLLED_IN_HIV_CARE), cohort, context);


		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date enrollmentDate = null;
			Date tiEnrollmentDate = null;
			Obs tiEnrollmentDateObs = EmrCalculationUtils.obsResultForPatient(tiEnrollmentDates, ptId);
			if (tiEnrollmentDateObs != null) {
				tiEnrollmentDate = tiEnrollmentDateObs.getValueDatetime();
			}

			ListResult listResult = (ListResult) enrolledHere.get(ptId);
			List<PatientProgram> patientProgram = CalculationUtils.extractResultValues(listResult);
			if(patientProgram.size() > 0){
				enrollmentDate = patientProgram.get(0).getDateEnrolled();

			}

			if (enrollmentDate != null && tiEnrollmentDate != null) {
				if (enrollmentDate.after(tiEnrollmentDate)) {
					ret.put(ptId, new SimpleResult(tiEnrollmentDate, this, context));
				} else if (enrollmentDate.before(tiEnrollmentDate)) {
					ret.put(ptId, new SimpleResult(enrollmentDate, this, context));
				} else {
					ret.put(ptId, new SimpleResult(enrollmentDate, this, context));
				}
			} else if (enrollmentDate != null && tiEnrollmentDate == null) {
				ret.put(ptId, new SimpleResult(enrollmentDate, this, context));
			} else if (tiEnrollmentDate != null && enrollmentDate == null) {
				ret.put(ptId, new SimpleResult(tiEnrollmentDate, this, context));
			} else {
				ret.put(ptId, null);
			}

		}

		return  ret;
	}
}
