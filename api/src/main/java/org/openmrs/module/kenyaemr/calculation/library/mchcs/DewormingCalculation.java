package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

public class DewormingCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {

		Concept albendazole = Dictionary.getConcept(Dictionary.ALBENDAZOLE);

		Concept mebendazole = Dictionary.getConcept(Dictionary.MEBENDAZOLE);

		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);

		// Get all patients who are alive and in MCH-CS program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inMchcsProgram = Filters.inProgram(mchcsProgram, alive, context);

		// Get medicatio orders
		CalculationResultMap medicationOrders = Calculations.allObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS),
				inMchcsProgram, context);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean hasBeenDewormed = false;

			if (inMchcsProgram.contains(ptId)) {
				ListResult patientMedOrders = (ListResult) medicationOrders.get(ptId);
				if (patientMedOrders != null) {
					// Look through list of medication order obs for any
					// Mabendazole or Albendazole
					List<Obs> medOrderObsList = CalculationUtils.extractResultValues(patientMedOrders);
					for (Obs medOrderObs : medOrderObsList) {
						if (medOrderObs.getValueCoded().equals(mebendazole) || medOrderObs.getValueCoded().equals(albendazole)) {
							hasBeenDewormed = true;
							break;
						}
					}
				}

			}

			ret.put(ptId, new BooleanResult(hasBeenDewormed, this));
		}

		return ret;

	}

}
