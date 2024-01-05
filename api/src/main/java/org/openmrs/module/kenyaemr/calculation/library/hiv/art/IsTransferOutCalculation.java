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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateOfLatestEnrollmentHivCalculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates whether a patient is a transfer out based on the status
 */
public class IsTransferOutCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	protected static final Log log = LogFactory.getLog(TransferOutDateCalculation.class);
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		CalculationResultMap transferOutDate = calculate(new TransferOutDateCalculation(), cohort, context);
		CalculationResultMap latestEnrollmentDate = calculate(new DateOfLatestEnrollmentHivCalculation(), cohort, context);

		CalculationResultMap result = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean isTransferOut = false;
			Integer daysSinceTo = 0;

			Date lastEnrollmentDate = EmrCalculationUtils.datetimeResultForPatient(latestEnrollmentDate, ptId);
			Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferOutDate, ptId);


			if (lastEnrollmentDate != null && dateTo != null) {
				daysSinceTo = daysBetween(lastEnrollmentDate, dateTo);
			}
			if(daysSinceTo >= 1){
				isTransferOut = true;
			}
			result.put(ptId, new BooleanResult(isTransferOut, this, context));
//			log.info("Transfer out ==>"+isTransferOut);
		}
		return result;
	}
	private int daysBetween(Date date1, Date date2) {
		DateTime d1 = new DateTime(date1.getTime());
		DateTime d2 = new DateTime(date2.getTime());
		return Days.daysBetween(d1, d2).getDays();
	}
}
