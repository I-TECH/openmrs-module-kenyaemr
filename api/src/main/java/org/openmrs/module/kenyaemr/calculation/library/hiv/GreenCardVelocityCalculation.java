/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;


/**
 * Calculates a consolidation of greencard validations such as :
 * In tb program
 * Not started on tb drugs - due for tb enrollment
 * In IPT program
 * On ART
 *
 */
public class GreenCardVelocityCalculation extends BaseEmrCalculation {

    protected static final Log log = LogFactory.getLog(GreenCardVelocityCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Set<Integer> alive = Filters.alive(cohort, context);
        //Check whether in tb program
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
        //Check whether in tb greencard
        Concept OnAntiTbQuestion = Context.getConceptService().getConcept(164948);
        Concept StartAntiTbQuestion = Context.getConceptService().getConcept(162309);

        CalculationResultMap tbCurrent = Calculations.lastObs(OnAntiTbQuestion, cohort, context);
        CalculationResultMap tbStarted = Calculations.lastObs(StartAntiTbQuestion, cohort, context);

        //Check whether in ipt program
        Concept IptCurrentQuestion = Context.getConceptService().getConcept(164949);
        Concept IptStartQuestion = Context.getConceptService().getConcept(1265);
        Concept IptStopQuestion = Context.getConceptService().getConcept(160433);

        CalculationResultMap iptCurrent = Calculations.lastObs(IptCurrentQuestion, cohort, context);
        CalculationResultMap iptStarted = Calculations.lastObs(IptStartQuestion, cohort, context);
        CalculationResultMap iptStopped = Calculations.lastObs(IptStopQuestion, cohort, context);


        // Get active ART regimen of each patient
        Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);
        CalculationResultMap currentARVDrugOrders = activeDrugOrders(arvs, cohort, context);

        // Get ART start date
        CalculationResultMap allDrugOrders = allDrugOrders(arvs, cohort, context);
        CalculationResultMap earliestOrderDates = earliestStartDates(allDrugOrders, context);

        CalculationResultMap ret = new CalculationResultMap();
        StringBuilder sb = new StringBuilder();
        for (Integer ptId : cohort) {
            //TB and ART patients
            boolean patientInTBProgram = false;
            boolean patientDueForTBEnrollment = false;
            boolean patientOnART = false;
            boolean hasBeenOnART = false;
            //IPT Calculation
            Date iptStartObsDate = null;
            Date iptStopObsDate = null;
            Date tbStartObsDate = null;
            Date tbStopObsDate = null;
            Date currentDate =new Date();
            boolean inIptProgram = false;
            boolean currentInIPT = false;
            Integer iptStartStopDiff = 0;
            Integer tbStartStopDiff = 0;
            Integer artStartCurrDiff = 0;

            //Patient with current on anti tb drugs and/or anti tb start dates
            Obs tbCurrentObs = EmrCalculationUtils.obsResultForPatient(tbCurrent, ptId);
            Obs tbStartObs = EmrCalculationUtils.obsResultForPatient(tbStarted, ptId);

            //Patient with IPT start date and now less than complete date
            Obs iptCurrentObs = EmrCalculationUtils.obsResultForPatient(iptCurrent, ptId);
            Obs iptStartObs = EmrCalculationUtils.obsResultForPatient(iptStarted, ptId);
            Obs iptStopObs = EmrCalculationUtils.obsResultForPatient(iptStopped, ptId);

            //Enrolled in tb program
            if (inTbProgram.contains(ptId)) {
                patientInTBProgram = true;
            }
            //Not enrolled but Currently on antiTb drugs
            if (tbCurrentObs != null && tbStartObs != null){
                //Started on antiTb drugs
                if (tbCurrentObs.getValueCoded().getConceptId().equals(1066) && tbStartObs.getValueCoded().getConceptId().equals(1065)) {
                    patientDueForTBEnrollment = true;
                }
            }
            //Currently on IPT
            if (!patientInTBProgram && !patientDueForTBEnrollment && iptCurrentObs != null &&  iptStopObs == null && iptCurrentObs.getValueCoded().getConceptId().equals(1065)) {
                inIptProgram = true;
            }
            //Started on IPT
            if (!patientInTBProgram  && !patientDueForTBEnrollment && iptStartObs != null &&  iptStopObs == null && iptStartObs.getValueCoded().getConceptId().equals(1065)) {
                inIptProgram = true;
            }
            //Repeat on IPT
            if(!patientInTBProgram && !patientDueForTBEnrollment && iptStartObs != null && iptStopObs != null && iptStartObs.getValueCoded().getConceptId().equals(1065)) {
                iptStartObsDate = iptStartObs.getObsDatetime();
                iptStopObsDate = iptStopObs.getObsDatetime();
                iptStartStopDiff = minutesBetween(iptStopObsDate,iptStartObsDate);
                if (iptStartStopDiff > 1) {
                    inIptProgram = true;
                }
            }
            ListResult patientDrugOrders = (ListResult) currentARVDrugOrders.get(ptId);
            if (patientDrugOrders != null) {
                patientOnART = true;
            }

            Date orderDate = EmrCalculationUtils.datetimeResultForPatient(earliestOrderDates, ptId);
            if (orderDate != null) {
                artStartCurrDiff = daysBetween(currentDate,orderDate);
                if (artStartCurrDiff > 3) {
                    hasBeenOnART = true;
                }
            }
            sb.append("inIPT:").append(inIptProgram).append(",");
            sb.append("inTB:").append(patientInTBProgram).append(",");
            sb.append("dueTB:").append(patientDueForTBEnrollment).append(",");
            sb.append("onART:").append(patientOnART).append(",");
            sb.append("hasBeenOnART:").append(hasBeenOnART);

            ret.put(ptId, new SimpleResult(sb.toString(), this, context));
        }
        return ret;
    }
    private int minutesBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Minutes.minutesBetween(d1, d2).getMinutes();

    }
    private int daysBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Math.abs(Days.daysBetween(d1, d2).getDays());
    }
}