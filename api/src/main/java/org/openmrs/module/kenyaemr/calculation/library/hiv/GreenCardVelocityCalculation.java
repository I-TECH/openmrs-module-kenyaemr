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
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.IsBreastFeedingCalculation;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.ui.framework.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Calculates a consolidation of greencard validations such as :
 * In tb program
 * Not started on tb drugs - due for tb enrollment
 * In IPT program
 * On ART
 *Is not pregnant
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
        Program iptProgram = MetadataUtils.existing(Program.class, IPTMetadata._Program.IPT);

        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        Set<Integer> activeInIptProgram = Filters.inProgram(iptProgram, alive, context);
        //Check whether in tb greencard
        Concept OnAntiTbQuestion = Context.getConceptService().getConcept(164948);
        Concept StartAntiTbQuestion = Context.getConceptService().getConcept(162309);

        CalculationResultMap tbCurrent = Calculations.lastObs(OnAntiTbQuestion, cohort, context);
        CalculationResultMap tbStarted = Calculations.lastObs(StartAntiTbQuestion, cohort, context);

         //Viral load
        Concept latestVL = Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD);
        Concept LDLQuestion = Context.getConceptService().getConcept(1305);
        Concept LDLAnswer = Context.getConceptService().getConcept(1302);
        //Checking adherence
        Concept AdherenceQuestion = Context.getConceptService().getConcept(1658);

        CalculationResultMap lastVLObs = Calculations.lastObs(latestVL, inHivProgram, context);
        CalculationResultMap lastLDLObs = Calculations.lastObs(LDLQuestion, inHivProgram, context);
        CalculationResultMap lastAdherenceObs = Calculations.lastObs(AdherenceQuestion, inHivProgram, context);

        // Get active ART regimen of each patient

        //find pregnant women
        Set<Integer> pregnantWomen = CalculationUtils.patientsThatPass(calculate(new IsPregnantCalculation(), cohort, context));
        //find breastfeeding women
       Set<Integer> breastFeeding = CalculationUtils.patientsThatPass(calculate(new IsBreastFeedingCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        StringBuilder sb = new StringBuilder();
        for (Integer ptId : cohort) {
            //TB and ART patients
            boolean patientInTBProgram = false;
            boolean patientDueForTBEnrollment = false;
            boolean patientOnART = false;
            boolean hasBeenOnART = false;
            Date adherenceObsDate = null;
            Date currentDate =new Date();
            boolean inIptProgram = false;
            boolean completed6MonthsIPT = false;
            boolean patientEverInHivProgram = false;
            boolean goodAdherence6Months = false;
            boolean isPregnant = false;
            boolean isBreastFeeding = false;
            Integer adherenceDiffDays = 0;
            Integer  goodAdherenceAnswer = 159405;
            Integer  iptOutcomeQuestion = 161555;
            Integer  iptCompletionAnswer = 1267;
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

            //Currently on IPT
            if (activeInIptProgram.contains(ptId)) {
                inIptProgram = true;
            }
            //Not enrolled but Currently on antiTb drugs
            if (tbCurrentObs != null && tbStartObs != null){
                //Started on antiTb drugs
                if (tbCurrentObs.getValueCoded().getConceptId().equals(1066) && tbStartObs.getValueCoded().getConceptId().equals(1065)) {
                    patientDueForTBEnrollment = true;
                }
            }

            //Currently in HIV
            EncounterService encounterService = Context.getEncounterService();
            FormService formService = Context.getFormService();
            PatientService patientService = Context.getPatientService();
            EncounterType et = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.HIV_ENROLLMENT);
            Form form = formService.getFormByUuid(HivMetadata._Form.HIV_ENROLLMENT);
            Patient pt = patientService.getPatient(ptId);
            Encounter lastHivEnrollmentEncounter = EmrUtils.lastEncounter(pt, et);
            if (lastHivEnrollmentEncounter != null ) {
                    patientEverInHivProgram = true;
            }

            //Completed IPT 6 months cycle
            ConceptService cs = Context.getConceptService();
            Concept IptOutcomeQuestionConcept = cs.getConcept(iptOutcomeQuestion);
            Concept IptCompletionOutcomeConcept = cs.getConcept(iptCompletionAnswer);

            Encounter lastIptOutcomeEncounter = EmrUtils.lastEncounter(Context.getPatientService().getPatient(ptId), Context.getEncounterService().getEncounterTypeByUuid(IPTMetadata._EncounterType.IPT_OUTCOME));   //last ipt outcome encounter
            boolean patientHasCompletedIPTOutcome = lastIptOutcomeEncounter != null ? EmrUtils.encounterThatPassCodedAnswer(lastIptOutcomeEncounter, IptOutcomeQuestionConcept, IptCompletionOutcomeConcept) : false;

            if(patientHasCompletedIPTOutcome) {
                completed6MonthsIPT = true;
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
                        artStartCurrDiff = monthsBetween(currentDate,artStartDate);
                        if (artStartCurrDiff > 3) {
                            hasBeenOnART = true;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }

            if(pregnantWomen.contains(ptId)) {
                isPregnant = true;
            }

            if(breastFeeding.contains(ptId)) {
                isBreastFeeding = true;
            }
            sb.append("inIPT:").append(inIptProgram).append(",");
            sb.append("inTB:").append(patientInTBProgram).append(",");
            sb.append("onART:").append(patientOnART).append(",");
            sb.append("hasBeenOnART:").append(hasBeenOnART).append(",");
            sb.append("regimenName:").append(regimenName).append(",");
            sb.append("duration:").append(artStartCurrDiff).append(",");
            sb.append("vlResult:").append(vlResult).append(",");
            sb.append("ldlResult:").append(ldlResult).append(",");
            sb.append("iptCompleted:").append(completed6MonthsIPT).append(",");
            sb.append("goodAdherence:").append(goodAdherence6Months).append(",");
            sb.append("isPregnant:").append(isPregnant).append(",");
            sb.append("isBreastFeeding:").append(isBreastFeeding).append(",");
            sb.append("isEnrolledInHIV:").append(patientEverInHivProgram).append(",");
            sb.append("artStartDate:").append(artStartDate);
            // sb.append("dueTB:").append(patientDueForTBEnrollment).append(",");

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
    private int monthsBetween(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Months.monthsBetween(dateTime1, dateTime2).getMonths());
    }

    /**
     * Evaluates the first program enrollment of the specified program
     * @param program the program
     * @param cohort the patient ids
     * @param context the calculation context
     * @return the enrollments in a calculation result map
     */
    public static CalculationResultMap lastEnrollments(Program program, Collection<Integer> cohort, PatientCalculationContext context) {
        ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
        def.setName("last in " + program.getName());
        def.setWhichEnrollment(TimeQualifier.LAST);
        def.setProgram(program);
        def.setEnrolledOnOrBefore(context.getNow());
        CalculationResultMap results = CalculationUtils.evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, context);
        return CalculationUtils.ensureEmptyListResults(results, cohort);
    }
}