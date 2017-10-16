package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients are (alive and) in the TB program
 * Eligibility criteria include:
 * Is currently active in tb program
 *
 */
public class PatientInIptProgramCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    protected static final Log log = LogFactory.getLog(PatientInIptProgramCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Concept IptStart = Context.getConceptService().getConcept(1265);
        Concept IptStop = Context.getConceptService().getConcept(160433);

        Set<Integer> alive = Filters.alive(cohort, context);
        //should not be in tb program
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

        CalculationResultMap iptStartMap = Calculations.lastObs(IptStart, cohort, context);
        CalculationResultMap iptStopMap = Calculations.lastObs(IptStop, cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){

            Date iptStartObsDate = null;
            Date iptStopObsDate = null;
            boolean inIptProgram = false;
            boolean currentInIPT = false;
            Integer reCurrentInIPT = 0;
            Integer iptCompletionDays = 0;
            Integer newIptStart = 0;
            Date currentDate =new Date();
            //Patient with IPT start date and now less than complete date
            Obs iptStartObs = EmrCalculationUtils.obsResultForPatient(iptStartMap, ptId);
            Obs iptStopObs = EmrCalculationUtils.obsResultForPatient(iptStopMap, ptId);

            if (inTbProgram.contains(ptId)) {
                currentInIPT = false;
            }
            if(iptStartObs != null) {
                iptStartObsDate = iptStartObs.getObsDatetime();
                iptCompletionDays = daysBetween(currentDate, iptStartObsDate);
            }
            if(iptStartObs != null && iptStopObs != null) {
                iptStartObsDate = iptStartObs.getObsDatetime();
                iptStopObsDate = iptStopObs.getObsDatetime();

                reCurrentInIPT = minutesBetween(iptStopObsDate,iptStartObsDate);
            }
//            log.info("Minutes between  ==> "+reCurrentInIPT);
            //Compute in IPT  used 7 months added 1+6 months  212 days
            if (iptStartObs != null && reCurrentInIPT > 0 && iptStartObs.getValueCoded().getConceptId().equals(1065) && iptCompletionDays <= 212) {
                inIptProgram = true;
            }

            if (inIptProgram)
                currentInIPT = true;

        ret.put(ptId, new BooleanResult(currentInIPT, this));
    }
        return ret;
}

    private int daysBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());

        return Days.daysBetween(d1, d2).getDays();
    }
    private int minutesBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Minutes.minutesBetween(d1, d2).getMinutes();

    }

    @Override
    public String getFlagMessage() {
        return "On IPT";
    }
}