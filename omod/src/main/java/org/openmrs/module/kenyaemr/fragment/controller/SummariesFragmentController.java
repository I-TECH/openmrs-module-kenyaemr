/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AllCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.*;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientSummary;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientProgramEnrollmentCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    protected static final Log log = LogFactory.getLog(SummariesFragmentController.class);
    private AdministrationService administrationService = Context.getAdministrationService();
    final String isKDoD = (administrationService.getGlobalProperty("kenyaemr.isKDoD"));


    public void controller(@FragmentParam("patient") Patient patient,
                           FragmentModel model){
        PatientWrapper wrapper = new PatientWrapper(patient);

        PatientSummary patientSummary = new PatientSummary();
        PatientService patientService = Context.getPatientService();
        KenyaEmrService kenyaEmrService = Context.getService(KenyaEmrService.class);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);


        Date artStartDate = null;

        String kDoDServiceNumber;
        String kDoDCadre;
        String kDoDRank;
        String kDoDUnit;
        String uniquePatientNumber;
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
        else {
            patientSummary.setMaritalStatus("");
        }

        //date completed tb

        //date confirmed hiv positive
        CalculationResultMap hivConfirmation = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), Arrays.asList(patient.getId()), context);
        Date dateConfirmed = EmrCalculationUtils.datetimeObsResultForPatient(hivConfirmation, patient.getPatientId());
        if(dateConfirmed != null){
            patientSummary.setHivConfrimedDate(formatDate(dateConfirmed));
        }
        else {
            patientSummary.setHivConfrimedDate("");
        }
        // height
        CalculationResultMap latestHeight = Calculations.lastObs(Dictionary.getConcept(Dictionary.HEIGHT_CM), Arrays.asList(patient.getId()), context);
        Obs heightValue = EmrCalculationUtils.obsResultForPatient(latestHeight, patient.getPatientId());
        if(heightValue != null){
            patientSummary.setHeightAtArtStart(heightValue.getValueNumeric().toString());
        }
        else {
            patientSummary.setHeightAtArtStart("");
        }
        // weight
        CalculationResultMap latestWeight = Calculations.lastObs(Dictionary.getConcept(Dictionary.WEIGHT_KG), Arrays.asList(patient.getId()), context);
        Obs weightValue = EmrCalculationUtils.obsResultForPatient(latestWeight, patient.getPatientId());
        if(weightValue != null){
            patientSummary.setWeightAtArtStart(weightValue.getValueNumeric().toString());
        }
        else {
            patientSummary.setWeightAtArtStart("");
        }
        // KDOD Number
        String serviceNumber ="";
        PatientIdentifierType kdodServiceNumber = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.KDoD_SERVICE_NUMBER);
        PatientIdentifier serviceNumberObj = patientService.getPatient(patient.getPatientId()).getPatientIdentifier(kdodServiceNumber);
        if(serviceNumberObj != null){
            serviceNumber = serviceNumberObj.getIdentifier();
        }else {
            patientSummary.setKdodNumber("");

        }
        // KDOD Unit
        String kdodUnit = "";
        PersonAttributeType kdodServiceUnit = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.KDOD_UNIT);
        PersonAttribute kdodUnitObj = patientService.getPatient(patient.getPatientId()).getAttribute(kdodServiceUnit);

        if(kdodUnitObj != null){
            kdodUnit = kdodUnitObj.getValue();
        }else {
            patientSummary.setKdodUnit("");

        }

        // KDOD Cadre
        String kdodCadre = "";
        PersonAttributeType kdodServiceCadre = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.KDOD_CADRE);
        PersonAttribute kdodCadreObj = patientService.getPatient(patient.getPatientId()).getAttribute(kdodServiceCadre);

        if(kdodCadreObj != null){
            kdodCadre = kdodCadreObj.getValue();
        }else {
            patientSummary.setKdodCadre("");

        }

        // KDOD Rank
        String kdodRank = "";
        PersonAttributeType kdodServiceRank = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.KDOD_RANK);
        PersonAttribute kdodRankObj = patientService.getPatient(patient.getPatientId()).getAttribute(kdodServiceRank);

        if(kdodRankObj != null){
            kdodRank = kdodRankObj.getValue();
        }else {
            patientSummary.setKdodCadre("");

        }
        //Oxygen Saturation/

        // weight
        CalculationResultMap latestOxygen = Calculations.lastObs(Dictionary.getConcept(Dictionary.OXYGEN_SATURATION), Arrays.asList(patient.getId()), context);
        Obs latestOxygenValue = EmrCalculationUtils.obsResultForPatient(latestOxygen, patient.getPatientId());
        if(latestOxygenValue != null){
            patientSummary.setOxygenSaturation(latestOxygenValue.getValueNumeric().toString());
        }
        else {
            patientSummary.setOxygenSaturation("");
        }

        //pulse rate
        CalculationResultMap latestPulseRate = Calculations.lastObs(Dictionary.getConcept(Dictionary.PULSE_RATE), Arrays.asList(patient.getId()), context);
        Obs latestPulseRates = EmrCalculationUtils.obsResultForPatient(latestPulseRate, patient.getPatientId());
        if(latestPulseRates != null){
            patientSummary.setPulseRate(latestPulseRates.getValueNumeric().toString());
        }
        else {
            patientSummary.setPulseRate("");
        }
        //Blood Pressure
        CalculationResultMap bloodPressure = Calculations.lastObs(Dictionary.getConcept(Dictionary.BLOOD_PRESSURE), Arrays.asList(patient.getId()), context);
        Obs latestBloodPressure = EmrCalculationUtils.obsResultForPatient(bloodPressure, patient.getPatientId());
        if(latestBloodPressure != null){
            patientSummary.setBloodPressure(latestBloodPressure.getValueNumeric().toString());
        }
        else {
            patientSummary.setBloodPressure("");
        }

        //BP_DIASTOLIC
        CalculationResultMap bpDiastolic = Calculations.lastObs(Dictionary.getConcept(Dictionary.BP_DIASTOLIC), Arrays.asList(patient.getId()), context);
        Obs latestBpDiastolic = EmrCalculationUtils.obsResultForPatient(bpDiastolic, patient.getPatientId());
        if(latestBpDiastolic != null){
            patientSummary.setBpDiastolic(latestBpDiastolic.getValueNumeric().toString());
        }
        else {
            patientSummary.setBpDiastolic("");
        }

        //LMP
        CalculationResultMap latestLmp = Calculations.lastObs(Dictionary.getConcept(Dictionary.LMP), Arrays.asList(patient.getId()), context);
        Obs latestLmpResults = EmrCalculationUtils.obsResultForPatient(latestLmp, patient.getPatientId());
        if(latestLmpResults != null){
            patientSummary.setLmp(formatDate(latestLmpResults.getObsDatetime()));

        }
        else {
            patientSummary.setLmp("");
        }

        //respitatory Rate/

        CalculationResultMap respiratoryRate = Calculations.lastObs(Dictionary.getConcept(Dictionary.RESPIRATORY_RATE), Arrays.asList(patient.getId()), context);
        Obs latestRespiratoryRate = EmrCalculationUtils.obsResultForPatient(respiratoryRate, patient.getPatientId());
        if(latestRespiratoryRate != null){
            patientSummary.setRespiratoryRate(latestRespiratoryRate.getValueNumeric().toString());
        }
        else {
            patientSummary.setRespiratoryRate("");
        }

        ///TB Screening
        CalculationResultMap tbMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.TB_SCREENING), Arrays.asList(patient.getPatientId()), context);
        Obs tbObs = EmrCalculationUtils.obsResultForPatient(tbMap, patient.getPatientId());
        if(tbObs != null) {
            patientSummary.setTbScreeningOutcome(tbScreeningOutcome(tbObs.getValueCoded()));
        }
        else {
            patientSummary.setTbScreeningOutcome("");

        }

        //TB start
        CalculationResultMap tbStartDate = Calculations.firstObs(Dictionary.getConcept(Dictionary.TB_START_DATE), Arrays.asList(patient.getId()), context);
        Obs tbStartDateValue = EmrCalculationUtils.obsResultForPatient(tbStartDate, patient.getPatientId());
        if(tbStartDateValue != null){
            patientSummary.setDateEnrolledInTb(formatDate(tbStartDateValue.getObsDatetime()));

        }
        else {
            patientSummary.setDateEnrolledInTb("None");
        }

        //TB completion
        CalculationResultMap tbEndDate = Calculations.firstObs(Dictionary.getConcept(Dictionary.TB_END_DATE), Arrays.asList(patient.getId()), context);
        Obs tbEndDateValue = EmrCalculationUtils.obsResultForPatient(tbEndDate, patient.getPatientId());
        if(tbEndDateValue != null){
            patientSummary.setDateCompletedInTb(formatDate(tbEndDateValue.getObsDatetime()));

        }
        else {
            patientSummary.setDateCompletedInTb("None");
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
        //CaCx
        CalculationResultMap cacxMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.CACX_SCREENING), Arrays.asList(patient.getPatientId()), context);
        Obs cacxObs = EmrCalculationUtils.obsResultForPatient(cacxMap, patient.getPatientId());
        if(cacxObs != null){
            patientSummary.setCaxcScreeningOutcome(cacxScreeningOutcome(cacxObs.getValueCoded()));
        }
        else {
            patientSummary.setCaxcScreeningOutcome("");
        }

        //STI SCREENING
        CalculationResultMap stiScreen = Calculations.firstObs(Dictionary.getConcept(Dictionary.STI_SCREENING), Arrays.asList(patient.getPatientId()), context);
        Obs stiObs = EmrCalculationUtils.obsResultForPatient(stiScreen, patient.getPatientId());
        if(stiObs != null) {
            patientSummary.setStiScreeningOutcome(stiScreeningOutcome(stiObs.getValueCoded()));
        }
        else {
            patientSummary.setStiScreeningOutcome("");

        }
//
        CalculationResultMap fplanning = Calculations.firstObs(Dictionary.getConcept(Dictionary.FAMILY_PLANNING_METHODS), Arrays.asList(patient.getPatientId()), context);
        Obs fmObs = EmrCalculationUtils.obsResultForPatient(fplanning, patient.getPatientId());
        if(fmObs != null) {
            patientSummary.setFamilyProtection(familyPlanningMethods(fmObs.getValueCoded()));
        }
        else {
            patientSummary.setFamilyProtection("");

        }
        //patient entry point
        CalculationResultMap entryPointMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT), Arrays.asList(patient.getPatientId()), context);
        Obs entryPointObs = EmrCalculationUtils.obsResultForPatient(entryPointMap, patient.getPatientId());
        if(entryPointObs != null) {
            patientSummary.setPatientEntryPoint(entryPointAbbriviations(entryPointObs.getValueCoded()));
            patientSummary.setDateEntryPoint(formatDate(entryPointObs.getObsDatetime()));
        }
        else {
            patientSummary.setPatientEntryPoint("");
            patientSummary.setDateEntryPoint("");
        }
        ///TB Start date
        CalculationResultMap tbConfirmation = Calculations.firstObs(Dictionary.getConcept(Dictionary.TB_START_DATE), Arrays.asList(patient.getPatientId()), context);
        Obs tbDateConfirmed = EmrCalculationUtils.obsResultForPatient(tbConfirmation, patient.getPatientId());
        if(tbDateConfirmed != null) {
            patientSummary.setDateEnrolledInTb(formatDate(tbDateConfirmed.getObsDatetime()));
        }
        else {
            patientSummary.setDateEnrolledInTb("");
        }
        //TB Complete date
        CalculationResultMap tbCompletion = Calculations.lastObs(Dictionary.getConcept(Dictionary.TB_END_DATE), Arrays.asList(patient.getPatientId()), context);
        Obs dateCompletedTB = EmrCalculationUtils.obsResultForPatient(tbCompletion, patient.getPatientId());
        if(dateCompletedTB != null) {
            patientSummary.setDateCompletedInTb(formatDate(dateCompletedTB.getObsDatetime()));
        }
        else {
            patientSummary.setDateCompletedInTb("");
        }

        Set<PersonName> names = patient.getNames();
        StringBuilder stringBuilder = new StringBuilder();
        for(PersonName name: names){
            stringBuilder.append(name);
        }

        //transfer in date
        CalculationResult transferInResults = EmrCalculationUtils.evaluateForPatient(TransferInDateCalculation.class, null, patient);
        String tiDate;
        if(transferInResults.isEmpty()){
            tiDate = "N/A";
        }
        else {
            tiDate = formatDate((Date) transferInResults.getValue());
        }
        //facility transferred form
        CalculationResultMap transferInFacilty = Calculations.lastObs(Dictionary.getConcept("160535AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        Obs faciltyObs = EmrCalculationUtils.obsResultForPatient(transferInFacilty, patient.getPatientId());
        if(faciltyObs != null){
            patientSummary.setTransferInFacility(faciltyObs.getValueText());
        }
        else {
            patientSummary.setTransferInFacility("N/A");
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
        // tbScreening
        CalculationResultMap chronicIllness = Calculations.allObs(Dictionary.getConcept("145439AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        ListResult chronicIllnessResults = (ListResult) chronicIllness.get(patient.getPatientId());
        List<Obs> listOfChronicIllness = CalculationUtils.extractResultValues(chronicIllnessResults);
        String chronicDisease = "";
        if(listOfChronicIllness.size() == 0){
            chronicDisease = "None";
        }
        else if(listOfChronicIllness.size() == 1){
            chronicDisease = listOfChronicIllness.get(0).getValueCoded().getName().getName();
        }
        else{
            for (Obs obs : listOfChronicIllness) {
                if (obs != null) {
                    chronicDisease += obs.getValueCoded().getName().getName()+" ";
                }
            }
        }

        //allergies
        CalculationResultMap alergies = Calculations.allObs(Dictionary.getConcept("160643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        ListResult allergyResults = (ListResult) alergies.get(patient.getPatientId());
        List<Obs> listOfAllergies = CalculationUtils.extractResultValues(allergyResults);
        String allergies = "";
        if(listOfAllergies.size() == 0){
            allergies = "None";
        }
        else if(listOfAllergies.size() == 1){
            allergies = listOfAllergies.get(0).getValueCoded().getName().getName();
        }
        else{
            for (Obs obs : listOfAllergies) {
                if (obs != null) {
                    allergies += obs.getValueCoded().getName().getName()+" ";
                }
            }
        }

        //previous art details
        CalculationResultMap previousArt = Calculations.lastObs(Dictionary.getConcept("160533AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        Obs previousArtObs = EmrCalculationUtils.obsResultForPatient(previousArt,patient.getPatientId());

        if (previousArtObs != null && previousArtObs.getValueCoded() != null &&  previousArtObs.getValueCoded().getConceptId() == 1 &&  previousArtObs.getVoided().equals(false)) {
            patientSummary.setPreviousArt("Yes");
        } else if (previousArtObs != null && previousArtObs.getValueCoded() != null &&  previousArtObs.getValueCoded().getConceptId() == 2 &&  previousArtObs.getVoided().equals(false)) {
            patientSummary.setPreviousArt("No");
        } else {
            patientSummary.setPreviousArt("None");
        }
        //set the purpose for previous art
        CalculationResultMap previousArtPurposePmtct = Calculations.lastObs(Dictionary.getConcept("1148AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap previousArtPurposePep = Calculations.lastObs(Dictionary.getConcept("1691AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap previousArtPurposeHaart = Calculations.lastObs(Dictionary.getConcept("1181AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        Obs previousArtPurposePmtctObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposePmtct, patient.getPatientId());
        Obs previousArtPurposePepObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposePep, patient.getPatientId());
        Obs previousArtPurposeHaartObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposeHaart, patient.getPatientId());
        String purposeString = "";
        if(patientSummary.getPreviousArt().equals("None") || patientSummary.getPreviousArt().equals("No")){
            purposeString ="None";
        }
        if(previousArtPurposePmtctObs != null && previousArtPurposePmtctObs.getValueCoded() != null) {
            purposeString +=previousArtReason(previousArtPurposePmtctObs.getConcept());
        }
        if(previousArtPurposePepObs != null && previousArtPurposePepObs.getValueCoded() != null){
            purposeString += " "+previousArtReason(previousArtPurposePepObs.getConcept());
        }
        if(previousArtPurposeHaartObs != null && previousArtPurposeHaartObs.getValueCoded() != null){
            purposeString +=" "+ previousArtReason(previousArtPurposeHaartObs.getConcept());
        }

        patientSummary.setArtPurpose(purposeString);

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
            patientSummary.setClinicalStageAtArtStart(intergerToRoman(whoStageAtArtStartResults.getValue().toString()));
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
        //height at art initiation
        CalculationResult bmiResults = EmrCalculationUtils.evaluateForPatient(BMICalculation.class, null,patient);
        if(bmiResults != null){
            patientSummary.setBmi(bmiResults.getValue().toString());
        }
        else {
            patientSummary.setBmi("");
        }
        //first regimen for the patient
        CalculationResult firstRegimenResults = EmrCalculationUtils.evaluateForPatient(InitialArtRegimenCalculation.class, null, patient);
        String firstRegimen;
        if(firstRegimenResults == null || firstRegimenResults.isEmpty()){
            firstRegimen = "";
        }
        else {
            firstRegimen = firstRegimenResults.getValue().toString();
        }
        //previous drugs/regimens and dates
        String regimens = "";
        String regimenDates = "";
        CalculationResultMap pmtctRegimenHivEnroll = Calculations.lastObs(Dictionary.getConcept("966AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap pepAndHaartRegimenHivEnroll = Calculations.allObs(Dictionary.getConcept("1088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);

        Obs obsPmtctHivEnroll = EmrCalculationUtils.obsResultForPatient(pmtctRegimenHivEnroll, patient.getPatientId());

        ListResult listResults = (ListResult) pepAndHaartRegimenHivEnroll.get(patient.getPatientId());
        List<Obs> pepAndHaartRegimenObsList = CalculationUtils.extractResultValues(listResults);
        if(patientSummary.getPreviousArt().equals("None") || patientSummary.getPreviousArt().equals("No")){
            regimens = "None";
            regimenDates += "None";
        }
        if(obsPmtctHivEnroll != null){

            regimens = getCorrectDrugCode(obsPmtctHivEnroll.getValueCoded());
            regimenDates = formatDate(obsPmtctHivEnroll.getObsDatetime());
        }

        if(pepAndHaartRegimenObsList != null && !pepAndHaartRegimenObsList.isEmpty() && pepAndHaartRegimenObsList.size() == 1){
            regimens =getCorrectDrugCode(pepAndHaartRegimenObsList.get(0).getValueCoded());
            regimenDates =formatDate(pepAndHaartRegimenObsList.get(0).getObsDatetime());

        }
        else if(pepAndHaartRegimenObsList != null && !pepAndHaartRegimenObsList.isEmpty() && pepAndHaartRegimenObsList.size() > 1){
            for(Obs obs:pepAndHaartRegimenObsList) {
                regimens +=getCorrectDrugCode(obs.getValueCoded())+",";
                regimenDates =formatDate(obs.getObsDatetime());
            }

        }
        patientSummary.setPurposeDrugs(regimens);
        patientSummary.setPurposeDate(regimenDates);

        //past or current oisg
        CalculationResultMap problemsAdded = Calculations.allObs(Dictionary.getConcept(Dictionary.PROBLEM_ADDED), Arrays.asList(patient.getPatientId()), context);
        ListResult problemsAddedList = (ListResult) problemsAdded.get(patient.getPatientId());
        List<Obs> problemsAddedListObs = CalculationUtils.extractResultValues(problemsAddedList);

        Set<Integer> ios = new HashSet<Integer>();
        String iosResults = "";
        List<Integer> iosIntoList = new ArrayList<Integer>();
        for (Obs obs : problemsAddedListObs) {
            ios.add(obs.getValueCoded().getConceptId());
        }
        iosIntoList.addAll(ios);
        if (iosIntoList.size() == 1) {
            iosResults = ios(iosIntoList.get(0));
        } else {
            for (Integer values : iosIntoList) {
                if (values != 1107) {
                    iosResults += ios(values) + " ";
                }
            }
        }

        //current art regimen
        CalculationResult currentRegimenResults = EmrCalculationUtils.evaluateForPatient(CurrentArtRegimenCalculation.class, null, patient);
        if(currentRegimenResults != null) {
            String roCurrent = currentRegimenResults.toString();
            if (roCurrent != null) {
                patientSummary.setCurrentArtRegimen(roCurrent);
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
        CalculationResultMap medOrdersMapCtx = Calculations.allObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap medicationDispensedCtx = Calculations.lastObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), Arrays.asList(patient.getPatientId()), context);

        ListResult medOrdersMapListResults = (ListResult) medOrdersMapCtx.get(patient.getPatientId());
        List<Obs> listOfObsCtx = CalculationUtils.extractResultValues(medOrdersMapListResults);

        Obs medicationDispensedCtxObs = EmrCalculationUtils.obsResultForPatient(medicationDispensedCtx, patient.getPatientId());
        String ctxValue = "";
        if(listOfObsCtx.size() > 0){
            Collections.reverse(listOfObsCtx);
            for(Obs obs:listOfObsCtx){
                if(obs.getValueCoded().equals(Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM))){
                    ctxValue = "Yes";
                    break;
                }
            }
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))){
            ctxValue = "Yes";
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NO))){
            ctxValue = "No";
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))){
            ctxValue = "N/A";
        }
        else {
            ctxValue = "No";
        }
        //Find if a patient is on dapsone
        CalculationResultMap medOrdersMapDapsone = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), Arrays.asList(patient.getPatientId()), context);
        Obs medOrdersMapObsDapsone = EmrCalculationUtils.obsResultForPatient(medOrdersMapDapsone, patient.getPatientId());
        if(medOrdersMapObsDapsone != null && medOrdersMapObsDapsone.getValueCoded().equals(Dictionary.getConcept(Dictionary.DAPSONE))){
            patientSummary.setDapsone("Yes");
        }
        else if(medOrdersMapObsDapsone != null && medOrdersMapObsDapsone.getValueCoded().equals(Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM)) || medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))){
            patientSummary.setDapsone("No");
        }
        else {
            patientSummary.setDapsone("No");
        }
        //on IPT
        CalculationResultMap medOrdersMapInh = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), Arrays.asList(patient.getPatientId()), context);
        Obs medOrdersMapObsInh = EmrCalculationUtils.obsResultForPatient(medOrdersMapInh, patient.getPatientId());
        CalculationResultMap medicationDispensedIpt = Calculations.lastObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), Arrays.asList(patient.getPatientId()), context);
        Obs medicationDispensedIptObs = EmrCalculationUtils.obsResultForPatient(medicationDispensedIpt, patient.getPatientId());
        if(medOrdersMapObsInh != null && medOrdersMapObsInh.getValueCoded().equals(Dictionary.getConcept(Dictionary.ISONIAZID))){
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
            patientSummary.setOnIpt("No");
        }

        //find clinics enrolled
        CalculationResult clinicsEnrolledResult = EmrCalculationUtils.evaluateForPatient(PatientProgramEnrollmentCalculation.class, null, patient);
        Set<String> patientProgramList= new HashSet<String>();
        List<String> setToList = new ArrayList<String>();
        if(clinicsEnrolledResult != null){
            List<PatientProgram> patientPrograms = (List<PatientProgram>) clinicsEnrolledResult.getValue();
            for(PatientProgram p: patientPrograms) {

                patientProgramList.add(programs(p.getProgram().getConcept().getConceptId()));
            }
        }
        setToList.addAll(patientProgramList);
        String clinicValues = "";
        if(setToList.size() == 1){
            clinicValues = setToList.get(0);
        }
        else {
            for(String val:setToList) {
                clinicValues += val+",";
            }
        }
        //most recent cd4
        CalculationResult cd4Results = EmrCalculationUtils.evaluateForPatient(LastCd4CountDateCalculation.class, null, patient);
        if(cd4Results != null && cd4Results.getValue() != null){
            patientSummary.setMostRecentCd4(((Obs) cd4Results.getValue()).getValueNumeric().toString());
            patientSummary.setMostRecentCd4Date(formatDate(((Obs) cd4Results.getValue()).getObsDatetime()));
        }
        else{
            patientSummary.setMostRecentCd4("");
            patientSummary.setMostRecentCd4Date("");
        }

        //All CD4 Count
        CalculationResult allCd4CountResults = EmrCalculationUtils.evaluateForPatient(AllCd4CountCalculation.class, null, patient);

        //most recent viral load
        CalculationResult vlResults = EmrCalculationUtils.evaluateForPatient(ViralLoadAndLdlCalculation.class, null, patient);
        String viralLoadValue = "None";
        String viralLoadDate = "None";
        if(!vlResults.isEmpty()) {
            String values = vlResults.getValue().toString();
            //split by brace
            String value = values.replaceAll("\\{", "").replaceAll("\\}","");
            if(!value.isEmpty()) {
                String[] splitByEqualSign = value.split("=");
                viralLoadValue = splitByEqualSign[0];

                //for a date from a string
                String dateSplitedBySpace = splitByEqualSign[1].split(" ")[0].trim();
                String yearPart = dateSplitedBySpace.split("-")[0].trim();
                String monthPart = dateSplitedBySpace.split("-")[1].trim();
                String dayPart = dateSplitedBySpace.split("-")[2].trim();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(yearPart));
                calendar.set(Calendar.MONTH, Integer.parseInt(monthPart) - 1);
                calendar.set(Calendar.DATE, Integer.parseInt(dayPart));

                viralLoadDate = formatDate(calendar.getTime());
            }
        }
        if(isKDoD.equals("true")){

            kDoDServiceNumber = wrapper.getKDoDServiceNumber();
            kDoDCadre = wrapper.getCadre();
            kDoDRank = wrapper.getRank();
            kDoDUnit = wrapper.getKDoDUnit();
        }
        else{
            uniquePatientNumber = wrapper.getUniquePatientNumber();
        }
        // find deceased date
        CalculationResult deadResults = EmrCalculationUtils.evaluateForPatient(DateOfDeathCalculation.class, null, patient);
        String dead;
        if(deadResults.isEmpty()){
            dead = "N/A";
        }
        else {
            dead = formatDate((Date) deadResults.getValue());
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
        String toDate;
        if(totResults.isEmpty()){
            toDate = "N/A";
        }
        else {
            toDate = formatDate((Date) totResults.getValue());
        }
        //transfer out to facility
        String toFacility;
        CalculationResultMap transferOutFacilty = Calculations.lastObs(Dictionary.getConcept("159495AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), Arrays.asList(patient.getPatientId()), context);
        Obs transferOutFacilityObs = EmrCalculationUtils.obsResultForPatient(transferOutFacilty, patient.getPatientId());
        if(transferOutFacilityObs != null){
            toFacility = transferOutFacilityObs.getValueText();
        }
        else {
            toFacility = "N/A";
        }
        model.addAttribute("isKDoD", isKDoD);
        model.addAttribute("patient", patientSummary);
        model.addAttribute("names", stringBuilder);
        model.addAttribute("currentRegimen", patientSummary.getCurrentArtRegimen());
        model.addAttribute("onCtx", ctxValue);
        model.addAttribute("onDapsone", patientSummary.getDapsone());
        model.addAttribute("onIpt", patientSummary.getOnIpt());
        model.addAttribute("programs", patientSummary.getClinicsEnrolled());
        model.addAttribute("recentCd4Count", patientSummary.getMostRecentCd4());
        model.addAttribute("recentCd4CountDate", patientSummary.getMostRecentCd4Date());
        model.addAttribute("height", patientSummary.getHeightAtArtStart());
        model.addAttribute("weight", patientSummary.getWeightAtArtStart());
        model.addAttribute("oxygenSaturation", patientSummary.getOxygenSaturation());
        model.addAttribute("pulseRate", patientSummary.getPulseRate());

        model.addAttribute("allVlResults", patientSummary.getVlResults());
        model.addAttribute("allVlDates", patientSummary.getVlDates());
        model.addAttribute("allCd4Results", patientSummary.getCd4Results());
        model.addAttribute("allCdDates", patientSummary.getCd4Dates());
        model.addAttribute("respiratoryRate", patientSummary.getRespiratoryRate());
        model.addAttribute("bloodPressure", patientSummary.getBloodPressure());
        model.addAttribute("setBpDiastolic", patientSummary.getBpDiastolic());
        model.addAttribute("tbStartDate", patientSummary.getDateEnrolledInTb());
        model.addAttribute("tbEndDate", patientSummary.getDateCompletedInTb());
        model.addAttribute("lmps", patientSummary.getLmp());
        model.addAttribute("recentVl", viralLoadValue);
        model.addAttribute("recentVlDate", viralLoadDate);
        model.addAttribute("deadDeath", dead);
        model.addAttribute("returnVisitDate", patientSummary.getNextAppointmentDate());
        model.addAttribute("toDate", toDate);
        model.addAttribute("toFacility", toFacility);
        model.addAttribute("tiDate", tiDate);
        model.addAttribute("allergies", allergies);
        model.addAttribute("serviceNumber", serviceNumber);
        model.addAttribute("kdodUnit", kdodUnit);
        model.addAttribute("kdodCadre", kdodCadre);
        model.addAttribute("kdodRank", kdodRank);
        model.addAttribute("allCd4CountResults", allCd4CountResults.getValue());



        model.addAttribute("iosResults", iosResults);
        model.addAttribute("clinicValues", clinicValues);
        model.addAttribute("firstRegimen", firstRegimen);
        model.addAttribute("chronicDisease", chronicDisease);


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

    String familyPlanningMethods(Concept concept) {
        String value = "Other";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept("5279AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "INJECTABLE HORMONES";
            } else if (concept.equals(Dictionary.getConcept("5278AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "CERVICAL CAP";
            } else if (concept.equals(Dictionary.getConcept("5275AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Intrauterine device";
            } else if (concept.equals(Dictionary.getConcept("5276AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Female sterilization";
            } else if (concept.equals(Dictionary.getConcept("190AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Condoms";
            } else if (concept.equals(Dictionary.getConcept("780AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Birth control pills";
            } else if (concept.equals(Dictionary.getConcept("5277AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Natural family planning";
            } else if (concept.equals(Dictionary.getConcept("159524AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Sexual abstinence";
            } else if (concept.equals(Dictionary.getConcept("78796AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "LEVONORGESTREL";
            } else if (concept.equals(Dictionary.getConcept("1175AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Not applicable";
            } else if (concept.equals(Dictionary.getConcept("1472AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Tubal ligation procedure";
            } else if (concept.equals(Dictionary.getConcept("907AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "MEDROXYPROGESTERONE ACETATE";
            } else if (concept.equals(Dictionary.getConcept("1489AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Vasectomy";
            } else if (concept.equals(Dictionary.getConcept("1359AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "NORPLANT (IMPLANTABLE CONTRACEPTIVE)";

            } else if (concept.equals(Dictionary.getConcept("1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "None";
            }
            else if (concept.equals(Dictionary.getConcept("136452AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "IUD Contraception";
            }
            else if (concept.equals(Dictionary.getConcept("159837AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Hysterectomy";
            }
            else if (concept.equals(Dictionary.getConcept("160570AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Emergency contraceptive pills";
            }
            else if (concept.equals(Dictionary.getConcept("136163AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Lactational amenorrhea";
            }
            else if (concept.equals(Dictionary.getConcept("1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Unknown";
            }
            else if (concept.equals(Dictionary.getConcept("162332AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Contraception method undecided";
            }
            else if (concept.equals(Dictionary.getConcept("164814AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Female condom";
            }
            else if (concept.equals(Dictionary.getConcept("164813AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Male condom";
            }
            else if (concept.equals(Dictionary.getConcept("159589AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Implantable contraceptive (unspecified type)";
            }

        }

        return value;
    }

    String tbScreeningOutcome(Concept concept) {
        String value = "N/A";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept("703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "POSITIVE";
            } else if (concept.equals(Dictionary.getConcept("664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "NEGATIVE";
            }
        }
        return value;
    }

    String stiScreeningOutcome(Concept concept) {
        String value = "N/A";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept("703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "POSITIVE";
            } else if (concept.equals(Dictionary.getConcept("664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "NEGATIVE";
            }
        }

        return value;
    }

    String cacxScreeningOutcome(Concept concept) {
        String value = "N/A";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "YES";
            }
            else if (concept.equals(Dictionary.getConcept("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "No";
            }
            else if (concept.equals(Dictionary.getConcept("1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "Unknown";
            }
        }
        return value;
    }

    String entryPointAbbriviations(Concept concept) {
        String value = "Other";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept(Dictionary.VCT_PROGRAM))) {
                value = "VCT";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.PMTCT_PROGRAM))) {
                value = "PMTCT";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.PEDIATRIC_INPATIENT_SERVICE))) {
                value = "IPD-P";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.ADULT_INPATIENT_SERVICE))) {
                value = "IPD-A";
            } else if (concept.equals(Dictionary.getConcept("160542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "OPD";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM))) {
                value = "TB";
            } else if (concept.equals(Dictionary.getConcept("160543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "CBO";
            } else if (concept.equals(Dictionary.getConcept("160543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "CBO";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.UNDER_FIVE_CLINIC))) {
                value = "UNDER FIVE";
            } else if (concept.equals(Dictionary.getConcept("160546AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "STI";
            } else if (concept.equals(Dictionary.getConcept("160548AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "IDU";
            } else if (concept.equals(Dictionary.getConcept("160548AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "IDU";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM))) {
                value = "MCH";
            } else if (concept.equals(Dictionary.getConcept("162223AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
                value = "VMMC";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.TRANSFER_IN))) {
                value = "TI";
            }
        }

        return value;
    }

    String whoStaging(Concept concept){
        String stage = "";
        if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_PEDS))){

            stage = "I";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_PEDS))){

            stage = "II";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS))){

            stage = "III";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT)) || concept.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_PEDS))){

            stage = "IV";
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
    String ios(Integer concept) {
        String value ;
        if(concept.equals(123358)){
            value = "Zoster";
        }
        else if(concept.equals(5334)){
            value = "Thrush - oral";
        }
        else if(concept.equals(298)){
            value = "Thrush - vaginal";
        }
        else if(concept.equals(143264)){
            value = "Cough";
        }
        else if(concept.equals(122496)){
            value = "Difficult breathing";
        }
        else if(concept.equals(140238)){
            value = "Fever";
        }
        else if(concept.equals(487)){
            value = "Dementia/Enceph";
        }
        else if(concept.equals(150796)){
            value = "Weight loss";
        }
        else if(concept.equals(114100)){
            value = "Pneumonia";
        }
        else if(concept.equals(123529)){
            value = "Urethral discharge";
        }
        else if(concept.equals(902)){
            value = "Pelvic inflammatory disease";
        }
        else if(concept.equals(111721)){
            value = "Ulcers - mouth";
        }
        else if(concept.equals(120939)){
            value = "Ulcers - other";
        }
        else if(concept.equals(145762)){
            value = "Genital ulcer disease";
        }
        else if(concept.equals(140707)){
            value = "Poor weight gain";
        }
        else if(concept.equals(112141)){
            value = "Tuberculosis";
        }
        else if(concept.equals(160028)){
            value = "Immune reconstitution inflammatory syndrome";
        }
        else if(concept.equals(162330)){
            value = "Severe uncomplicated malnutrition";
        }
        else if(concept.equals(162331)){
            value = "Severe complicated malnutrition";
        }

        else if(concept.equals(1107)){
            value = "None";
        }

        else {
            value = Context.getConceptService().getConcept(concept).getName().getName();
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
        if(value == 160541){
            prog ="TB";
        }

        if(value == 160631){
            prog ="HIV";
        }

        if(value == 159937){
            prog ="MCH";
        }

        return prog;
    }

    String getCorrectDrugCode(Concept concept){
        String defaultString = "";
        if(concept.equals(Dictionary.getConcept("794AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "LPV/r";
        }
        else if(concept.equals(Dictionary.getConcept("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "d4T";
        }
        else if(concept.equals(Dictionary.getConcept("74807AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "DDI";
        }
        else if(concept.equals(Dictionary.getConcept("70056AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "ABC";
        }
        else if(concept.equals(Dictionary.getConcept("80487AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "NFV";
        }
        else if(concept.equals(Dictionary.getConcept("80586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "NVP";
        }
        else if(concept.equals(Dictionary.getConcept("75523AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "EFV";
        }
        else if(concept.equals(Dictionary.getConcept("78643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "3TC";
        }
        else if(concept.equals(Dictionary.getConcept("84795AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "TDF";
        }
        else if(concept.equals(Dictionary.getConcept("86663AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "AZT";
        }
        else if(concept.equals(Dictionary.getConcept("83412AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "RTV";
        }
        else if(concept.equals(Dictionary.getConcept("71647AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))){
            defaultString = "ATV";
        }
        else {
            defaultString = concept.getName().getName();
        }

        return defaultString;
    }
    String intergerToRoman(String integer){
        String value = "";
        if(integer.equals("1")){
            value = "I";
        }
        else if(integer.equals("2")){
            value = "II";
        }
        else if(integer.equals("3")){
            value = "III";
        }
        else if(integer.equals("4")){
            value = "IV";
        }
        return value;
    }

}
