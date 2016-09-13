package org.openmrs.module.kenyaemr.calculation.library.tb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
				ret.put(ptId, new BooleanResult(missedScreening, this));
			}
			
		}

		return ret;
	}

}
