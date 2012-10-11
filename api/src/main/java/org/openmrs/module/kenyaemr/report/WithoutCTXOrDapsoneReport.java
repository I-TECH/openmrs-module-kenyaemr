package org.openmrs.module.kenyaemr.report;

import org.openmrs.module.kenyaemr.calculation.WithoutCTXOrDapsoneCalculation;
import org.springframework.stereotype.Component;

@Component
public class WithoutCTXOrDapsoneReport extends PatientAlertListReportManager{
	
	public WithoutCTXOrDapsoneReport(){
		setAlertCalculation(new WithoutCTXOrDapsoneCalculation());
	}

}
