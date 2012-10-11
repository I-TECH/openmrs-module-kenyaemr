package org.openmrs.module.kenyaemr.report;

import org.openmrs.module.kenyaemr.calculation.PatientsOnSecondLineCalculation;
import org.springframework.stereotype.Component;

@Component
public class PatientsOnSecondLineReport extends PatientAlertListReportManager {
	
	public PatientsOnSecondLineReport(){
		setAlertCalculation(new PatientsOnSecondLineCalculation());
	}

}
