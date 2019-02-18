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
import org.openmrs.Encounter;
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
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Set<Integer> alive = Filters.alive(cohort, context);
        //Check whether in tb program
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        //Check whether in tb greencard
        Concept OnAntiTbQuestion = Context.getConceptService().getConcept(164948);
        Concept StartAntiTbQuestion = Context.getConceptService().getConcept(162309);

        CalculationResultMap tbCurrent = Calculations.lastObs(OnAntiTbQuestion, cohort, context);
        CalculationResultMap tbStarted = Calculations.lastObs(StartAntiTbQuestion, cohort, context);

        //Check whether in ipt program
        Concept IptCurrentQuestion = Context.getConceptService().getConcept(164949);
        Concept IptStartQuestion = Context.getConceptService().getConcept(1265);
        Concept IptStopQuestion = Context.getConceptService().getConcept(160433);
         //Viral load
        Concept latestVL = Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD);
        Concept LDLQuestion = Context.getConceptService().getConcept(1305);
        Concept LDLAnswer = Context.getConceptService().getConcept(1302);
        //Checking adherence
        Concept AdherenceQuestion = Context.getConceptService().getConcept(1658);

        CalculationResultMap iptCurrent = Calculations.lastObs(IptCurrentQuestion, cohort, context);
        CalculationResultMap iptStarted = Calculations.lastObs(IptStartQuestion, cohort, context);
        CalculationResultMap iptStopped = Calculations.lastObs(IptStopQuestion, cohort, context);
        CalculationResultMap lastVLObs = Calculations.lastObs(latestVL, inHivProgram, context);
        CalculationResultMap lastLDLObs = Calculations.lastObs(LDLQuestion, inHivProgram, context);
        CalculationResultMap lastAdherenceObs = Calculations.lastObs(AdherenceQuestion, inHivProgram, context);

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
            Date iptStartDate = null;
            Date tbStartObsDate = null;
            Date tbStopObsDate = null;
            Date adherenceObsDate = null;
            Date currentDate =new Date();
            boolean inIptProgram = false;
            boolean currentInIPT = false;
            boolean patientInIPT6Months = false;
            boolean goodAdherence6Months = false;
            Integer iptStartStopDiff = 0;
            Integer iptCompletionDays = 0;
            Integer adherenceDiffDays = 0;
            Integer  goodAdherenceAnswer = 159405;
            //ART calculations
            String artStartObsDate = null;
            Date artStartDate = null;
            Integer tbStartStopDiff = 0;
            Integer artStartCurrDiff = 0;

            String regimenName = null;
            Obs lastVLObsResult = null;
            String ldlResult = null;
            Double vlResult = 0.0;
                //Patient with current on anti tb drugs and/or anti tb start dates
            Obs tbCurrentObs = EmrCalculationUtils.obsResultForPatient(tbCurrent, ptId);
            Obs tbStartObs = EmrCalculationUtils.obsResultForPatient(tbStarted, ptId);

            //Patient with IPT start date and now less than complete date
            Obs iptCurrentObs = EmrCalculationUtils.obsResultForPatient(iptCurrent, ptId);
            Obs iptStartObs = EmrCalculationUtils.obsResultForPatient(iptStarted, ptId);
            Obs iptStopObs = EmrCalculationUtils.obsResultForPatient(iptStopped, ptId);

            //Viral Load
            Double vl = EmrCalculationUtils.numericObsResultForPatient(lastVLObs, ptId);
            Concept ldl = EmrCalculationUtils.codedObsResultForPatient(lastLDLObs, ptId);

            //Adherence
            Concept adherenceResults = EmrCalculationUtils.codedObsResultForPatient(lastAdherenceObs, ptId);
            // get latest of ldl or vl
            if (ldl != null && vl != null) {
                Obs vlObs = EmrCalculationUtils.obsResultForPatient(lastVLObs, ptId);
                Obs ldlObs = EmrCalculationUtils.obsResultForPatient(lastLDLObs, ptId);
                lastVLObsResult = EmrCalculationUtils.findLastOnOrBefore(Arrays.asList(vlObs, ldlObs), context.getNow());
                if (lastVLObsResult != null && lastVLObsResult.getConcept() == latestVL){
                    vlResult = lastVLObsResult.getValueNumeric();
                    ldlResult = null;
                  }
                 else if (lastVLObsResult != null && (lastVLObsResult.getConcept() == LDLQuestion && lastVLObsResult.getValueCoded() == LDLAnswer)){
                    ldlResult = "LDL";
                     vlResult = 0.0;
                }
            } else if (ldl != null && vl == null) {
                lastVLObsResult = EmrCalculationUtils.obsResultForPatient(lastLDLObs, ptId);
                if(lastVLObsResult != null && (lastVLObsResult.getConcept() == LDLQuestion && lastVLObsResult.getValueCoded() == LDLAnswer)) {
                    ldlResult = "LDL";
                    vlResult = 0.0;
                }
            } else if (ldl == null && vl != null) {
                lastVLObsResult = EmrCalculationUtils.obsResultForPatient(lastVLObs, ptId);
                if(lastVLObsResult != null && lastVLObsResult.getConcept() == latestVL) {
                    vlResult = lastVLObsResult.getValueNumeric();
                    ldlResult = null;
                }
            }else if (ldl == null && vl == null) {
                vlResult = 0.0;
                ldlResult = null;
            }
               // Good adherence in the last 6 months
            if (adherenceResults != null) {
                if (adherenceResults != null && adherenceResults.getConceptId().equals(goodAdherenceAnswer) ) {
                    Obs adherenceObsResults = EmrCalculationUtils.obsResultForPatient(lastAdherenceObs, ptId);
                    adherenceObsDate = adherenceObsResults.getObsDatetime();
                    adherenceDiffDays = daysBetween(currentDate, adherenceObsDate);
                     if (adherenceDiffDays >= 0 && adherenceDiffDays <= 182) {
                         goodAdherence6Months = true;
                     }
                }
            }
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
            //have completed 6 months IPT
            Obs iptObs = EmrCalculationUtils.obsResultForPatient(iptStarted, ptId);
            if (iptObs != null) {

                iptStartDate = iptObs.getObsDatetime();
                iptCompletionDays = daysBetween(currentDate, iptStartDate);
            }

            if (iptObs != null && iptObs.getValueCoded().getConceptId().equals(1065) && iptCompletionDays >= 182) {
                patientInIPT6Months = true;
            }

          //On ART -- find if client has active ART
            Encounter lastDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(Context.getPatientService().getPatient(ptId), "ARV");   //last DRUG_REGIMEN_EDITOR encounter
            if (lastDrugRegimenEditorEncounter != null) {
                SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastDrugRegimenEditorEncounter.getAllObs(), lastDrugRegimenEditorEncounter);
                regimenName = o.get("regimenShortDisplay").toString();
                if (regimenName != null) {
                    patientOnART = true;
                }
            }

            Encounter firstDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getFirstEncounterForCategory(Context.getPatientService().getPatient(ptId), "ARV");   //first DRUG_REGIMEN_EDITOR encounter
            if (firstDrugRegimenEditorEncounter != null) {
                SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(firstDrugRegimenEditorEncounter.getAllObs(), firstDrugRegimenEditorEncounter);
                artStartObsDate =o.get("startDate").toString();
                if (artStartObsDate != null) {
                    try {
                        artStartDate = DATE_FORMAT.parse(artStartObsDate);
                        artStartCurrDiff = daysBetween(currentDate,artStartDate);
                        if (artStartCurrDiff > 3) {
                            hasBeenOnART = true;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }

            sb.append("inIPT:").append(inIptProgram).append(",");
            sb.append("inTB:").append(patientInTBProgram).append(",");
            sb.append("onART:").append(patientOnART).append(",");
            sb.append("hasBeenOnART:").append(hasBeenOnART).append(",");
            sb.append("regimenName:").append(regimenName).append(",");
            sb.append("duration:").append(artStartCurrDiff).append(",");
            sb.append("vlResult:").append(vlResult).append(",");
            sb.append("ldlResult:").append(ldlResult).append(",");
            sb.append("iptCompleted:").append(patientInIPT6Months).append(",");
            sb.append("goodAdherence:").append(goodAdherence6Months);
            // sb.append("dueTB:").append(patientDueForTBEnrollment).append(",");
            // sb.append("artStartDate:").append(artStartDate).append(",");

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