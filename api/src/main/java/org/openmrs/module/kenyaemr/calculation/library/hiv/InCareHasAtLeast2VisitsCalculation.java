/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Patients who are in care and have at least 2 visits 3 months a part
 */
public class InCareHasAtLeast2VisitsCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);
		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId: cohort){
			boolean has2VisitsWithin3Months = false;
			List<Visit> visits = Context.getVisitService().getVisitsByPatient(Context.getPatientService().getPatient(ptId));

			List<Date> visitDates = new ArrayList<Date>();
			if(inHivProgram.contains(ptId) &&  visits.size() > 1){
				for (Visit visit: visits) {
					visitDates.add(visit.getStartDatetime());
				}
				//check if the list is NOT empty and the visits exceed 2
				if (dateThatAre6MonthsOldFromNow(visitDates, context).size() > 1 && !(dateThatAre6MonthsOldFromNow(visitDates, context).isEmpty())) {
						if(checkIfAnyVisit3MonthsApart(dateThatAre6MonthsOldFromNow(visitDates, context))) {
							has2VisitsWithin3Months = true;
						}
				}
			}
			ret.put(ptId, new BooleanResult(has2VisitsWithin3Months, this, context));
		}
		return ret;
	}
	private List<Date> dateThatAre6MonthsOldFromNow(List<Date> dates, PatientCalculationContext context){
		List<Date> returnDates = new ArrayList<Date>();
		Date reportingTime = context.getNow();//to hold the date when reporting is done
		Date startDate;// to handle the date we expect our visits to have started
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(reportingTime);
		calendar.add(Calendar.MONTH, -6);
		startDate = calendar.getTime();
		for (Date date: dates){
			if(date.after(startDate) && date.before(reportingTime)) {
				returnDates.add(date);
			}
		}

		return returnDates;
	}

	private boolean checkIfAnyVisit3MonthsApart(List<Date> dateList) {
		boolean isTrue = false;
		Collections.reverse(dateList);
		//finding if any of the dates in a list is 3 months a part
		for (int i = 0; i < dateList.size(); i++) {
			for (int j = i+1; j < dateList.size(); j++){
			  if(daysSince(dateList.get(i), dateList.get(j)) >= 85) {
				  isTrue = true;
				  break;
			  }
			}
		}

		return isTrue;
	}

	private int daysSince(Date date1, Date date2) {
		DateTime d1 = new DateTime(date1.getTime());
		DateTime d2 = new DateTime(date2.getTime());
		return Days.daysBetween(d1, d2).getDays();
	}
}
