package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Return the facility name where a patient was first enrolled into HIV program
 */
public class FacilityNameCalculation extends AbstractPatientCalculation {
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		EncounterType hivEnrollment = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);

		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

		CalculationResultMap havingHivEnrollment = Calculations.lastEncounter(hivEnrollment, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();

		for(Integer ptId: cohort) {
			String loc;
			Location facility = Context.getService(KenyaEmrService.class).getDefaultLocation();
			loc = facility.getName();
			ret.put(ptId, new SimpleResult(loc, this));

		}
		return ret;
	}
}
