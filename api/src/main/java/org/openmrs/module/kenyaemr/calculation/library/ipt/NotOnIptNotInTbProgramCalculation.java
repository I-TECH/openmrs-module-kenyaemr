package org.openmrs.module.kenyaemr.calculation.library.ipt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Years;
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
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

public class NotOnIptNotInTbProgramCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	@Override
	public String getFlagMessage() {
		return "Due for IPT";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {

		/*Program iptProgram = MetadataUtils.existing(Program.class, IPTMetadata._Program.IPT);
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		EncounterType iptOutcome = MetadataUtils.existing(EncounterType.class, IPTMetadata._EncounterType.IPT_OUTCOME);

		// Get all patients who are alive and initiated into IPT
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> onIptProgram = Filters.inProgram(iptProgram, alive, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

		CalculationResultMap lastIptMap = Calculations.lastEncounter(iptOutcome, alive, context);*/
		CalculationResultMap ret = new CalculationResultMap();
/*
		for (Integer ptId : cohort) {

			Boolean notOnIpt = false;

			if (alive.contains(ptId) && !inTbProgram.contains(ptId) && !onIptProgram.contains(ptId)) {
				
				Encounter lastIptEncounter = EmrCalculationUtils.encounterResultForPatient(lastIptMap, ptId);

				if (lastIptEncounter != null) {
					DateTime lastIptEncounterDate = new DateTime(lastIptEncounter.getEncounterDatetime());
					Years yearsSinceLastIpt = Years.yearsBetween(lastIptEncounterDate, new DateTime());

					// check if 2 years have passed since last IPT
					if (yearsSinceLastIpt.getYears() > 2) {
						
						notOnIpt = true;
						
					}
				} else {	
					
					notOnIpt = true;
					
				}
				
			}
			
			ret.put(ptId, new BooleanResult(notOnIpt, this, context));
			
		}*/
		
		return ret;
	}

}
