package org.openmrs.module.kenyaemr.report;

import org.openmrs.module.kenyaemr.calculation.DeclineCD4Calculation;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: ningosi
 * Date: 9/20/12
 * Time: 12:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class DeclinedCD4Report extends PatientAlertListReportManager{

    public  DeclinedCD4Report(){
        setAlertCalculation(new DeclineCD4Calculation());
    }
}
