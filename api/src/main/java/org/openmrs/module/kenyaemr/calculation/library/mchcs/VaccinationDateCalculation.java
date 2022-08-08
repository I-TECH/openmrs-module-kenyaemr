/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.apache.log4j.Logger;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VaccinationDateCalculation extends AbstractPatientCalculation{
	private final static Logger logger = Logger.getLogger(ChildrenGivenVaccineCalculation.class);
	private Integer vaccinationSequenceNumber;
	private String vaccineConceptUuid;

	public VaccinationDateCalculation(String vaccineConceptUuid, Integer sequenceNumber) {

		this.vaccinationSequenceNumber = sequenceNumber;
		this.vaccineConceptUuid = vaccineConceptUuid;
	
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {

		Concept immunizationsQuestionConcept = Dictionary.getConcept(Dictionary.IMMUNIZATIONS);
		Concept vaccineConcept = Dictionary.getConcept(this.vaccineConceptUuid);
		Concept immunizationsHistoryConcept = Dictionary.getConcept(Dictionary.IMMUNIZATION_HISTORY);
		Concept vaccinationSequenceNumberConcept = Dictionary.getConcept(Dictionary.IMMUNIZATION_SEQUENCE_NUMBER);
		
		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap immunizationsHistoryObsMap = Calculations.allObs(immunizationsHistoryConcept, cohort, context);

		for (Integer ptId : cohort) {

			Boolean vaccineFound = false;
			Boolean vaccineAndSequenceCombinationFound = false;

			ListResult immunizationsHistoryObsForThisChildListResult = (ListResult) immunizationsHistoryObsMap.get(ptId);

			List<Obs> immunizationsHistoryObsForThisChild = CalculationUtils.extractResultValues(immunizationsHistoryObsForThisChildListResult);

			Obs vaccinationObs = null;
			
			history: for (Obs immunizationsHistoryObs : immunizationsHistoryObsForThisChild) {

				Set<Obs> immunizationHistoryObsGroupMembers = immunizationsHistoryObs.getGroupMembers();

				for (Obs obs : immunizationHistoryObsGroupMembers) {

					if (!vaccineFound) {

						if (obs.getConcept().equals(immunizationsQuestionConcept) && obs.getValueCoded().equals(vaccineConcept)) { // Vaccine administered?
								
							vaccinationObs = obs;
							vaccineFound = true;
							
						}

					} else {

						if (obs.getConcept().getDatatype().isNumeric()) {

							if (obs.getConcept().equals(vaccinationSequenceNumberConcept) && obs.getValueNumeric().equals(new Double(this.vaccinationSequenceNumber))) {

								vaccineAndSequenceCombinationFound = true;
								break history;

							}

						}

					}
					

				}
								
			}
			
			if (vaccineAndSequenceCombinationFound) {
				
				ret.put(ptId, new SimpleResult(vaccinationObs.getObsDatetime(), this, context));
				
			}
			
		}
		
		return ret;
		
	}

}
