package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

public class ChildrenGivenVaccineCalculation extends AbstractPatientCalculation {
	private final static Logger logger = Logger.getLogger(ChildrenGivenVaccineCalculation.class);

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {

		Integer vaccinationSequenceNumber = (parameterValues != null && parameterValues.containsKey("sequenceNumber")) ? (Integer) parameterValues.get("sequenceNumber") : 1;
		String vaccineConceptUUID = (parameterValues != null && parameterValues.containsKey("vaccine")) ? (String) parameterValues.get("vaccine") : "";

		Concept immunizationsQuestionConcept = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);
		Concept vaccineConcept = Dictionary.getConcept(vaccineConceptUUID);
		Concept immunizationsHistoryConcept = Dictionary.getConcept(Dictionary.IMMUNIZATION_HISTORY);
		Concept vaccinationSequenceNumberConcept = Dictionary.getConcept(Dictionary.IMMUNIZATION_SEQUENCE_NUMBER);
		
		logger.info("Getting children vaccine with Parameter values " + parameterValues.toString());

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap immunizationsHistoryObsMap = Calculations.allObs(immunizationsHistoryConcept, cohort, context);

		for (Integer ptId : cohort) {

			Boolean vaccineFound = false;
			Boolean vaccineAndSequenceCombinationFound = false;

			ListResult immunizationsHistoryObsForThisChildListResult = (ListResult) immunizationsHistoryObsMap.get(ptId);

			List<Obs> immunizationsHistoryObsForThisChild = CalculationUtils.extractResultValues(immunizationsHistoryObsForThisChildListResult);

			history: for (Obs immunizationsHistoryObs : immunizationsHistoryObsForThisChild) {

				Set<Obs> immunizationHistoryObsGroupMembers = immunizationsHistoryObs.getGroupMembers();

				for (Obs obs : immunizationHistoryObsGroupMembers) {

					if (!vaccineFound) {

						if (obs.getConcept().equals(immunizationsQuestionConcept) && obs.getValueCoded().equals(vaccineConcept)) { // Vaccine administered?

							vaccineFound = true;
							
						}

					} else {

						if (obs.getConcept().getDatatype().isNumeric()) {

							if (obs.getConcept().equals(vaccinationSequenceNumberConcept) && obs.getValueNumeric().equals(new Double(vaccinationSequenceNumber))) {

								vaccineAndSequenceCombinationFound = true;
								break history;

							}

						}

					}

				}
								
			}
			
			ret.put(ptId, new BooleanResult(vaccineAndSequenceCombinationFound, this, context));
			
		}
		
		return ret;

	}
}