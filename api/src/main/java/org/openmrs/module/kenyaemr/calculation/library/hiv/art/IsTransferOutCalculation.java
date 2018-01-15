package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Program;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient is a transfer out based on the status
 */
public class IsTransferOutCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	protected static final Log log = LogFactory.getLog(TransferOutDateCalculation.class);
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		CalculationResultMap inHivProgram = Calculations.activeEnrollment(hivProgram, cohort, context);
		CalculationResultMap transferOutDate = calculate(new TransferOutDateCalculation(), cohort, context);


		CalculationResultMap result = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean isTransferOut = false;
			Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferOutDate, ptId);
			if (inHivProgram.containsKey(ptId) && dateTo != null) {
				isTransferOut = true;
			}
			result.put(ptId, new BooleanResult(isTransferOut, this, context));
//			log.info("Transfer out ==>"+isTransferOut);
		}

		return result;
	}
}
