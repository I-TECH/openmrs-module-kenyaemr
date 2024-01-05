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
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates the calculation Patients With Advanced Hiv Disease Calculation
 * 1. In HIV program
 * 1.1 Patients with WHO clinical stage 3 or 4 disease
 * 1.2 Patients with CD4 cell count of <200 cells for adults, adolescents, and children 5yrs and older
 * 1.3 All children younger than five years
 */
public class PatientsWithAdvancedHivDiseaseCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
	protected static final Log log = LogFactory.getLog(PatientsWithAdvancedHivDiseaseCalculation.class);

	@Override
	public String getFlagMessage() {
		return "AHD Client";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		PatientService patientService = Context.getPatientService();
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

		CalculationResultMap lastWhoStage = calculate(new LastWhoStageCalculation(), cohort, context);
		CalculationResultMap lastQuantitativeCd4Obs = calculate(new LastCd4CountCalculation(), cohort, context);
		Concept CD4QualitativeConceptQuestion = Context.getConceptService().getConcept(167718);
		Concept CD4QualitativeConceptAnswer = Context.getConceptService().getConcept(167717);
		CalculationResultMap lastQualitativeCD4Obs = Calculations.lastObs(CD4QualitativeConceptQuestion, inHivProgram, context);
		CalculationResultMap ret = new CalculationResultMap();

			for (Integer ptId : cohort) {
			boolean eligible = false;
			Patient patient = patientService.getPatient(ptId);
			  // 1. In HIV program
			if (inHivProgram.contains(ptId)) {
				// 1.1 Who stage 3 or 4
				if (lastWhoStage != null) {
					Integer whoStage = EmrUtils.whoStage(EmrCalculationUtils.codedObsResultForPatient(lastWhoStage, ptId));
					if (whoStage != null) {
						if (whoStage == 3 || whoStage == 4) {
							eligible = true;
						}
					}
				}
				// 1.2.0 Cd4 Quantitative < 200 and age >= 5
				if (lastQuantitativeCd4Obs != null) {
					Double cd4ResultValue = EmrCalculationUtils.numericObsResultForPatient(lastQuantitativeCd4Obs, ptId);
					if (cd4ResultValue != null) {
						if (cd4ResultValue < 200 && patient.getAge() >= 5) {
							eligible = true;
						}
					}
				}
				// 1.2.1 Cd4 Qualitative CD4 count less than or equal to 200 and age >= 5
				if (lastQualitativeCD4Obs != null) {
					Concept qualitativeCD4Results = EmrCalculationUtils.codedObsResultForPatient(lastQualitativeCD4Obs, ptId);
					if (qualitativeCD4Results != null) {
						if (qualitativeCD4Results.equals(CD4QualitativeConceptAnswer) && patient.getAge() >= 5) {
							eligible = true;
						}
					}
				}
				//1.3 All children younger than five years
			if (patient.getAge() < 5 ) {
				eligible = true;
				}
			}
			ret.put(ptId, new BooleanResult(eligible, this));
		}
		return ret;
	}

}
