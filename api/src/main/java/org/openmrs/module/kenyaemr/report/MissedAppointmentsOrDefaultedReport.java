package org.openmrs.module.kenyaemr.report;



import org.openmrs.module.kenyaemr.calculation.MissedAppointmentsOrDefaultedCalculation;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: ningosi
 * Date: 9/21/12
 * Time: 10:34 AM

 */
@Component
public class MissedAppointmentsOrDefaultedReport extends PatientAlertListReportManager{

    public MissedAppointmentsOrDefaultedReport(){ 
        setAlertCalculation(new MissedAppointmentsOrDefaultedCalculation());
        
    }
}
