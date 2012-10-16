package org.openmrs.module.kenyaemr.report;

import org.openmrs.module.kenyaemr.calculation.HIVPatientsWhoHaveNeverScreenedForTBCalculation;
import org.springframework.stereotype.Component;

@Component
public class HIVPatientsWhoHaveNeverScreenedForTBCalculationReport extends PatientAlertListReportManager {
	
	public HIVPatientsWhoHaveNeverScreenedForTBCalculationReport(){
		setAlertCalculation(new HIVPatientsWhoHaveNeverScreenedForTBCalculation());
	}

}
