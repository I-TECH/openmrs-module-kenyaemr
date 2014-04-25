package org.openmrs.module.kenyaemr.calculation.library.hiv.art.cohort.analysis;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * calculates the patient unique number of a patient given when enrolling a patient into HIV program
 */
public class PatientUniqueNumberCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> elligible = Filters.inProgram(hivProgram, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();

		for(Integer pid : cohort) {
			boolean hasUpn = false;
			if(elligible.contains(pid)) {
				 hasUpn = true;
			}
		}

		return ret;
	}
}
