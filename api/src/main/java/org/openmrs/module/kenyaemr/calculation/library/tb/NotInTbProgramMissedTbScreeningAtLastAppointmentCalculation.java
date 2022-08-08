/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class NotInTbProgramMissedTbScreeningAtLastAppointmentCalculation extends AbstractPatientCalculation implements PatientFlagCalculation{

	@Override
	public String getFlagMessage() {
		return "Missed TB Screening";
	}
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {
		
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		EncounterType tbScreening = MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_SCREENING);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
		
		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap lastEncounterMap =  Calculations.lastEncounter(null, alive, context);
		CalculationResultMap lastTbScreeningMap = Calculations.lastEncounter(tbScreening, alive, context);

		for(Integer ptId: cohort){
			Boolean missedScreening = false;

			//Check for patients who are alive but not in TB Program
			if (alive.contains(ptId) && !inTbProgram.contains(ptId)) {
				Encounter lastEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounterMap, ptId);
				Encounter lastTbScreeningEncounter = EmrCalculationUtils.encounterResultForPatient(lastTbScreeningMap, ptId);
				
				DateTime lastEncounterDate = lastEncounter!= null ? new DateTime(lastEncounter.getEncounterDatetime()): null;
				DateTime lastTbScreeningDate = lastTbScreeningEncounter!=null ? new DateTime(lastTbScreeningEncounter.getEncounterDatetime()): null;
				
				if ((lastTbScreeningDate != null && lastEncounterDate != null)) {
					
					if (Math.abs(DateTimeComparator.getDateOnlyInstance().compare(lastTbScreeningDate, lastEncounterDate))>1){
						
						missedScreening = true;
						
					}
					
				}
				
			}
			
			ret.put(ptId, new BooleanResult(missedScreening, this, context));
			
		}

		return ret;
	}

}
