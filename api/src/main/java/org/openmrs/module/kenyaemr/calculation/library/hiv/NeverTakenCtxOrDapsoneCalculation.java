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
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients have taken CTX or Dapsone
 */
public class NeverTakenCtxOrDapsoneCalculation extends AbstractPatientCalculation {

	@SuppressWarnings("unchecked")
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

		CalculationResultMap medOrdersObss = Calculations.allObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);

		CalculationResultMap ctxProphylaxisObss = Calculations.allObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), cohort, context);

		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

		//calculate those patients who are in care
		//patient who have heard an encounter in the last 3 months
		Date endOfReportingPeriod = DateUtil.getEndOfMonth(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), -1, DurationUnit.DAYS));
		Date startOfReportingPeriod = DateUtil.adjustDate(DateUtil.adjustDate(endOfReportingPeriod, -3, DurationUnit.MONTHS), -1, DurationUnit.DAYS);
		//context.setNow(endOfReportingPeriod);
		CalculationResultMap activePatients = Calculations.allEncounters(null, cohort, context);


		// Get concepts...
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean neverTakenCtxOrDapsone = false;
			boolean isActive = false;
			ListResult listResult = (ListResult) activePatients.get(ptId);
			List<Encounter> allEncounters = CalculationUtils.extractResultValues(listResult);
			for(Encounter encounter : allEncounters) {
				if(encounter.getEncounterDatetime().after(startOfReportingPeriod)) {
					isActive = true;
					break;
				}
			}


			// Is patient alive and in the HIV program
			if (inHivProgram.contains(ptId) && isActive) {
				neverTakenCtxOrDapsone = true;

				// First look to see if they have an obs for taking as prophylaxis
				ListResult ctxProphylaxis = (ListResult) ctxProphylaxisObss.get(ptId);
				if (ctxProphylaxis != null) {
					List<Obs> ctxProphylaxisObsList = CalculationUtils.extractResultValues(ctxProphylaxis);
					for (Obs ctxProphylaxisObs : ctxProphylaxisObsList) {
						if (ctxProphylaxisObs.getValueCoded().equals(yes)) {
							neverTakenCtxOrDapsone = false;
							break;
						}
					}
				}

				// Failing that, look for a med order
				if (neverTakenCtxOrDapsone) {
					ListResult patientMedOrders = (ListResult) medOrdersObss.get(ptId);
					if (patientMedOrders != null) {
						// Look through list of medication order obs for any Dapsone or CTX
						List<Obs> medOrderObsList = CalculationUtils.extractResultValues(patientMedOrders);
						for (Obs medOrderObs : medOrderObsList) {
							if (medOrderObs.getValueCoded().equals(dapsone) || medOrderObs.getValueCoded().equals(ctx)) {
								neverTakenCtxOrDapsone = false;
								break;
							}
						}
					}
				}
			}

			if(ltfu.contains(ptId)){
				neverTakenCtxOrDapsone = false;
			}

			ret.put(ptId, new BooleanResult(neverTakenCtxOrDapsone, this));
		}
		return ret;
	}
}