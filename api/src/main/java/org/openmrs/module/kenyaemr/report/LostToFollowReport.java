package org.openmrs.module.kenyaemr.report;

import org.openmrs.module.kenyaemr.calculation.LostToFollowUpCalculation;
import org.springframework.stereotype.Component;

@Component
public class LostToFollowReport extends PatientAlertListReportManager {
	
	public LostToFollowReport(){
	setAlertCalculation(new LostToFollowUpCalculation());
	}
}
