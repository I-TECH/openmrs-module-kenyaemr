package org.openmrs.module.kenyaemr.fragment.controller;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CD4AtARTInitiationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4CountDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastViralLoadCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.WeightAtArtInitiationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientSummary;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientProgramEnrollmentCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 10/30/15.
 * A fragment controller for a patient summary details
 */
public class SummariesFragmentController {

    public void controller(@FragmentParam("patient") Patient patient,
                           FragmentModel model){
        PatientSummary patientSummary = new PatientSummary();
        PatientService patientService = Context.getPatientService();
        ObsService obsService = Context.getObsService();
        KenyaEmrService kenyaEmrService = Context.getService(KenyaEmrService.class);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        ConceptService conceptService = Context.getConceptService();
        Date artStartDate = null;

        patientSummary.setDateOfReport(formatDate(new Date()));
        patientSummary.setClinicName(kenyaEmrService.getDefaultLocation().getName());
        patientSummary.setMflCode(kenyaEmrService.getDefaultLocationMflCode());
        //find the names
        patientSummary.setNames(patient.getNames());
        //age
        patientSummary.setAge(age(new Date(), patient.getBirthdate()));
        //birthdate
        patientSummary.setBirthDate(formatDate(patient.getBirthdate()));
        //gender
        patientSummary.setGender(patient.getGender());




        PatientIdentifierType type = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        List<PatientIdentifier> upn = patientService.getPatientIdentifiers(null, Arrays.asList(type), null, Arrays.asList(patient), false);
        if(upn.size() > 0){
            patientSummary.setUpn(upn.get(0).getIdentifier());
        }



        PatientCalculationContext context = Context.getService(PatientCalculationService.class).createCalculationContext();
        context.setNow(new Date());

        //get civil status
        CalculationResultMap civilStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.CIVIL_STATUS), Arrays.asList(patient.getId()), context);
        Concept status = EmrCalculationUtils.codedObsResultForPatient(civilStatus, patient.getPatientId());
        if(status != null){
            patientSummary.setMaritalStatus(status.getName().getName());
        }

        //date confirmed hiv positive
        CalculationResultMap hivConfirmation = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), Arrays.asList(patient.getId()), context);
        Date dateConfirmed = EmrCalculationUtils.datetimeObsResultForPatient(hivConfirmation, patient.getPatientId());
        if(dateConfirmed != null){
            patientSummary.setHivConfrimedDate(formatDate(dateConfirmed));
        }
        else {
            patientSummary.setHivConfrimedDate("");
        }

        //first cd4 count
        CalculationResultMap firstCd4CountMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.CD4_COUNT), Arrays.asList(patient.getId()), context);
        Obs cd4Value = EmrCalculationUtils.obsResultForPatient(firstCd4CountMap, patient.getPatientId());
        if(cd4Value != null){
            patientSummary.setFirstCd4(cd4Value.getValueNumeric().toString());
            patientSummary.setFirstCd4Date(formatDate(cd4Value.getObsDatetime()));

        }
        else {
            patientSummary.setFirstCd4("");
            patientSummary.setFirstCd4Date("");
        }
        //date enrolled into care
        CalculationResultMap enrolled = Calculations.firstEnrollments(hivProgram, Arrays.asList(patient.getPatientId()), context);
        PatientProgram program = EmrCalculationUtils.resultForPatient(enrolled, patient.getPatientId());
        if(program != null) {
            patientSummary.setDateEnrolledIntoCare(formatDate(program.getDateEnrolled()));
        }
        else {
            patientSummary.setDateEnrolledIntoCare("");
        }

        //who staging
        CalculationResultMap whoStage = Calculations.firstObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), Arrays.asList(patient.getPatientId()), context);
        Obs firstWhoStageObs = EmrCalculationUtils.obsResultForPatient(whoStage, patient.getPatientId());
        if(firstWhoStageObs != null){
            patientSummary.setWhoStagingAtEnrollment(whoStaging(firstWhoStageObs.getValueCoded()));
        }
        else {
            patientSummary.setWhoStagingAtEnrollment("");
        }

        //patient entry point
        CalculationResultMap entryPointMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT), Arrays.asList(patient.getPatientId()), context);
        Obs entryPointObs = EmrCalculationUtils.obsResultForPatient(entryPointMap, patient.getPatientId());
        patientSummary.setPatientEntryPoint(entryPointAbbriviations(entryPointObs.getValueCoded()));
        patientSummary.setDateEntryPoint(formatDate(entryPointObs.getObsDatetime()));

        Set<PersonName> names = patient.getNames();
        StringBuilder stringBuilder = new StringBuilder();
        for(PersonName name: names){
            stringBuilder.append(name);
        }

        //transfer in date
        CalculationResult transferInResults = EmrCalculationUtils.evaluateForPatient(TransferInDateCalculation.class, null, patient);
        if(transferInResults != null) {
            patientSummary.setTransferInDate(formatDate((Date) transferInResults.getValue()));
        }
        else {
            patientSummary.setTransferInDate("");
        }
        //facility transferred form
        CalculationResultMap transferInFacilty = Calculations.lastObs(Dictionary.getConcept("160535AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        Obs faciltyObs = EmrCalculationUtils.obsResultForPatient(transferInFacilty, patient.getPatientId());
        if(faciltyObs != null){
            patientSummary.setTransferInFacility(faciltyObs.getValueText());
        }
        else {
            patientSummary.setTransferInFacility("");
        }
        //treatment suppoter details
        CalculationResultMap treatmentSupporterName = Calculations.lastObs(Dictionary.getConcept("160638AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap treatmentSupporterRelation = Calculations.lastObs(Dictionary.getConcept("160640AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap treatmentSupporterContacts = Calculations.lastObs(Dictionary.getConcept("160642AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);

        Obs treatmentSupporterNameObs = EmrCalculationUtils.obsResultForPatient(treatmentSupporterName, patient.getPatientId());
        Obs treatmentSupporterRelationObs = EmrCalculationUtils.obsResultForPatient(treatmentSupporterRelation, patient.getPatientId());
        Obs treatmentSupporterContactsObs = EmrCalculationUtils.obsResultForPatient(treatmentSupporterContacts, patient.getPatientId());
        if(treatmentSupporterNameObs != null){
            patientSummary.setNameOfTreatmentSupporter(treatmentSupporterNameObs.getValueText());
        }
        else {
            patientSummary.setNameOfTreatmentSupporter("");
        }

        if(treatmentSupporterRelationObs != null){
            patientSummary.setRelationshipToTreatmentSupporter(treatmentSupporterRelationObs.getValueCoded().getName().getName());
        }
        else {
            patientSummary.setRelationshipToTreatmentSupporter("");
        }

        if(treatmentSupporterContactsObs != null){
            patientSummary.setContactOfTreatmentSupporter(treatmentSupporterContactsObs.getValueText());
        }
        else {
            patientSummary.setContactOfTreatmentSupporter("");
        }

        //allergies
        CalculationResultMap alergies = Calculations.allObs(Dictionary.getConcept("160643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        ListResult allergyResults = (ListResult) alergies.get(patient.getPatientId());
        List<Obs> listOfAllergies = CalculationUtils.extractResultValues(allergyResults);
        for(Obs obs:listOfAllergies){
            if(obs != null) {
                patientSummary.setDrigAllergies(obs.getValueCoded().getName().getName());
            }
            else {
                patientSummary.setDrigAllergies("");
            }
        }
        //previous art details
        CalculationResultMap previousArt = Calculations.allObs(Dictionary.getConcept("160533AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        ListResult previousArtList = (ListResult) previousArt.get(patient.getPatientId());
        List<Obs> previousArtObsList = CalculationUtils.extractResultValues(previousArtList);
        for(Obs obs:previousArtObsList) {

            if (obs.getValueCoded() != null && obs.getValueCoded().getConceptId() == 1 &&  obs.getVoided().equals(false)) {
                patientSummary.setPreviousArt("Yes");
                break;
            } else if (obs.getValueCoded() != null && obs.getValueCoded().getConceptId() == 2 &&  obs.getVoided().equals(false)) {
                patientSummary.setPreviousArt("No");
                break;
            } else {
                patientSummary.setPreviousArt("");
            }
        }
        //set the purpose for previous art
        CalculationResultMap previousArtPurposePmtct = Calculations.lastObs(Dictionary.getConcept("1148AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap previousArtPurposePep = Calculations.lastObs(Dictionary.getConcept("1691AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap previousArtPurposeHaart = Calculations.lastObs(Dictionary.getConcept("1181AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        Obs previousArtPurposePmtctObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposePmtct, patient.getPatientId());
        Obs previousArtPurposePepObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposePep, patient.getPatientId());
        Obs previousArtPurposeHaartObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposeHaart, patient.getPatientId());
        String purposeString = "";
        if(previousArtPurposePmtctObs != null && previousArtPurposePmtctObs.getValueCoded() != null) {
            purposeString +=previousArtReason(previousArtPurposePmtctObs.getConcept());
        }
        if(previousArtPurposePepObs != null && previousArtPurposePepObs.getValueCoded() != null){
            purposeString += ","+previousArtReason(previousArtPurposePepObs.getConcept());
        }
        if(previousArtPurposeHaartObs != null && previousArtPurposeHaartObs.getValueCoded() != null){
            purposeString +=","+ previousArtReason(previousArtPurposeHaartObs.getConcept());
        }
        if(purposeString.isEmpty()){
           patientSummary.setArtPurpose("");
        }
        else {
            patientSummary.setArtPurpose(purposeString);
        }

        //art start date
        CalculationResult artStartDateResults = EmrCalculationUtils.evaluateForPatient(InitialArtStartDateCalculation.class, null, patient);
        if(artStartDateResults != null) {
            artStartDate = (Date) artStartDateResults.getValue();
            patientSummary.setDateStartedArt(formatDate((Date) artStartDateResults.getValue()));
        }
        else {
            patientSummary.setDateStartedArt("");
        }

        //Clinical stage at art start
        CalculationResult whoStageAtArtStartResults = EmrCalculationUtils.evaluateForPatient(WhoStageAtArtStartCalculation.class, null,patient);
        if(whoStageAtArtStartResults != null){
            patientSummary.setClinicalStageAtArtStart(whoStageAtArtStartResults.getValue().toString());
        }
        else {
            patientSummary.setClinicalStageAtArtStart("");
        }

        //cd4 at art initiation
        CalculationResult cd4AtArtStartResults = EmrCalculationUtils.evaluateForPatient(CD4AtARTInitiationCalculation.class, null,patient);
        if(cd4AtArtStartResults != null){
            patientSummary.setCd4AtArtStart(cd4AtArtStartResults.getValue().toString());
        }
        else {
            patientSummary.setCd4AtArtStart("");
        }

        //weight at art initiation
        CalculationResult weightAtArtStartResults = EmrCalculationUtils.evaluateForPatient(WeightAtArtInitiationCalculation.class, null,patient);
        if(weightAtArtStartResults != null){
            patientSummary.setWeightAtArtStart(weightAtArtStartResults.getValue().toString());
        }
        else {
            patientSummary.setWeightAtArtStart("");
        }

        //first regimen for the patient
        CalculationResult firstRegimenResults = EmrCalculationUtils.evaluateForPatient(InitialArtRegimenCalculation.class, null, patient);
        if(firstRegimenResults != null) {
            RegimenOrder ro = (RegimenOrder) firstRegimenResults.getValue();
            List<String> components = new ArrayList<String>();
            if (ro != null) {
                for (DrugOrder o : ro.getDrugOrders()) {
                    ConceptName cn = o.getConcept().getPreferredName(CoreConstants.LOCALE);
                    if (cn == null) {
                        cn = o.getConcept().getName(CoreConstants.LOCALE);
                    }
                    components.add(cn.getName());
                }
                patientSummary.setFirstRegimen(getRegimenName(standardRegimens(), components));
            }
        }

        else {
            patientSummary.setFirstRegimen("");
        }
        //previous drugs/regimens and dates
        String regimens = "";
        String regimenDates = "";
        CalculationResultMap pmtctRegimen = Calculations.lastObs(Dictionary.getConcept("966AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap pepAndHaartRegimen = Calculations.lastObs(Dictionary.getConcept("1088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        Obs pmtctRegimenObs = EmrCalculationUtils.obsResultForPatient(pmtctRegimen, patient.getPatientId());
        Obs pepAndHaartRegimenObs = EmrCalculationUtils.obsResultForPatient(pepAndHaartRegimen, patient.getPatientId());
        if(pmtctRegimenObs != null){
            regimens += pmtctRegimenObs.getValueCoded().getName().getName();
            regimenDates += formatDate(pmtctRegimenObs.getObsDatetime());
        }
        if(pepAndHaartRegimenObs != null){
            regimens +=","+pepAndHaartRegimenObs.getValueCoded().getName().getName();
            regimenDates +=","+formatDate(pepAndHaartRegimenObs.getObsDatetime());
        }
        patientSummary.setPurposeDrugs(regimens);
        patientSummary.setPurposeDate(regimenDates);

        //past or current ois
        CalculationResultMap problemsAdded = Calculations.allObs(Dictionary.getConcept(Dictionary.PROBLEM_ADDED), Arrays.asList(patient.getPatientId()), context);
        ListResult problemsAddedList = (ListResult) problemsAdded.get(patient.getPatientId());
        List<Obs> problemsAddedListObs = CalculationUtils.extractResultValues(problemsAddedList);
        List<String> ios = new ArrayList<String>();
        for(Obs obs:problemsAddedListObs) {
            ios.add(ios(obs.getValueCoded()));
        }
        if(ios.size() > 0) {
            patientSummary.setOis(ios);
        }
        else {
            patientSummary.setOis(Collections.<String>emptyList());
        }
        //current art regimen
        CalculationResult currentRegimenResults = EmrCalculationUtils.evaluateForPatient(CurrentArtRegimenCalculation.class, null, patient);
        if(currentRegimenResults != null) {
            RegimenOrder roCurrent = (RegimenOrder) currentRegimenResults.getValue();
            List<String> componentsCurrent = new ArrayList<String>();

            if (roCurrent != null) {
                for (DrugOrder drugOrder : roCurrent.getDrugOrders()) {
                    ConceptName cnn = drugOrder.getConcept().getPreferredName(CoreConstants.LOCALE);
                    if (cnn == null) {
                        cnn = drugOrder.getConcept().getName(CoreConstants.LOCALE);
                    }
                    componentsCurrent.add(cnn.getName());
                }
                patientSummary.setCurrentArtRegimen(getRegimenName(standardRegimens(), componentsCurrent));
            }
        }
        else {
            patientSummary.setCurrentArtRegimen("");
        }

        //current who staging
        CalculationResult currentWhoStaging = EmrCalculationUtils.evaluateForPatient(LastWhoStageCalculation.class, null, patient);
        if(currentWhoStaging != null){
            patientSummary.setCurrentWhoStaging(whoStaging(((Obs) currentWhoStaging.getValue()).getValueCoded()));
        }
        else {
            patientSummary.setCurrentWhoStaging("");
        }
        //find whether this patient has been in CTX
        CalculationResultMap medOrdersMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap medicationDispensedCtx = Calculations.lastObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), Arrays.asList(patient.getPatientId()), context);

        Obs medOrdersMapObs = EmrCalculationUtils.obsResultForPatient(medOrdersMap, patient.getPatientId());
        Obs medicationDispensedCtxObs = EmrCalculationUtils.obsResultForPatient(medicationDispensedCtx, patient.getPatientId());
        if(medOrdersMapObs != null && medOrdersMapObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM))){
            patientSummary.setOnCtx("Yes");
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))){
            patientSummary.setOnCtx("Yes");
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NO))){
            patientSummary.setOnCtx("No");
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))){
            patientSummary.setOnCtx("N/A");
        }
        else {
            patientSummary.setOnCtx("");
        }

        //Find if a patient is on dapsone
        if(medOrdersMapObs != null && medOrdersMapObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.DAPSONE))){
            patientSummary.setDapsone("Yes");
        }
        else {
            patientSummary.setDapsone("");
        }
        //on IPT
        CalculationResultMap medicationDispensedIpt = Calculations.lastObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), Arrays.asList(patient.getPatientId()), context);
        Obs medicationDispensedIptObs = EmrCalculationUtils.obsResultForPatient(medicationDispensedIpt, patient.getPatientId());
        if(medOrdersMapObs != null && medOrdersMapObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.ISONIAZID))){
            patientSummary.setOnIpt("Yes");
        }
        else if(medicationDispensedIptObs != null && medicationDispensedIptObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))){
            patientSummary.setOnIpt("Yes");
        }
        else if(medicationDispensedIptObs != null && medicationDispensedIptObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NO))){
            patientSummary.setOnIpt("No");
        }
        else if(medicationDispensedIptObs != null && medicationDispensedIptObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))){
            patientSummary.setOnIpt("N/A");
        }
        else {
            patientSummary.setOnIpt("");
        }

        //find clinics enrolled
        CalculationResult clinicsEnrolledResult = EmrCalculationUtils.evaluateForPatient(PatientProgramEnrollmentCalculation.class, null, patient);
        Set<String> patientProgramList= new HashSet<String>();
        if(clinicsEnrolledResult != null){
            List<PatientProgram> patientPrograms = (List<PatientProgram>) clinicsEnrolledResult.getValue();
            for(PatientProgram p: patientPrograms) {

                patientProgramList.add(programs(p.getProgram().getProgramId()));
            }
        }
            if(patientProgramList.size() > 0){
                patientSummary.setClinicsEnrolled(patientProgramList.toString());
            }
        else {
                patientSummary.setClinicsEnrolled("");
            }
    //most recent cd4
        CalculationResult cd4Results = EmrCalculationUtils.evaluateForPatient(LastCd4CountDateCalculation.class, null, patient);
        if(cd4Results != null){
            patientSummary.setMostRecentCd4(((Obs) cd4Results.getValue()).getValueNumeric().toString());
            patientSummary.setMostRecentCd4Date(formatDate(((Obs) cd4Results.getValue()).getObsDatetime()));
        }
        else{
            patientSummary.setMostRecentCd4("");
            patientSummary.setMostRecentCd4Date("");
        }

        //most recent viral load
        CalculationResult vlResults = EmrCalculationUtils.evaluateForPatient(LastViralLoadCalculation.class, null, patient);
        if(vlResults != null){
            patientSummary.setMostRecentVl(((Obs) vlResults.getValue()).getValueNumeric().toString());
            patientSummary.setMostRecentVlDate(formatDate(((Obs) vlResults.getValue()).getObsDatetime()));
        }
        else{
            patientSummary.setMostRecentVl("");
            patientSummary.setMostRecentVlDate("");
        }
        // find deceased date
        CalculationResult deadResults = EmrCalculationUtils.evaluateForPatient(DateOfDeathCalculation.class, null, patient);
        if(deadResults != null){
            patientSummary.setDeathDate(formatDate((Date) deadResults.getValue()));
        }
        else{
            patientSummary.setDeathDate("");
        }
        // next appointment date
        CalculationResult returnVisitResults = EmrCalculationUtils.evaluateForPatient(LastReturnVisitDateCalculation.class, null, patient);
        if(returnVisitResults != null){
            patientSummary.setNextAppointmentDate(formatDate((Date) returnVisitResults.getValue()));
        }
        else {
            patientSummary.setNextAppointmentDate("");
        }
        // transfer out date
        CalculationResult totResults = EmrCalculationUtils.evaluateForPatient(TransferOutDateCalculation.class, null, patient);
        if(totResults != null){
            patientSummary.setTransferOutDate(formatDate((Date) totResults.getValue()));
        }
        else {
            patientSummary.setTransferOutDate("");
        }








        model.addAttribute("patient", patientSummary);
        model.addAttribute("names", stringBuilder);
        model.addAttribute("currentRegimen", patientSummary.getCurrentArtRegimen());
        model.addAttribute("onCtx", patientSummary.getOnCtx());
        model.addAttribute("firstRegimen", patientSummary.getFirstRegimen());
        model.addAttribute("onDapsone", patientSummary.getDapsone());
        model.addAttribute("onIpt", patientSummary.getOnIpt());
        model.addAttribute("programs", patientSummary.getClinicsEnrolled());
        model.addAttribute("recentCd4Count", patientSummary.getMostRecentCd4());
        model.addAttribute("recentCd4CountDate", patientSummary.getMostRecentCd4Date());
        model.addAttribute("recentVl", patientSummary.getMostRecentVl());
        model.addAttribute("recentVlDate", patientSummary.getMostRecentVlDate());
        model.addAttribute("deadDeath", patientSummary.getDeathDate());
        model.addAttribute("returnVisitDate", patientSummary.getNextAppointmentDate());
        model.addAttribute("toDate", patientSummary.getTransferOutDate());

    }

    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }

    private int age(Date d1, Date d2){
        DateTime birthDate = new DateTime(d1.getTime());
        DateTime today = new DateTime(d2.getTime());

        return Math.abs(Years.yearsBetween(today, birthDate).getYears());
    }

    String entryPointAbbriviations(Concept concept) {
        String value = "Other";
        if(concept.equals(Dictionary.getConcept(Dictionary.VCT_PROGRAM))) {
            value = "VCT";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.PMTCT_PROGRAM))){
            value = "PMTCT";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.PEDIATRIC_INPATIENT_SERVICE))){
            value = "IPD-P";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.ADULT_INPATIENT_SERVICE))){
            value = "IPD-A";
        }

        else if(concept.equals(Dictionary.getConcept("160542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "OPD";
        }

        else if(concept.equals(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM))){
            value = "TB";
        }
        else if(concept.equals(Dictionary.getConcept("160543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "CBO";
        }
        else if(concept.equals(Dictionary.getConcept("160543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "CBO";
        }

        else if(concept.equals(Dictionary.getConcept(Dictionary.UNDER_FIVE_CLINIC))){
            value = "UNDER FIVE";
        }

        else if(concept.equals(Dictionary.getConcept("160546AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "STI";
        }

        else if(concept.equals(Dictionary.getConcept("160548AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "IDU";
        }

        else if(concept.equals(Dictionary.getConcept("160548AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "IDU";
        }

        else if(concept.equals(Dictionary.getConcept(Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM))){
            value = "MCH";
        }

        else if(concept.equals(Dictionary.getConcept("162223AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "VMMC";
        }

        else if(concept.equals(Dictionary.getConcept(Dictionary.TRANSFER_IN))){
            value = "TI";
        }

        return value;
    }

    String whoStaging(Concept concept){
        String stage = "";
        if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_PEDS))){

            stage = "1";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_PEDS))){

            stage = "2";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS))){

            stage = "3";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_PEDS))){

            stage = "4";
        }
        return stage;
    }

    String previousArtReason(Concept concept){
        String value = "";
        if(concept.equals(Dictionary.getConcept("1148AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value ="PMTCT";
        }
        else if(concept.equals(Dictionary.getConcept("1691AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "PEP";
        }

        else if(concept.equals(Dictionary.getConcept("1181AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "HAART";
        }
        return  value;
    }
    String ios(Concept concept) {
        String value;
        if(concept.equals(Dictionary.getConcept("123358AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Zoster";
        }
        else if(concept.equals(Dictionary.getConcept("5334AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Thrush - oral";
        }
        else if(concept.equals(Dictionary.getConcept("298AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Thrush - vaginal";
        }
        else if(concept.equals(Dictionary.getConcept("143264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Cough";
        }
        else if(concept.equals(Dictionary.getConcept("122496AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Difficult breathing";
        }
        else if(concept.equals(Dictionary.getConcept("140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Fever";
        }
        else if(concept.equals(Dictionary.getConcept("487AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Dementia/Enceph";
        }
        else if(concept.equals(Dictionary.getConcept("150796AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Weight loss";
        }
        else if(concept.equals(Dictionary.getConcept("114100AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Pneumonia";
        }
        else if(concept.equals(Dictionary.getConcept("123529AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Urethral discharge";
        }
        else if(concept.equals(Dictionary.getConcept("902AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Pelvic inflammatory disease";
        }
        else if(concept.equals(Dictionary.getConcept("111721AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Ulcers - mouth";
        }
        else if(concept.equals(Dictionary.getConcept("120939AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Ulcers - other";
        }
        else if(concept.equals(Dictionary.getConcept("145762AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Genital ulcer disease";
        }
        else if(concept.equals(Dictionary.getConcept("140707AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Poor weight gain";
        }
        else if(concept.equals(Dictionary.getConcept("112141AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Tuberculosis";
        }
        else if(concept.equals(Dictionary.getConcept("160028AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Immune reconstitution inflammatory syndrome";
        }
        else if(concept.equals(Dictionary.getConcept("162330AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Severe uncomplicated malnutrition";
        }
        else if(concept.equals(Dictionary.getConcept("162331AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "Severe complicated malnutrition";
        }

        else if(concept.equals(Dictionary.getConcept("1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            value = "None";
        }

        else {
            value = concept.getName().getName();
        }
        return value;
    }

    Map<String, List<String>> standardRegimens(){

        Map<String, List<String>> listMap = new HashMap<String, List<String>>();
        listMap.put("AZT+3TC+NVP", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "NEVIRAPINE" ));
        listMap.put("AZT+3TC+EFV", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "EFAVIRENZ" ));
        listMap.put("AZT+3TC+ABC", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "ABACAVIR" ));

        listMap.put("TDF+3TC+NVP", Arrays.asList("TENOFOVIR","LAMIVUDINE", "NEVIRAPINE" ));
        listMap.put("TDF+3TC+EFV", Arrays.asList("TENOFOVIR","LAMIVUDINE", "EFAVIRENZ" ));
        listMap.put("TDF+3TC+ABC", Arrays.asList("TENOFOVIR","LAMIVUDINE", "ABACAVIR" ));
        listMap.put("TDF+3TC+AZT", Arrays.asList("TENOFOVIR","LAMIVUDINE", "ZIDOVUDINE" ));

        listMap.put("d4T+3TC+NVP", Arrays.asList("STAVUDINE","LAMIVUDINE", "NEVIRAPINE" ));
        listMap.put("d4T+3TC+EFV", Arrays.asList("STAVUDINE","LAMIVUDINE", "EFAVIRENZ" ));
        listMap.put("d4T+3TC+ABC", Arrays.asList("STAVUDINE","LAMIVUDINE", "ABACAVIR" ));

        listMap.put("AZT+3TC+LPV/r", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("AZT+3TC+ATV/r", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "ATAZANAVIR", "RITONAVIR" ));
        listMap.put("TDF+3TC+LPV/r", Arrays.asList("TENOFOVIR","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("TDF+ABC+LPV/r", Arrays.asList("TENOFOVIR","ABACAVIR", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("TDF+3TC+ATV/r", Arrays.asList("TENOFOVIR","LAMIVUDINE", "ATAZANAVIR", "RITONAVIR" ));
        listMap.put("ABC+ddI+LPV/r", Arrays.asList("ABACAVIR","DIDANOSINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("d4T+3TC+LPV/r", Arrays.asList("STAVUDINE","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("d4T+ABC+LPV/r", Arrays.asList("STAVUDINE","ABACAVIR", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("AZT+ddI+LPV/r", Arrays.asList("ZIDOVUDINE","DIDANOSINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("TDF+AZT+LPV/r", Arrays.asList("TENOFOVIR","ZIDOVUDINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("AZT+ABC+LPV/r", Arrays.asList("ZIDOVUDINE","ABACAVIR", "LOPINAVIR", "RITONAVIR" ));

        listMap.put("ABC+3TC+NVP", Arrays.asList("ABACAVIR","LAMIVUDINE", "NEVIRAPINE" ));
        listMap.put("ABC+3TC+EFV", Arrays.asList("ABACAVIR","LAMIVUDINE", "EFAVIRENZ" ));
        listMap.put("ABC+3TC+AZT", Arrays.asList("ABACAVIR","LAMIVUDINE", "ZIDOVUDINE" ));

        listMap.put("ABC+3TC+LPV/r", Arrays.asList("ABACAVIR","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("ABC+ddI+LPV/r", Arrays.asList("ABACAVIR","DIDANOSINE", "LOPINAVIR", "RITONAVIR" ));
        listMap.put("AZT+3TC+DRV/r", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "DARUNAVIR", "RITONAVIR" ));
        listMap.put("ABC+3TC+DRV/r", Arrays.asList("ABACAVIR","LAMIVUDINE", "DARUNAVIR", "RITONAVIR" ));

        return listMap;

    }
    String getRegimenName(Map<String, List<String>> standardRegimens, List<String> drugs){
        if (standardRegimens.size() ==0 )
            return null;

        if (drugs.size() == 0)
            return null;
        String regimen = null;

        for (String key : standardRegimens.keySet()){
            List<String> value = standardRegimens.get(key);
            if (value.containsAll(drugs)) {
                regimen = key;
                break;
            }

        }
        return regimen;
    }
    String programs(int value){
       String prog="";
        if(value == 1){
            prog ="TB";
        }

        if(value == 2){
            prog ="HIV";
        }

        if(value == 3){
            prog ="MCH-Child";
        }

        if(value == 4){
            prog ="MCH-Mother";
        }
        return prog;
    }

}
