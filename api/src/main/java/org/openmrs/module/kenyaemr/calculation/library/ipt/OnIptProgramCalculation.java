/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.ipt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients are (alive and) in the IPT program
 * Eligibility criteria include:
 * Is currently active in IPT program
 *
 */
public class OnIptProgramCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	protected static final Log log = LogFactory.getLog(OnIptProgramCalculation.class);

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Concept IptCurrentQuestion = Context.getConceptService().getConcept(164949);
		Concept IptStartQuestion = Context.getConceptService().getConcept(1265);
		Concept IptStopQuestion = Context.getConceptService().getConcept(160433);

		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

		CalculationResultMap iptCurrent = Calculations.lastObs(IptCurrentQuestion, cohort, context);
		CalculationResultMap iptStarted = Calculations.lastObs(IptStartQuestion, cohort, context);
		CalculationResultMap iptStopped = Calculations.lastObs(IptStopQuestion, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId: cohort){

			Date iptStartObsDate = null;
			Date iptStopObsDate = null;
			boolean tbPatient = false;
			boolean inIptProgram = false;
			boolean currentInIPT = false;
			boolean patientInTbProgram = false;
			Integer iptStartStopDiff = 0;

			Obs iptCurrentObs = EmrCalculationUtils.obsResultForPatient(iptCurrent, ptId);
			Obs iptStartObs = EmrCalculationUtils.obsResultForPatient(iptStarted, ptId);
			Obs iptStopObs = EmrCalculationUtils.obsResultForPatient(iptStopped, ptId);

			//Eligibility not for patients already in tb program
			if (inTbProgram.contains(ptId)) {
				patientInTbProgram = true;
			}

			//Currently on IPT
			if (iptCurrentObs != null &&  iptStopObs == null && iptCurrentObs.getValueCoded().getConceptId().equals(1065)) {
				inIptProgram = true;
			}
			//Started on IPT
			if (iptStartObs != null &&  iptStopObs == null && iptStartObs.getValueCoded().getConceptId().equals(1065)) {
				inIptProgram = true;
			}
			//Repeat on IPT
			if(iptStartObs != null && iptStopObs != null && iptStartObs.getValueCoded().getConceptId().equals(1065)) {
				iptStartObsDate = iptStartObs.getObsDatetime();
				iptStopObsDate = iptStopObs.getObsDatetime();
				iptStartStopDiff = minutesBetween(iptStopObsDate,iptStartObsDate);
				if (iptStartStopDiff > 1) {
					inIptProgram = true;
				}
			}

			if (!patientInTbProgram && inIptProgram)
				currentInIPT = true;

			ret.put(ptId, new BooleanResult(currentInIPT, this));
		}
		return ret;
	}
	private int minutesBetween(Date date1, Date date2) {
		DateTime d1 = new DateTime(date1.getTime());
		DateTime d2 = new DateTime(date2.getTime());
		return Minutes.minutesBetween(d1, d2).getMinutes();

	}

	@Override
	public String getFlagMessage() {
		return "On IPT";
	}

}