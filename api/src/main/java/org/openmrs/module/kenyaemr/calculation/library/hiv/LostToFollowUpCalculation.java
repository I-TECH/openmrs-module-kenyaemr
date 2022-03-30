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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.HtsConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculates whether a patient has been lost to follow up. Calculation returns true if patient
 * is alive and discontinued from Hiv program,
 * Or enrolled in the HIV program, but hasn't had an encounter in LOST_TO_FOLLOW_UP_THRESHOLD_DAYS days = 30 days
 */
public class LostToFollowUpCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	@Override
	public String getFlagMessage() {
		return "Lost to Followup";
	}

	protected static final Log log = LogFactory.getLog(LostToFollowUpCalculation.class);

	/**
	 * Evaluates the calculation
	 * @should calculate false for deceased patients
	 * @should calculate false for patients not in HIV program
	 * @should calculate true for patient with a HIV Greencard encounter and 31 days past since last TCA
	 * OR
	 * Discontinued Hiv clients with reason Lost to followup
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean lost = false;
			Integer tcaConcept = 5096;
			Date tcaDate = null;
			PatientService patientService = Context.getPatientService();
			EncounterService encounterService = Context.getEncounterService();

			Concept reasonForDiscontinuation = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
			Concept transferout = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
			Concept discontinued_ltfu = Dictionary.getConcept(Dictionary.LOST_TO_FOLLOWUP);
			EncounterType hivDiscEncType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_DISCONTINUATION);
			Form hivDiscForm = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_DISCONTINUATION);
			Encounter lastHivDiscontinuationEncounter = EmrUtils.lastEncounter(patientService.getPatient(ptId), hivDiscEncType, hivDiscForm);  //last hiv discontinuation encounter
			EncounterType hivEnrolmentEncounter = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.HIV_ENROLLMENT);
			Encounter lastHivEnrollmentEncounter = EmrUtils.lastEncounter(patientService.getPatient(ptId), hivEnrolmentEncounter);
			// Is patient alive and in HIV program
			if (inHivProgram.contains(ptId)) {
				//With Greencard Encounter
				EncounterType greenCardEncType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
				Form pocHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
				Form rdeHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
				Encounter lastFollowUpEncounter = EmrUtils.lastEncounter(patientService.getPatient(ptId), greenCardEncType, Arrays.asList(pocHivFollowup, rdeHivFollowup));  //last hiv followup encounter

				if (lastFollowUpEncounter != null) {
					for (Obs obs : lastFollowUpEncounter.getObs()) {
						if (obs.getConcept().getConceptId().equals(tcaConcept)) {
							tcaDate = obs.getValueDatetime();
							if (tcaDate != null) {
								if (daysSince(tcaDate, context) > HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS) {
									lost = true;
								}
							}
						}
					}
				}
			}
			// Is patient alive and discontinued from HIV program
			if (alive.contains(ptId) && lastHivDiscontinuationEncounter != null) {          //these clients are no longer in hiv prog
				if (lastHivDiscontinuationEncounter.getEncounterDatetime().after(lastHivEnrollmentEncounter.getEncounterDatetime())) {   // check for re-enrollments
					for (Obs obs : lastHivDiscontinuationEncounter.getObs()) {
						if (obs.getConcept().equals(reasonForDiscontinuation) && obs.getValueCoded().equals(discontinued_ltfu)) {
							lost = true;
						}
						if (obs.getConcept().equals(reasonForDiscontinuation) && obs.getValueCoded().equals(transferout)) {
							lost = false;
						}
					}
					ret.put(ptId, new SimpleResult(lost, this, context));
				}
			}
		}
		return ret;
	}
}
