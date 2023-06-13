/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.PatientProgram;
import org.openmrs.Order;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.api.AdministrationService;
import org.openmrs.EncounterType;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.calculation.CalculationManager;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.util.ZScoreUtil;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbDiseaseClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbPatientClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbTreatmentNumberCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientInTbProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4CountDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.HIVEnrollment;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralLoadAndLdlCalculation;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemrorderentry.util.Utils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
/**
 * The rest controller for exposing resources through kenyacore and kenyaemr modules
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/kenyaemr")
public class KenyaemrCoreRestController extends BaseRestController {
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Gets a list of available/completed forms for a patient
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/forms") // gets all visit forms for a patient
    @ResponseBody
    public Object getAllAvailableFormsForVisit(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid) {
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
        ArrayNode formList = JsonNodeFactory.instance.arrayNode();
        ObjectNode allFormsObj = JsonNodeFactory.instance.objectNode();

        if (!activeVisits.isEmpty()) {
            Visit patientVisit = activeVisits.get(0);

                /**
                 *  {uuid: string;
                 *   encounterType?: EncounterType;
                 *   name: string;
                 *   display: string;
                 *   version: string;
                 *   published: boolean;
                 *   retired: boolean;}
                 */

            FormManager formManager = CoreContext.getInstance().getManager(FormManager.class);
            List<FormDescriptor> uncompletedFormDescriptors = formManager.getAllUncompletedFormsForVisit(patientVisit);

            if (!uncompletedFormDescriptors.isEmpty()) {

                for (FormDescriptor descriptor : uncompletedFormDescriptors) {
                    if(!descriptor.getTarget().getRetired()) {
                        ObjectNode formObj = generateFormDescriptorPayload(descriptor);
                        formObj.put("formCategory", "available");
                        formList.add(formObj);
                    }
                }
            }

            List<FormDescriptor> completedFormDescriptors = formManager.getCompletedFormsForVisit(patientVisit);

            if (!completedFormDescriptors.isEmpty()) {

                for (FormDescriptor descriptor : completedFormDescriptors) {
                    ObjectNode formObj = generateFormDescriptorPayload(descriptor);
                    formObj.put("formCategory", "completed");
                    formList.add(formObj);
                }
            }
        }

        allFormsObj.put("results", formList);

        return allFormsObj.toString();
    }

    /**
     * Gets a list of flags for a patient
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/flags") // gets all flags for a patient
    @ResponseBody
    public Object getAllPatientFlags(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid, @SpringBean CalculationManager calculationManager) {
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        ObjectNode flagsObj = JsonNodeFactory.instance.objectNode();

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        calculationManager.refresh();
        ArrayNode flags = JsonNodeFactory.instance.arrayNode();
        for (PatientFlagCalculation calc : calculationManager.getFlagCalculations()) {

			try {
				CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(patient.getId(), calc);
				if (result != null && (Boolean) result.getValue()) {
                    flags.add(calc.getFlagMessage());
				}
			}
			catch (Exception ex) {
				log.error("Error evaluating " + calc.getClass(), ex);
                return new ResponseEntity<Object>("ERROR EVALUATING!"+ calc.getFlagMessage(),
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
			}
		}
        flagsObj.put("results", flags);

		return flagsObj.toString();
        
    }

     /**
     * Returns custom patient object
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/patient") 
    @ResponseBody
    public Object getPatientIdByPatientUuid(@RequestParam("patientUuid") String patientUuid) {
        ObjectNode patientNode = JsonNodeFactory.instance.objectNode();
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        ObjectNode patientObj = JsonNodeFactory.instance.objectNode();

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        patientNode.put("patientId", patient.getPatientId());
        patientNode.put("name", patient.getPerson().getPersonName().getFullName());
        patientNode.put("age", patient.getAge());

        patientObj.put("results", patientNode);

		return patientObj.toString();
        
    }

	/**
	 * Fetches default facility
	 *
	 * @return custom location object
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/default-facility")
	@ResponseBody
	public Object getDefaultConfiguredFacility() {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(EmrConstants.GP_DEFAULT_LOCATION);

		if (gp == null) {
			return new ResponseEntity<Object>("Default facility not configured!", new HttpHeaders(), HttpStatus.NOT_FOUND);
		}

		Location location = (Location) gp.getValue();
		ObjectNode locationNode = JsonNodeFactory.instance.objectNode();

		locationNode.put("locationId", location.getLocationId());
		locationNode.put("uuid", location.getUuid());
		locationNode.put("display", location.getName());

		return locationNode.toString();

	}

    /**
     * Get a list of programs a patient is eligible for
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/eligiblePrograms") // gets all programs a patient is eligible for
    @ResponseBody
    public Object getEligiblePrograms(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid) {
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        ProgramManager programManager = CoreContext.getInstance().getManager(ProgramManager.class);
        ArrayNode programList = JsonNodeFactory.instance.arrayNode();

        if (!patient.isVoided()) {
            Collection<ProgramDescriptor> activePrograms = programManager.getPatientActivePrograms(patient);
            Collection<ProgramDescriptor> eligiblePrograms = programManager.getPatientEligiblePrograms(patient);

            /**
             * ProgramEndPoint {
             *   uuid: string;
             *   display: string;
             *   enrollmentFormUuid: string;
             *   discontinuationFormUuid: string;
             *   enrollmentStatus: string;
             * }
             */
            for (ProgramDescriptor descriptor : eligiblePrograms) {
                    ObjectNode programObj = JsonNodeFactory.instance.objectNode();
                    programObj.put("uuid", descriptor.getTargetUuid());
                    programObj.put("display", descriptor.getTarget().getName());
                    programObj.put("enrollmentFormUuid", descriptor.getDefaultEnrollmentForm().getTargetUuid());
                    programObj.put("discontinuationFormUuid", descriptor.getDefaultCompletionForm().getTargetUuid());
                    programObj.put("enrollmentStatus", activePrograms.contains(descriptor) ? "active" : "eligible");
                    programList.add(programObj);
            }
        }

        return programList.toString();
    }


    /**
     * Calculate z-score based on a client's sex, weight, and height
     * @param sex
     * @param weight
     * @param height
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/zscore")
    @ResponseBody
    public Object calculateZScore(@RequestParam("sex") String sex, @RequestParam("weight") Double weight, @RequestParam("height") Double height) {
        ObjectNode resultNode = JsonNodeFactory.instance.objectNode();
        Integer result =  ZScoreUtil.calculateZScore(height, weight, sex);

        if (result < -4) { // this is an indication of an error. We can break it down further for appropriate messages
            return new ResponseEntity<Object>("Could not compute the zscore for the patient!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        resultNode.put("wfl_score", result);
        return resultNode.toString();

    }

    /**
     * Fetches Patient's hiv care panel data
     *
     * @return custom hiv data
     */
    @RequestMapping(method = RequestMethod.GET, value = "/currentProgramDetails")
    @ResponseBody
    public Object getPatientHivCarePanel(@RequestParam("patientUuid") String patientUuid) {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        Map<String, SimpleObject> carePanelObj = new HashMap<String, SimpleObject>();
        SimpleObject mchMotherResponseObj = new SimpleObject();
        SimpleObject mchChildResponseObj = new SimpleObject();
        SimpleObject hivResponseObj = new SimpleObject();
        SimpleObject tbResponseObj = new SimpleObject();
        SimpleObject firstEncDetails = null;
        CalculationResult enrolledInHiv = EmrCalculationUtils.evaluateForPatient(HIVEnrollment.class, null, patient);

        if((Boolean) enrolledInHiv.getValue() == false) {
            CalculationResult lastWhoStage = EmrCalculationUtils.evaluateForPatient(LastWhoStageCalculation.class, null, patient);
            if(lastWhoStage != null && lastWhoStage.getValue() != null) {
                hivResponseObj.put("whoStage", EmrUtils.whoStage(((Obs) lastWhoStage.getValue()).getValueCoded()));
                hivResponseObj.put("whoStageDate", formatDate(((Obs) lastWhoStage.getValue()).getObsDatetime()));
            } else {
                hivResponseObj.put("whoStage", "");
                hivResponseObj.put("whoStageDate", "");
            }
            CalculationResult lastCd4 = EmrCalculationUtils.evaluateForPatient(LastCd4CountDateCalculation.class, null, patient);
            if(lastCd4 != null  && lastCd4.getValue() != null) {
                hivResponseObj.put("cd4", ((Obs) lastCd4.getValue()).getValueNumeric().toString());
                hivResponseObj.put("cd4Date", formatDate(((Obs) lastCd4.getValue()).getObsDatetime()));
            } else {
                hivResponseObj.put("cd4", "None");
                hivResponseObj.put("cd4Date", "");
            }
            CalculationResult lastCd4Percent = EmrCalculationUtils.evaluateForPatient(LastCd4PercentageCalculation.class, null, patient);
            if(lastCd4Percent != null && lastCd4Percent.getValue() != null) {
                hivResponseObj.put("cd4Percent", ((Obs) lastCd4Percent.getValue()).getValueNumeric().toString());
                hivResponseObj.put("cd4PercentDate", formatDate(((Obs) lastCd4Percent.getValue()).getObsDatetime()));
            } else {
                hivResponseObj.put("cd4Percent", "None");
                hivResponseObj.put("cd4PercentDate", "");
            }

            CalculationResult lastViralLoad = EmrCalculationUtils.evaluateForPatient(ViralLoadAndLdlCalculation.class, null, patient);
            String valuesRequired = "None";
            Date datesRequired = null;
            if(!lastViralLoad.isEmpty()){
                String values = lastViralLoad.getValue().toString();
                //split by brace
                String value = values.replaceAll("\\{", "").replaceAll("\\}","");
                //split by equal sign
                if(!value.isEmpty()) {
                    String[] splitByEqualSign = value.split("=");
                    valuesRequired = splitByEqualSign[0];
                    //for a date from a string
                    String dateSplitedBySpace = splitByEqualSign[1].split(" ")[0].trim();
                    String yearPart = dateSplitedBySpace.split("-")[0].trim();
                    String monthPart = dateSplitedBySpace.split("-")[1].trim();
                    String dayPart = dateSplitedBySpace.split("-")[2].trim();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.parseInt(yearPart));
                    calendar.set(Calendar.MONTH, Integer.parseInt(monthPart) - 1);
                    calendar.set(Calendar.DATE, Integer.parseInt(dayPart));

                    datesRequired = calendar.getTime();
                }
            }
            // get default LDL value
            AdministrationService as = Context.getAdministrationService();
            hivResponseObj.put("ldlValue", valuesRequired);
            hivResponseObj.put("ldlDate", formatDate(datesRequired));
            hivResponseObj.put("enrolledInHiv", (Boolean) enrolledInHiv.getValue());

            Encounter lastEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");
            SimpleObject lastEncDetails = null;
            if (lastEnc != null) {
                lastEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastEnc.getObs(), lastEnc);
            }
            hivResponseObj.put("lastEncDetails", lastEncDetails);
            carePanelObj.put("HIV", hivResponseObj);

        }

        // tb details
        CalculationResult patientEnrolledInTbProgram = EmrCalculationUtils.evaluateForPatient(PatientInTbProgramCalculation.class, null,patient);
        if((Boolean) patientEnrolledInTbProgram.getValue() == true) {
            CalculationResult tbDiseaseClassification = EmrCalculationUtils.evaluateForPatient(TbDiseaseClassificationCalculation.class, null, patient);
            if(tbDiseaseClassification != null && tbDiseaseClassification.getValue() != null) {
                tbResponseObj.put("tbDiseaseClassification", ((Obs) tbDiseaseClassification.getValue()).getValueCoded().getName().getName());
                tbResponseObj.put("tbDiseaseClassificationDate", formatDate(((Obs) tbDiseaseClassification.getValue()).getObsDatetime()));
            } else {
                tbResponseObj.put("tbDiseaseClassification", "None");
                tbResponseObj.put("tbDiseaseClassificationDate", "");
            }

            CalculationResult tbPatientClassification = EmrCalculationUtils.evaluateForPatient(TbPatientClassificationCalculation.class, null, patient);
            if(tbPatientClassification != null){
                Obs obs  = (Obs) tbPatientClassification.getValue();
                if(obs.getValueCoded().equals(Dictionary.getConcept(Dictionary.SMEAR_POSITIVE_NEW_TUBERCULOSIS_PATIENT))) {
                    tbResponseObj.put("tbPatientClassification", "New tuberculosis patient");
                }
                else {
                    tbResponseObj.put("tbPatientClassification", obs.getValueCoded().getName().getName());
                }
            }

            CalculationResult tbTreatmentNo = EmrCalculationUtils.evaluateForPatient(TbTreatmentNumberCalculation.class, null, patient);
            if(tbTreatmentNo != null && tbTreatmentNo.getValue() != null) {
                tbResponseObj.put("tbTreatmentNumber", ((Obs) tbTreatmentNo.getValue()));
            } else {
                tbResponseObj.put("tbTreatmentNumber", "None");
            }
            Encounter lastTBEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "TB");
            SimpleObject lastTBEncDetails = null;
            if (lastTBEnc != null) {
                lastTBEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastTBEnc.getObs(), lastTBEnc);
            }
            tbResponseObj.put("lastTbEncounter", lastTBEncDetails);
            carePanelObj.put("TB", tbResponseObj);
        }

        //mch mother details
        PatientCalculationContext context = Context.getService(PatientCalculationService.class).createCalculationContext();
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        PatientWrapper patientWrapper = new PatientWrapper(patient);

        Encounter lastMchEnrollment = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT));
        Encounter lastMchFollowup = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION));

        if(lastMchEnrollment != null ) {
            EncounterWrapper lastMchEnrollmentWrapped = null;
            EncounterWrapper lastMchFollowUpWrapped = null;
            //Check whether already in hiv program
            CalculationResultMap enrolled = Calculations.firstEnrollments(hivProgram, Arrays.asList(patient.getPatientId()), context);
            PatientProgram program = EmrCalculationUtils.resultForPatient(enrolled, patient.getPatientId());

            if (lastMchEnrollment != null) {
                lastMchEnrollmentWrapped = new EncounterWrapper(lastMchEnrollment);
            }
            if (lastMchFollowup != null) {
                lastMchFollowUpWrapped = new EncounterWrapper(lastMchFollowup);
            }

            Obs hivEnrollmentStatusObs = null;
            Obs hivFollowUpStatusObs = null;

            if (lastMchEnrollmentWrapped != null) {
                hivEnrollmentStatusObs = lastMchEnrollmentWrapped.firstObs(Dictionary.getConcept(Dictionary.HIV_STATUS));
            }
            if (lastMchFollowUpWrapped != null) {
                hivFollowUpStatusObs = lastMchFollowUpWrapped.firstObs(Dictionary.getConcept(Dictionary.HIV_STATUS));
            }
            //Check if already enrolled in HIV, add regimen
            if(program != null) {
                String regimenName = null;
                String regimenStartDate = null;
                Encounter lastDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");   //last DRUG_REGIMEN_EDITOR encounter
                if (lastDrugRegimenEditorEncounter != null) {
                    SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastDrugRegimenEditorEncounter.getAllObs(), lastDrugRegimenEditorEncounter);
                    regimenName = o.get("regimenShortDisplay").toString();
                    regimenStartDate = o.get("startDate").toString();
                    if (regimenName != null) {
                        mchMotherResponseObj.put("hivStatus", "Positive");
                        mchMotherResponseObj.put("hivStatusDate", regimenStartDate);
                        mchMotherResponseObj.put("onHaart", "Yes (" + regimenName + ")");
                        mchMotherResponseObj.put("onHaartDate", regimenStartDate);
                    } else {
                        mchMotherResponseObj.put("hivStatus", "Positive");
                        mchMotherResponseObj.put("hivStatusDate", regimenStartDate);
                        mchMotherResponseObj.put("onHaart", "Not specified");
                        mchMotherResponseObj.put("onHaartDate", regimenStartDate);
                    }
                }
                //Check mch enrollment and followup forms
            } else if(hivEnrollmentStatusObs != null || hivFollowUpStatusObs != null) {
                String regimenName = null;
                if(hivFollowUpStatusObs != null){
                    mchMotherResponseObj.put("hivStatus", hivFollowUpStatusObs.getValueCoded().getName().getName());
                    mchMotherResponseObj.put("hivStatusDate", hivFollowUpStatusObs.getValueDatetime());
                }else {
                    mchMotherResponseObj.put("hivStatus", hivEnrollmentStatusObs.getValueCoded().getName().getName());
                    mchMotherResponseObj.put("hivStatusDate", hivEnrollmentStatusObs.getValueDatetime());
                }
                Encounter lastDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");   //last DRUG_REGIMEN_EDITOR encounter
                mchMotherResponseObj.put("onHaart", hivEnrollmentStatusObs.getValueDatetime());
                if (lastDrugRegimenEditorEncounter != null) {
                    SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastDrugRegimenEditorEncounter.getAllObs(), lastDrugRegimenEditorEncounter);
                    regimenName = o.get("regimenShortDisplay").toString();
                    if (regimenName != null) {
                        if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("positive")) {
                            mchMotherResponseObj.put("onHaart", "Yes (" + regimenName + ")");
                        } else {
                            mchMotherResponseObj.put("onHaart", "Not specified");
                        }
                    } else {
                        mchMotherResponseObj.put("onHaart", "Not specified");
                    }

                } else {
                    if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("negative")) {
                        mchMotherResponseObj.put("onHaart", "Not applicable");
                    }
                    if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("unknown")) {
                        mchMotherResponseObj.put("onHaart", "Not applicable");
                    }
                    if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("positive")) {
                        mchMotherResponseObj.put("onHaart", "Not specified");
                    }
                }
            }
            carePanelObj.put("mchMother", mchMotherResponseObj);

        }

        //mch child details
        Encounter lastHeiEnrollmentEncounter = Utils.lastEncounter(patient, Context.getEncounterService().getEncounterTypeByUuid(MchMetadata._EncounterType.MCHCS_ENROLLMENT));

        if(lastHeiEnrollmentEncounter != null) {
            List<Obs> milestones = new ArrayList<Obs>();
            String prophylaxis;
            String feeding;
            List<Obs> remarks = new ArrayList<Obs>();
            String heiOutcomes;
            Integer prophylaxisQuestion = 1282;
            Integer feedingMethodQuestion = 1151;
            Integer heiOutcomesQuestion = 159427;

            EncounterType mchcs_consultation_encounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_CONSULTATION);
            Encounter lastMchcsConsultation = patientWrapper.lastEncounter(mchcs_consultation_encounterType);

            Concept pcrInitialTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE);
            Concept rapidTest = Dictionary.getConcept(Dictionary.RAPID_HIV_CONFIRMATORY_TEST);
            CalculationResultMap pcrObs = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), Arrays.asList(patient.getPatientId()), context);
            CalculationResultMap rapidTestObs = Calculations.allObs(Dictionary.getConcept(Dictionary.RAPID_HIV_CONFIRMATORY_TEST), Arrays.asList(patient.getPatientId()), context);
            Encounter lastHeiCWCFollowupEncounter = Utils.lastEncounter(patient, Context.getEncounterService().getEncounterTypeByUuid(MchMetadata._EncounterType.MCHCS_CONSULTATION));
            Encounter lastHeiOutComeEncounter = Utils.lastEncounter(patient, Context.getEncounterService().getEncounterTypeByUuid(MchMetadata._EncounterType.MCHCS_HEI_COMPLETION));

            if(lastHeiOutComeEncounter !=null){
                for (Obs obs : lastHeiOutComeEncounter.getAllObs() ){
                    if (obs.getConcept().getConceptId().equals(heiOutcomesQuestion)) {
                        heiOutcomes = obs.getValueCoded().getName().toString();
                        mchChildResponseObj.put("heiOutcome", heiOutcomes);
                        mchChildResponseObj.put("heiOutcomeDate", obs.getValueDatetime());
                    }
                }
            }
            if (lastHeiEnrollmentEncounter != null) {
                for (Obs obs : lastHeiEnrollmentEncounter.getObs()) {
                    if (obs.getConcept().getConceptId().equals(prophylaxisQuestion)) {
                        Integer heiProphylaxisObsAnswer = obs.getValueCoded().getConceptId();
                        if (heiProphylaxisObsAnswer.equals(86663)) {
                            prophylaxis = obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentProphylaxisUsed", prophylaxis);
                            mchChildResponseObj.put("currentProphylaxisUsedDate", obs.getValueDatetime());
                        } else if (heiProphylaxisObsAnswer.equals(80586)) {
                            prophylaxis =  obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentProphylaxisUsed", prophylaxis);
                            mchChildResponseObj.put("currentProphylaxisUsedDate", obs.getValueDatetime());
                        } else if (heiProphylaxisObsAnswer.equals(1652)) {
                            prophylaxis =  obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentProphylaxisUsed", prophylaxis);
                            mchChildResponseObj.put("currentProphylaxisUsedDate", obs.getValueDatetime());
                        } else if (heiProphylaxisObsAnswer.equals(1149)) {
                            prophylaxis =  obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentProphylaxisUsed", prophylaxis);
                            mchChildResponseObj.put("currentProphylaxisUsedDate", obs.getValueDatetime());
                        } else if (heiProphylaxisObsAnswer.equals(1107)) {
                            prophylaxis =  obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentProphylaxisUsed", prophylaxis);
                            mchChildResponseObj.put("currentProphylaxisUsedDate", obs.getValueDatetime());
                        } else {
                            mchChildResponseObj.put("currentProphylaxisUsed", "Not Specified");
                            mchChildResponseObj.put("currentProphylaxisUsedDate", obs.getValueDatetime());
                        }
                    }

                }
            }
            if (lastHeiCWCFollowupEncounter != null) {
                for (Obs obs : lastHeiCWCFollowupEncounter.getObs()) {
                    if (obs.getConcept().getConceptId().equals(feedingMethodQuestion)) {
                        Integer heiBabyFeedingObsAnswer = obs.getValueCoded().getConceptId();
                        if (heiBabyFeedingObsAnswer.equals(5526)) {
                            feeding = obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentFeedingOption", feeding);
                            mchChildResponseObj.put("currentFeedingOptionDate", obs.getValueDatetime());
                        } else if (heiBabyFeedingObsAnswer.equals(1595)) {
                            feeding = obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentFeedingOption", feeding);
                            mchChildResponseObj.put("currentFeedingOptionDate", obs.getValueDatetime());
                        } else if (heiBabyFeedingObsAnswer.equals(6046)) {
                            feeding = obs.getValueCoded().getName().toString();
                            mchChildResponseObj.put("currentFeedingOption", feeding);
                            mchChildResponseObj.put("currentFeedingOptionDate", obs.getValueDatetime());
                        } else {
                            mchChildResponseObj.put("currentFeedingOption", "Not Specified");
                            mchChildResponseObj.put("currentFeedingOptionDate", obs.getValueDatetime());
                        }
                    }
                }
            }
            if (lastMchcsConsultation != null) {
                EncounterWrapper mchcsConsultationWrapper = new EncounterWrapper(lastMchcsConsultation);

                milestones.addAll(mchcsConsultationWrapper.allObs(Dictionary.getConcept(Dictionary.DEVELOPMENTAL_MILESTONES)));
                String joined = "";
                if (milestones.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Obs milestone : milestones) {
                        sb.append(milestone.getValueCoded().getName().toString());
                        sb.append(", ");
                    }
                    joined = sb.substring(0, sb.length() - 2);
                    mchChildResponseObj.put("milestonesAttained", joined);
                } else {
                    mchChildResponseObj.put("milestonesAttained", "Not Specified");
                }
            }

            carePanelObj.put("mchChild", mchChildResponseObj);
        }

        return carePanelObj;

    }

    /**
     * Generate payload for a form descriptor. Required when serving forms to the frontend
     * @param descriptor
     * @return
     */
    private ObjectNode generateFormDescriptorPayload(FormDescriptor descriptor) {
        ObjectNode formObj = JsonNodeFactory.instance.objectNode();
        ObjectNode encObj = JsonNodeFactory.instance.objectNode();
        Form frm = descriptor.getTarget();
        encObj.put("uuid", frm.getEncounterType().getUuid());
        encObj.put("display", frm.getEncounterType().getName());
        formObj.put("uuid", descriptor.getTargetUuid());
        formObj.put("encounterType", encObj);
        formObj.put("name", frm.getName());
        formObj.put("display", frm.getName());
        formObj.put("version", frm.getVersion());
        formObj.put("published", frm.getPublished());
        formObj.put("retired", frm.getRetired());
        return formObj;
    }

    /**
     * @see BaseRestController#getNamespace()
     */

    @Override
    public String getNamespace() {
        return "v1/kenyaemr";
    }

    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }
}
