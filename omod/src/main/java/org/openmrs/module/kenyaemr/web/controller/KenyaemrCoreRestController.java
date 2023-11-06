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

import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.PatientProgram;
import org.openmrs.Relationship;
import org.openmrs.DrugOrder;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemrorderentry.util.Utils;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbDiseaseClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbPatientClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbTreatmentNumberCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientInTbProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4CountDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.HIVEnrollment;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralLoadAndLdlCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.BMICalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.EligibleForMchmsDischargeCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.util.ZScoreUtil;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.metadata.OTZMetadata;
import org.openmrs.module.kenyaemr.metadata.OVCMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.VMMCMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenConfiguration;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.kenyaemr.wrapper.Enrollment;
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.calculation.CalculationManager;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.ConceptName;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CD4AtARTInitiationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AllCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AllVlCountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientProgramEnrollmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.Years;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import java.util.Locale;
import java.util.Comparator;

/**
 * The rest controller for exposing resources through kenyacore and kenyaemr modules
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/kenyaemr")
public class KenyaemrCoreRestController extends BaseRestController {
    protected final Log log = LogFactory.getLog(getClass());
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Autowired
    private ProgramManager programManager;

    public static String HIV_PROGRAM_UUID = "dfdc6d40-2f2f-463d-ba90-cc97350441a8";
    public static String MCH_CHILD_PROGRAM_UUID = "c2ecdf11-97cd-432a-a971-cfd9bd296b83";
    public static String MCH_MOTHER_PROGRAM_UUID = "b5d9e05f-f5ab-4612-98dd-adb75438ed34";
    public static String TB_PROGRAM_UUID = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";
    public static String TPT_PROGRAM_UUID = "335517a1-04bc-438b-9843-1ba49fb7fcd9";
    public static String OVC_PROGRAM_UUID = "6eda83f0-09d9-11ea-8d71-362b9e155667";
    public static String OTZ_PROGRAM_UUID = "24d05d30-0488-11ea-8d71-362b9e155667";
    public static String VMMC_PROGRAM_UUID = "228538f4-cad9-476b-84c3-ab0086150bcc";
    public static String PREP_PROGRAM_UUID = "214cad1c-bb62-4d8e-b927-810a046daf62";
    public static String KP_PROGRAM_UUID = "7447305a-18a7-11e9-ab14-d663bd873d93";
    public static final String KP_CLIENT_ENROLMENT = "c7f47cea-207b-11e9-ab14-d663bd873d93";
    public static final String KP_CLIENT_DISCONTINUATION = "1f76643e-2495-11e9-ab14-d663bd873d93";

    public static final String PREP_ENROLLMENT_FORM = "d5ca78be-654e-4d23-836e-a934739be555";

    public static final String PREP_DISCONTINUATION_FORM = "467c4cc3-25eb-4330-9cf6-e41b9b14cc10";

    public static final String MCH_DELIVERY_FORM_UUID = "496c7cc3-0eea-4e84-a04c-2292949e2f7f";

    public static final String MCH_DISCHARGE_FORM_UUID = "af273344-a5f9-11e8-98d0-529269fb1459";
    public static final String LAB_ORDERS_FORM_UUID = "2cdeded1-3f69-3bda-beff-1ed2ead94eaf";

    public static final Locale LOCALE = Locale.ENGLISH;

    public String name = null;

    public static String ISONIAZID_DRUG_UUID = "78280AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static String RIFAMPIN_ISONIAZID_DRUG_UUID = "1194AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

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
                PatientWrapper patientWrapper = new PatientWrapper(patient);
                Encounter lastMchEnrollment = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT));
                if(lastMchEnrollment != null) {
                    ObjectNode delivery = JsonNodeFactory.instance.objectNode();
                    delivery.put("uuid", MCH_DELIVERY_FORM_UUID);
                    delivery.put("name", "Delivery");
                    delivery.put("display", "MCH Delivery Form");
                    delivery.put("version", "1.0");
                    delivery.put("published", true);
                    delivery.put("retired", false);
                    formList.add(delivery);
                }
                CalculationResult eligibleForDischarge = EmrCalculationUtils.evaluateForPatient(EligibleForMchmsDischargeCalculation.class, null, patient);
                if((Boolean) eligibleForDischarge.getValue() == true) {
                    ObjectNode discharge = JsonNodeFactory.instance.objectNode();
                    discharge.put("uuid", MCH_DISCHARGE_FORM_UUID);
                    discharge.put("name", "Discharge");
                    discharge.put("display", "MCH Discharge Form");
                    discharge.put("version", "1.0");
                    discharge.put("published", true);
                    discharge.put("retired", false);
                    formList.add(discharge);
                }
                ObjectNode labOrder = JsonNodeFactory.instance.objectNode();
                labOrder.put("uuid", LAB_ORDERS_FORM_UUID);
                labOrder.put("name", "Laboratory Test Orders");
                labOrder.put("display", "Laboratory Test Orders");
                labOrder.put("version", "1.0");
                labOrder.put("published", true);
                labOrder.put("retired", false);
                formList.add(labOrder);
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
     * Returns regimen history for a patient
     * @param request
     * @param category // ARV or TB
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/regimenHistory")
    @ResponseBody
    public Object getRegimenHistory(@RequestParam("patientUuid") String patientUuid, @RequestParam("category") String category) {
        ObjectNode regimenObj = JsonNodeFactory.instance.objectNode();
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        ArrayNode regimenNode = JsonNodeFactory.instance.arrayNode();
        List<SimpleObject> obshistory = EncounterBasedRegimenUtils.getRegimenHistoryFromObservations(patient, category);
        for (SimpleObject obj : obshistory) {
            ObjectNode node = JsonNodeFactory.instance.objectNode();;
            node.put("startDate", obj.get("startDate").toString());
            node.put("endDate", obj.get("endDate").toString());
            node.put("regimenShortDisplay", obj.get("regimenShortDisplay").toString());
            node.put("regimenLine", obj.get("regimenLine").toString());
            node.put("regimenLongDisplay", obj.get("regimenLongDisplay").toString());
            node.put("changeReasons", obj.get("changeReasons").toString());
            node.put("regimenUuid", obj.get("regimenUuid").toString());
            node.put("current", obj.get("current").toString());
            regimenNode.add(node);
        }

        regimenObj.put("results", regimenNode);
        return regimenObj.toString();

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
     * ARV drugs
     *
     * @return custom ARV drugs object for non standard regimen
     */
    @RequestMapping(method = RequestMethod.GET, value = "/arvDrugs")
    @ResponseBody
    public Object getArvDrugs() {
        ConceptService concService = Context.getConceptService();
        ArrayNode drugs = JsonNodeFactory.instance.arrayNode();
        ObjectNode drugsObj = JsonNodeFactory.instance.objectNode();

        List<Concept> arvDrugs = Arrays.asList(
                concService.getConcept(84309),
                concService.getConcept(86663),
                concService.getConcept(84795),
                concService.getConcept(78643),
                concService.getConcept(70057),
                concService.getConcept(75628),
                concService.getConcept(74807),
                concService.getConcept(80586),
                concService.getConcept(75523),
                concService.getConcept(79040),
                concService.getConcept(83412),
                concService.getConcept(71648),
                concService.getConcept(159810),
                concService.getConcept(154378),
                concService.getConcept(74258),
                concService.getConcept(164967)
                );

        for (Concept con: arvDrugs) {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("name", con.getName() != null ? con.getName().toString() : "");
            node.put("uuid", con.getUuid() != null ? con.getUuid().toString() : "");
            drugs.add(node);
        }

        drugsObj.put("results", drugs);
        return drugsObj.toString();

    }

    /**
     * Gets the facility name given the facility code
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/facilityName")
    @ResponseBody
    public Object getFacilityName(@RequestParam("facilityCode") String facilityCode) {
        SimpleObject locationResponseObj = new SimpleObject();
        Location facility = Context.getService(KenyaEmrService.class).getLocationByMflCode(facilityCode);
        locationResponseObj.put("name", facility.getName());
        return locationResponseObj;
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
     * Gets the last regimen encounter uuid by category
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/lastRegimenEncounter")
    @ResponseBody
    public Object getLastRegimenEncounterUuid(@RequestParam("category") String category, @RequestParam("patientUuid") String patientUuid) {
        ObjectNode encObj = JsonNodeFactory.instance.objectNode();
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        String event = null;

        Encounter enc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, category);

        String ARV_TREATMENT_PLAN_EVENT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String DATE_REGIMEN_STOPPED = "1191AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String endDate = null;

        if (enc != null) {
            Date latest = null;
            List<Date> dates = new ArrayList<Date>();
            for(Obs obs:enc.getObs()) {
                dates.add(obs.getObsDatetime());
                latest = Collections.max(dates);
            }

            for(Obs obs:enc.getObs()) {
				if(obs.getConcept().getUuid().equals(ARV_TREATMENT_PLAN_EVENT) && obs.getObsDatetime().equals(latest)) {
					event =obs.getValueCoded() != null ?  obs.getValueCoded().getName().getName() : "";
				}
                if (obs.getConcept() != null && obs.getConcept().getUuid().equals(DATE_REGIMEN_STOPPED)) {
                    if(obs.getValueDatetime() != null){
                        endDate = DATE_FORMAT.format(obs.getValueDatetime());
                    }
                }

			}

        }
        node.put("uuid", enc != null ?  enc.getUuid() : "");
        node.put("startDate", enc != null ? DATE_FORMAT.format(enc.getEncounterDatetime()): "");
        node.put("endDate", endDate);
        node.put("event", event);
        encObj.put("results", node);

        return encObj.toString();
    }


    /**
     * Get a list of standard regimen
          * @param request
     * @param
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/standardRegimen")
    @ResponseBody
    public Object getStandardRegimen() throws SAXException, IOException, ParserConfigurationException {
        ArrayNode standardRegimenCategories = JsonNodeFactory.instance.arrayNode();

        ObjectNode resultsObj = JsonNodeFactory.instance.objectNode();
        for (RegimenConfiguration configuration : Context.getRegisteredComponents(RegimenConfiguration.class)) {
            InputStream stream = null;
            try {
                ClassLoader loader = configuration.getClassLoader();
                stream = loader.getResourceAsStream(configuration.getDefinitionsPath());
                if (stream == null || stream.available() == 0) {
                    throw new RuntimeException("Empty or unavailable stream for " + configuration.getDefinitionsPath());
                }

                // XML parsing logic
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();
                Document document = builder.parse(stream);
                Element root = document.getDocumentElement();
                // Category section i.e ARV, TB etc
                NodeList categoryNodes = root.getElementsByTagName("category");
                for (int c = 0; c < categoryNodes.getLength(); c++) {
                    ObjectNode categoryObj = JsonNodeFactory.instance.objectNode();
                    Element categoryElement = (Element) categoryNodes.item(c);
                    String categoryCode = categoryElement.getAttribute("code");
                    categoryObj.put("categoryCode", categoryCode);


                    NodeList groupNodes = categoryElement.getElementsByTagName("group");
                    ArrayNode standardRegimen = JsonNodeFactory.instance.arrayNode();

                    for (int g = 0; g < groupNodes.getLength(); g++) {
                        ObjectNode standardRegimenObj = JsonNodeFactory.instance.objectNode();
                        Element groupElement = (Element) groupNodes.item(g);
                        String groupName = groupElement.getAttribute("name");
                        String regimenLineValue = "";

                        if (groupName.equalsIgnoreCase("Adult (first line)")) {
                            regimenLineValue = "AF";
                        }else if (groupName.equalsIgnoreCase("Adult (second line)")) {
                            regimenLineValue = "AS";
                        }else if (groupName.equalsIgnoreCase("Adult (third line)")) {
                            regimenLineValue = "AT";
                        }else if (groupName.equalsIgnoreCase("Child (First Line)")) {
                            regimenLineValue = "CF";
                        }else if (groupName.equalsIgnoreCase("Child (Second Line)")) {
                            regimenLineValue = "CS";
                        }else if (groupName.equalsIgnoreCase("Child (Third Line)")) {
                            regimenLineValue = "CT";
                        }else if (groupName.equalsIgnoreCase("Intensive Phase (Adult)")) {
                            regimenLineValue = "Intensive Phase (Adult)";
                        }else if (groupName.equalsIgnoreCase("Intensive Phase (Child)")) {
                            regimenLineValue = "Intensive Phase (Child)";
                        }else if (groupName.equalsIgnoreCase("Continuation Phase (Adult)")) {
                            regimenLineValue = "Continuation Phase (Adult)";
                        }

                    
                    

                        standardRegimenObj.put("regimenline", groupName);
                        standardRegimenObj.put("regimenLineValue", regimenLineValue);

                        ArrayNode regimen = JsonNodeFactory.instance.arrayNode();
                        NodeList regimenNodes = groupElement.getElementsByTagName("regimen");
                        for (int r = 0; r < regimenNodes.getLength(); r++) {
                            ObjectNode regimenObj = JsonNodeFactory.instance.objectNode();
                            Element regimenElement = (Element) regimenNodes.item(r);
                            String name = regimenElement.getAttribute("name");
                            String conceptRef = regimenElement.getAttribute("conceptRef");
                            regimenObj.put("name", name);
                            regimenObj.put("conceptRef", conceptRef);
                            regimen.add(regimenObj);
                        }
                        standardRegimenObj.put("regimen", regimen);
                        standardRegimen.add(standardRegimenObj);
                    }
                    categoryObj.put("category", standardRegimen);
                    standardRegimenCategories.add(categoryObj);

                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to load " + configuration.getModuleId() + ":" + configuration.getDefinitionsPath(), e);
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        resultsObj.put("results", standardRegimenCategories);

        return resultsObj.toString();
    }


    /**
     * Returns regimen change/stop reasons
          * @param request
     * @param category // ARV or TB
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/regimenReason")
    @ResponseBody
    public Object getRegimenReason() {
        ObjectNode regimenReasonObj = JsonNodeFactory.instance.objectNode();

        ArrayNode reasons = JsonNodeFactory.instance.arrayNode();
        ObjectNode arvReasonsObj = JsonNodeFactory.instance.objectNode();
        ObjectNode tbReasonsObj = JsonNodeFactory.instance.objectNode();
        Map<String, String> arvReasonOptionsMap = new HashMap<String, String>();
        Map<String, String> tbReasonOptionsMap = new HashMap<String, String>();
        arvReasonOptionsMap.put("Toxicity / side effects", "102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Pregnancy", "1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Risk of pregnancy", "160559AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("New diagnosis of TB", "160567AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("New drug available", "160561AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Drugs out of stock", "1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Clinical treatment failure", "843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Immunological failure", "160566AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Virological Failure", "160569AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Poor Adherence", "159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Inpatient care or hospitalization", "5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Refusal / patient decision", "127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Planned treatment interruption", "160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Completed total PMTCT", "1253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Tuberculosis treatment started", "1270AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        arvReasonOptionsMap.put("Patient lacks finance", "819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        ArrayNode arvReasons = JsonNodeFactory.instance.arrayNode();
        for (Map.Entry<String, String> entry : arvReasonOptionsMap.entrySet()) {
            ObjectNode arvCategoryReasonObj = JsonNodeFactory.instance.objectNode();
            arvCategoryReasonObj.put("label", entry.getKey());
            arvCategoryReasonObj.put("value", entry.getValue());
            arvReasons.add(arvCategoryReasonObj);
        }
        arvReasonsObj.put("category", "ARV");
        arvReasonsObj.put("reason", arvReasons);
        reasons.add(arvReasonsObj);

        tbReasonOptionsMap.put("Toxicity / side effects", "102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Pregnancy", "1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Clinical treatment failure", "843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Poor Adherence", "159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Inpatient care or hospitalization", "5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Drugs out of stock", "1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Planned treatment interruption", "160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Refusal / patient decision", "127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Drug formulation changed", "1258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        tbReasonOptionsMap.put("Patient lacks finance", "819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");


        ArrayNode tbReasons = JsonNodeFactory.instance.arrayNode();
        for (Map.Entry<String, String> entry : tbReasonOptionsMap.entrySet()) {
            ObjectNode tbCategoryReasonObj = JsonNodeFactory.instance.objectNode();
            tbCategoryReasonObj.put("label", entry.getKey());
            tbCategoryReasonObj.put("value", entry.getValue());
            tbReasons.add(tbCategoryReasonObj);
        }
        tbReasonsObj.put("category", "TB");
        tbReasonsObj.put("reason", tbReasons);
        reasons.add(tbReasonsObj);
        regimenReasonObj.put("results", reasons);
        return regimenReasonObj.toString();

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

    /**
     * Fetches Patient's program enrollments
     *
     * @return patient program enrollment data
     */
    @RequestMapping(method = RequestMethod.GET, value = "/patientHistoricalEnrollment")
    @ResponseBody
    public ArrayList<SimpleObject> getPatientHistoricalEnrollment(@RequestParam("patientUuid") String patientUuid) {
        List<ProgramDescriptor> programs = new ArrayList<ProgramDescriptor>();
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        if (!patient.isVoided()) {
            Collection<ProgramDescriptor> activePrograms = programManager.getPatientActivePrograms(patient);
            Collection<ProgramDescriptor> eligiblePrograms = programManager.getPatientEligiblePrograms(patient);

            // Display active programs on top
            programs.addAll(activePrograms);

            // Don't add duplicates for programs for which patient is both active and eligible
            for (ProgramDescriptor descriptor : eligiblePrograms) {
                if (!programs.contains(descriptor)) {
                    programs.add(descriptor);
                }
            }
        }
        ArrayList enrollmentDetails = new ArrayList<SimpleObject>();
        for(ProgramDescriptor descriptor: programs) {
            Program program = descriptor.getTarget();
            Form defaultEnrollmentForm = descriptor.getDefaultEnrollmentForm().getTarget();
            Form defaultCompletionForm = descriptor.getDefaultCompletionForm().getTarget();

            List<PatientProgram> allEnrollments = programManager.getPatientEnrollments(patient, program);
            for (PatientProgram patientProgramEnrollment : allEnrollments) {
                SimpleObject programDetails = new SimpleObject();
                programDetails.put("enrollmentUuid", patientProgramEnrollment.getUuid());
                programDetails.put("dateEnrolled", formatDate(patientProgramEnrollment.getDateEnrolled()));
                programDetails.put("dateCompleted", formatDate(patientProgramEnrollment.getDateCompleted()));
                // hiv program
                if (patientProgramEnrollment.getProgram().getUuid().equals(HIV_PROGRAM_UUID)) {

                    Enrollment hivEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter hivEnrollmentEncounter = hivEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter hivCompletionEncounter = hivEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    Obs whoObs = hivEnrollment.firstObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE));
                    if (whoObs != null) {
                        programDetails.put("whoStage", whoObs.getValueCoded().getName().getName());
                    }

                    // art start date
                    Date artStartDate = null;
                    CalculationResult artStartDateResults = EmrCalculationUtils
                            .evaluateForPatient(InitialArtStartDateCalculation.class, null, patient);
                    if (artStartDateResults != null) {
                        artStartDate = (Date) artStartDateResults.getValue();
                        programDetails.put("artStartDate", formatDate((Date) artStartDateResults.getValue()));
                    } else {
                        programDetails.put("artStartDate", "");
                    }

                    if (hivEnrollmentEncounter != null) {
                        for (Obs obs : hivEnrollmentEncounter.getAllObs(true)) {
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT))) {
                                programDetails.put("entryPoint", entryPointAbbriviations(obs.getValueCoded()));
                            }

                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.CD4_COUNT))) {
                                programDetails.put("cd4Count", obs.getValueNumeric().intValue());
                                programDetails.put("cd4CountDate", formatDate(obs.getObsDatetime()));
                            }
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.CD4_PERCENT))) {
                                programDetails.put("cd4Percentage", obs.getValueNumeric().intValue());
                                programDetails.put("cd4PercentageDate", formatDate(obs.getObsDatetime()));
                            }
                            Encounter firstEnc = EncounterBasedRegimenUtils.getFirstEncounterForCategory(patient,
                                    "ARV");
                            SimpleObject firstEncDetails = null;
                            if (firstEnc != null) {
                                firstEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(firstEnc.getObs(),
                                        firstEnc);
                            }
                            Encounter lastEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");
                            SimpleObject lastEncDetails = null;
                            if (lastEnc != null) {
                                lastEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastEnc.getObs(),
                                        lastEnc);
                            }
                            programDetails.put("enrollmentEncounterUuid", hivEnrollmentEncounter.getUuid());
                            programDetails.put("lastEncounter", lastEncDetails);
                            programDetails.put("firstEncounter", firstEncDetails);
                        }
                    }
                    if (hivCompletionEncounter != null) {
                        for (Obs obs : hivCompletionEncounter.getAllObs(true)) {
                            if (obs.getConcept()
                                    .equals(Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION))) {
                                programDetails.put("reason", obs.getValueCoded().getName().getName());
                            }
                        }
                        programDetails.put("discontinuationEncounterUuid", hivCompletionEncounter.getUuid());
                    }
                    programDetails.put("discontinuationFormUuid", HivMetadata._Form.HIV_DISCONTINUATION);
                    programDetails.put("discontinuationFormName", "HIV Discontinuation");
                    programDetails.put("enrollmentFormUuid", HivMetadata._Form.HIV_ENROLLMENT);
                    programDetails.put("enrollmentFormName", "HIV Enrollment");
                }

                // tpt program
                if (patientProgramEnrollment.getProgram().getUuid().equals(TPT_PROGRAM_UUID)) {
                    Enrollment tptEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter tptEnrollmentEncounter = tptEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter tptDiscontinuationEncounter = tptEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (tptEnrollmentEncounter != null) {
                        for (Obs obs : tptEnrollmentEncounter.getAllObs(true)) {
                            if (obs.getConcept()
                                    .equals(Dictionary.getConcept(Dictionary.INDICATION_FOR_TB_PROPHYLAXIS))) {
                                programDetails.put("tptIndication", obs.getValueCoded().getName().getName());
                            }
                        }
                        programDetails.put("enrollmentEncounterUuid", tptEnrollmentEncounter.getUuid());
                    }
                    if (tptDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", tptDiscontinuationEncounter.getUuid());
                    }
                    try {
                        // get medication patient is on
                        List<DrugOrder> allDrugOrders = EmrUtils.drugOrdersFromOrders(patient, null);
                        List<DrugOrder> tptDrugOrders = new ArrayList<DrugOrder>();

                        for (DrugOrder order : allDrugOrders) {
                            if (order != null && order.getConcept() != null) {
                                ConceptName cn = order.getConcept().getName(CoreConstants.LOCALE);
                                if (cn != null && (cn.getUuid().equals(ISONIAZID_DRUG_UUID)
                                        || cn.getUuid().equals(RIFAMPIN_ISONIAZID_DRUG_UUID))) {
                                    tptDrugOrders.add(order);
                                }
                            }
                        }
                        if (!tptDrugOrders.isEmpty()) {

                            Collections.sort(tptDrugOrders, new Comparator<DrugOrder>() {
                                @Override
                                public int compare(DrugOrder order1, DrugOrder order2) {
                                    return order2.getDateCreated().compareTo(order1.getDateCreated());
                                }
                            });
                            DrugOrder drugOrder = (DrugOrder) tptDrugOrders.get(0).cloneForRevision();
                            // Now you can use the latestDrugOrder as needed
                            programDetails.put("tptDrugName",
                                    drugOrder.getDrug() != null ? drugOrder.getDrug().getFullName(LOCALE) : "");
                            programDetails.put("tptDrugStartDate", formatDate(drugOrder.getEffectiveStartDate()));
                        }
                    } catch (NullPointerException e) {
                        // Handle null pointer exception
                        e.printStackTrace();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    programDetails.put("enrollmentFormUuid", IPTMetadata._Form.IPT_INITIATION);
                    programDetails.put("enrollmentFormName", "IPT Initiation");
                    programDetails.put("discontinuationFormUuid", IPTMetadata._Form.IPT_OUTCOME);
                    programDetails.put("discontinuationFormName", "IPT Outcome");
                }

                // tb program
                if (patientProgramEnrollment.getProgram().getUuid().equals(TB_PROGRAM_UUID)) {
                    Enrollment tbEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter tbEnrollmentEncounter = tbEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter tbDiscontinuationEncounter = tbEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (tbEnrollmentEncounter != null) {
                        for (Obs obs : tbEnrollmentEncounter.getAllObs(true)) {
                            if (obs.getConcept()
                                    .equals(Dictionary.getConcept(Dictionary.REFERRING_CLINIC_OR_HOSPITAL))) {
                                programDetails.put("referredFrom", obs.getValueCoded().getName().getName());
                            }
                            programDetails.put("enrollmentEncounterUuid", tbEnrollmentEncounter.getUuid());
                            Encounter firstEnc = EncounterBasedRegimenUtils.getFirstEncounterForCategory(patient, "TB");
                            SimpleObject firstEncDetails = null;
                            if (firstEnc != null) {
                                firstEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(firstEnc.getObs(),
                                        firstEnc);
                            }
                            programDetails.put("firstEncounter", firstEncDetails);
                        }
                    }
                    if (tbDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", tbDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", TbMetadata._Form.TB_ENROLLMENT);
                    programDetails.put("enrollmentFormName", "TB Enrollment");
                    programDetails.put("discontinuationFormUuid", TbMetadata._Form.TB_COMPLETION);
                    programDetails.put("discontinuationFormName", "TB Discontinuation");
                }

                // mch mother program
                if (patientProgramEnrollment.getProgram().getUuid().equals(MCH_MOTHER_PROGRAM_UUID)) {
                    Enrollment mchmEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter mchmEnrollmentEncounter = mchmEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter mchmDiscontinuationEncounter = mchmEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());
                    EncounterType mchMsConsultation = MetadataUtils.existing(EncounterType.class,
                            MchMetadata._EncounterType.MCHMS_CONSULTATION);
                    Form delivery = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);
                    Encounter deliveryEncounter = mchmEnrollment.encounterByForm(mchMsConsultation, delivery);
                    Integer parityTerm = 0;
                    Integer gravida = 0;

                    if (mchmEnrollmentEncounter != null) {
                        for (Obs obs : mchmEnrollmentEncounter.getAllObs(true)) {
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.ANTENATAL_CASE_NUMBER))) {
                                programDetails.put("ancNumber", obs.getValueCoded().getName().getName());
                            }
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD))) {
                                if (deliveryEncounter == null) {
                                    Weeks weeks = Weeks.weeksBetween(new DateTime(obs.getValueDate()),
                                            new DateTime(new Date()));
                                    programDetails.put("gestationInWeeks", weeks.getWeeks());
                                    programDetails.put("lmp", formatDate(obs.getValueDate()));
                                    programDetails.put("eddLmp",
                                            formatDate(CoreUtils.dateAddDays(obs.getValueDate(), 280)));
                                } else {
                                    programDetails.put("gestationInWeeks", "N/A");
                                    programDetails.put("lmp", "N/A");
                                    programDetails.put("eddLmp", "N/A");
                                }
                            }
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.EXPECTED_DATE_OF_DELIVERY))) {
                                if (deliveryEncounter == null) {
                                    programDetails.put("eddUltrasound", formatDate(obs.getValueDate()));
                                }
                            }
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.GRAVIDA))) {
                                programDetails.put("gravida", obs.getValueNumeric().intValue());
                            }
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.PARITY_TERM))) {
                                parityTerm = obs.getValueNumeric().intValue();
                            }
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.PARITY_ABORTION))) {
                                gravida = obs.getValueNumeric().intValue();
                            }
                            if (parityTerm != null && gravida != null) {
                                programDetails.put("parity", parityTerm + gravida);
                            }
                            programDetails.put("enrollmentEncounterUuid", mchmEnrollmentEncounter.getUuid());
                        }

                    }
                    if (mchmDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", mchmDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", MchMetadata._Form.MCHMS_ENROLLMENT);
                    programDetails.put("enrollmentFormName", "MCH-MS Enrollment");
                    programDetails.put("discontinuationFormUuid", MchMetadata._Form.MCHMS_DISCONTINUATION);
                    programDetails.put("discontinuationFormName", "MCH-MS Discontinuation");
                }

                // mch child program
                if (patientProgramEnrollment.getProgram().getUuid().equals(MCH_CHILD_PROGRAM_UUID)) {
                    Enrollment mchcEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter mchcEnrollmentEncounter = mchcEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter mchcDiscontinuationEncounter = mchcEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (mchcEnrollmentEncounter != null) {
                        for (Obs obs : mchcEnrollmentEncounter.getAllObs(true)) {
                            if (obs.getConcept().equals(Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT))) {
                                programDetails.put("entryPoint", entryPointAbbriviations(obs.getValueCoded()));
                            }
                            programDetails.put("enrollmentEncounterUuid", mchcEnrollmentEncounter.getUuid());
                        }
                    }
                    if (mchcDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", mchcDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", MchMetadata._Form.MCHCS_ENROLLMENT);
                    programDetails.put("enrollmentFormName", "Mch Child Enrolment Form");
                    programDetails.put("discontinuationFormUuid", MchMetadata._Form.MCHCS_DISCONTINUATION);
                    programDetails.put("discontinuationFormName", "Child Welfare Services Discontinuation");
                }

                // otz program
                if (patientProgramEnrollment.getProgram().getUuid().equals(OTZ_PROGRAM_UUID)) {
                    Enrollment otzEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter otzEnrollmentEncounter = otzEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter otzDiscontinuationEncounter = otzEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (otzEnrollmentEncounter != null) {
                        programDetails.put("enrollmentEncounterUuid", otzEnrollmentEncounter.getUuid());
                    }
                    if (otzDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", otzDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", OTZMetadata._Form.OTZ_ENROLLMENT_FORM);
                    programDetails.put("enrollmentFormName", "OTZ Enrollment Form");
                    programDetails.put("discontinuationFormUuid", OTZMetadata._Form.OTZ_DISCONTINUATION_FORM);
                    programDetails.put("discontinuationFormName", "OTZ Discontinuation Form");
                }

                // ovc program
                if (patientProgramEnrollment.getProgram().getUuid().equals(OVC_PROGRAM_UUID)) {
                    Enrollment ovcEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter ovcEnrollmentEncounter = ovcEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter ovcDiscontinuationEncounter = ovcEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (ovcEnrollmentEncounter != null) {
                        programDetails.put("enrollmentEncounterUuid", ovcEnrollmentEncounter.getUuid());
                    }
                    if (ovcDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", ovcDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", OVCMetadata._Form.OVC_ENROLLMENT_FORM);
                    programDetails.put("enrollmentFormName", "OVC Enrollment Form");
                    programDetails.put("discontinuationFormUuid", OVCMetadata._Form.OVC_DISCONTINUATION_FORM);
                    programDetails.put("discontinuationFormName", "OVC Discontinuation Form");
                }

                // vmmc program
                if (patientProgramEnrollment.getProgram().getUuid().equals(VMMC_PROGRAM_UUID)) {
                    Enrollment vmmcEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter vmmcEnrollmentEncounter = vmmcEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter vmmcDiscontinuationEncounter = vmmcEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (vmmcEnrollmentEncounter != null) {
                        programDetails.put("enrollmentEncounterUuid", vmmcEnrollmentEncounter.getUuid());
                    }
                    if (vmmcDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", vmmcDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", VMMCMetadata._Form.VMMC_ENROLLMENT_FORM);
                    programDetails.put("enrollmentFormName", "VMMC Enrollment Form");
                    programDetails.put("discontinuationFormUuid", VMMCMetadata._Form.VMMC_DISCONTINUATION_FORM);
                    programDetails.put("discontinuationFormUuid", "VMMC Discontinuation Form");
                }

                // prep program
                if (patientProgramEnrollment.getProgram().getUuid().equals(PREP_PROGRAM_UUID)) {
                    Enrollment prepEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter prepEnrollmentEncounter = prepEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter prepDiscontinuationEncounter = prepEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (prepEnrollmentEncounter != null) {
                        programDetails.put("enrollmentEncounterUuid", prepEnrollmentEncounter.getUuid());
                    }
                    if (prepDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", prepDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", PREP_ENROLLMENT_FORM);
                    programDetails.put("enrollmentFormName", "PrEP Enrollment");
                    programDetails.put("discontinuationFormUuid", PREP_DISCONTINUATION_FORM);
                    programDetails.put("discontinuationFormName", "PrEP Client Discontinuation");
                }

                // kp program
                if (patientProgramEnrollment.getProgram().getUuid().equals(KP_PROGRAM_UUID)) {
                    Enrollment kpEnrollment = new Enrollment(patientProgramEnrollment);
                    Encounter kpEnrollmentEncounter = kpEnrollment
                            .lastEncounter(defaultEnrollmentForm.getEncounterType());
                    Encounter kpDiscontinuationEncounter = kpEnrollment
                            .lastEncounter(defaultCompletionForm.getEncounterType());

                    if (kpEnrollmentEncounter != null) {
                        programDetails.put("enrollmentEncounterUuid", kpEnrollmentEncounter.getUuid());
                    }
                    if (kpDiscontinuationEncounter != null) {
                        programDetails.put("discontinuationEncounterUuid", kpDiscontinuationEncounter.getUuid());
                    }
                    programDetails.put("enrollmentFormUuid", KP_CLIENT_ENROLMENT);
                    programDetails.put("enrollmentFormUuid", "KP Enrollment");
                    programDetails.put("discontinuationFormUuid", KP_CLIENT_DISCONTINUATION);
                    programDetails.put("discontinuationFormName", "KP Discontinuation");
                }

                programDetails.put("programName", patientProgramEnrollment.getProgram().getName());
                programDetails.put("active", patientProgramEnrollment.getActive());
                programDetails.put("dateEnrolled", formatDate(patientProgramEnrollment.getDateEnrolled()));
                programDetails.put("dateCompleted", formatDate(patientProgramEnrollment.getDateCompleted()));
                enrollmentDetails.add(programDetails);
            }
        }

        return enrollmentDetails;
    }

    /**
     * TODO : check performance, when AdministrationService is used, kenyaemr takes
     * longer to start
     * Fetches Patient's program enrollments
     *
     * @return patient program enrollment data
     */
    @RequestMapping(method = RequestMethod.GET, value = "/patientSummary")
    @ResponseBody
    public Object getPatientSummary(@RequestParam("patientUuid") String patientUuid) {
        AdministrationService administrationService = Context.getAdministrationService();
        String isKDoD = (administrationService.getGlobalProperty("kenyaemr.isKDoD"));
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        PatientService patientService = Context.getPatientService();
        KenyaEmrService kenyaEmrService = Context.getService(KenyaEmrService.class);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        SimpleObject patientSummary = new SimpleObject();

        patientSummary.put("reportDate", formatDate(new Date()));
        patientSummary.put("clinicName", kenyaEmrService.getDefaultLocation().getName());
        patientSummary.put("mflCode", kenyaEmrService.getDefaultLocationMflCode());
        patientSummary.put("patientName", patient.getPersonName().getFullName());
        patientSummary.put("birthDate", formatDate(patient.getBirthdate()));
        patientSummary.put("gender", patient.getGender());
        patientSummary.put("age", age(new Date(), patient.getBirthdate()));

        if (isKDoD.equals("true")) {
            // KDOD Number
            PatientIdentifierType kdodServiceNumber = MetadataUtils.existing(PatientIdentifierType.class,
                    CommonMetadata._PatientIdentifierType.KDoD_SERVICE_NUMBER);
            PatientIdentifier serviceNumberObj = patientService.getPatient(patient.getPatientId())
                    .getPatientIdentifier(kdodServiceNumber);
            if (serviceNumberObj != null) {
                patientSummary.put("kdodServiceNumber", serviceNumberObj.getIdentifier());
            } else {
                patientSummary.put("kdodServiceNumber", "");
            }
            // KDOD Unit
            PersonAttributeType kdodServiceUnit = MetadataUtils.existing(PersonAttributeType.class,
                    CommonMetadata._PersonAttributeType.KDOD_UNIT);
            PersonAttribute kdodUnitObj = patientService.getPatient(patient.getPatientId())
                    .getAttribute(kdodServiceUnit);

            if (kdodUnitObj != null) {
                patientSummary.put("kdodUnit", kdodUnitObj.getValue());
            } else {
                patientSummary.put("kdodUnit", "");
            }
            // KDOD Cadre
            PersonAttributeType kdodServiceCadre = MetadataUtils.existing(PersonAttributeType.class,
                    CommonMetadata._PersonAttributeType.KDOD_CADRE);
            PersonAttribute kdodCadreObj = patientService.getPatient(patient.getPatientId())
                    .getAttribute(kdodServiceCadre);
            if (kdodCadreObj != null) {
                patientSummary.put("kdodCadre", kdodCadreObj.getValue());
            } else {
                patientSummary.put("kdodCadre", "");
            }
            // KDOD Rank
            PersonAttributeType kdodServiceRank = MetadataUtils.existing(PersonAttributeType.class,
                    CommonMetadata._PersonAttributeType.KDOD_RANK);
            PersonAttribute kdodRankObj = patientService.getPatient(patient.getPatientId())
                    .getAttribute(kdodServiceRank);
            if (kdodRankObj != null) {
                patientSummary.put("kdodRank", kdodRankObj.getValue());
            } else {
                patientSummary.put("kdodRank", "");
            }

        } else {
            PatientIdentifierType type = MetadataUtils.existing(PatientIdentifierType.class,
                    HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
            List<PatientIdentifier> upn = patientService.getPatientIdentifiers(null, Arrays.asList(type), null,
                    Arrays.asList(patient), false);
            if (upn.size() > 0) {
                patientSummary.put("uniquePatientIdentifier", upn.get(0).getIdentifier());
            }
            PatientIdentifierType identifierType = MetadataUtils.existing(PatientIdentifierType.class,
                    CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
            List<PatientIdentifier> nupi = patientService.getPatientIdentifiers(null, Arrays.asList(identifierType),
                    null, Arrays.asList(patient), false);
            if (nupi.size() > 0) {
                patientSummary.put("nationalUniquePatientIdentifier", nupi.get(0).getIdentifier());
            }
        }

        PatientCalculationContext context = Context.getService(PatientCalculationService.class)
                .createCalculationContext();
        context.setNow(new Date());

        // get marital status
        CalculationResultMap civilStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.CIVIL_STATUS),
                Arrays.asList(patient.getId()), context);
        Concept status = EmrCalculationUtils.codedObsResultForPatient(civilStatus, patient.getPatientId());
        if (status != null) {
            patientSummary.put("maritalStatus", status.getName().getName());
        } else {
            patientSummary.put("maritalStatus", "");
        }

        // height
        CalculationResultMap latestHeight = Calculations.lastObs(Dictionary.getConcept(Dictionary.HEIGHT_CM),
                Arrays.asList(patient.getId()), context);
        Obs heightValue = EmrCalculationUtils.obsResultForPatient(latestHeight, patient.getPatientId());
        if (heightValue != null) {
            patientSummary.put("height", heightValue.getValueNumeric().toString());
        } else {
            patientSummary.put("height", "");
        }
        // weight
        CalculationResultMap latestWeight = Calculations.lastObs(Dictionary.getConcept(Dictionary.WEIGHT_KG),
                Arrays.asList(patient.getId()), context);
        Obs weightValue = EmrCalculationUtils.obsResultForPatient(latestWeight, patient.getPatientId());
        if (weightValue != null) {
            patientSummary.put("weight", weightValue.getValueNumeric().toString());
        } else {
            patientSummary.put("weight", "");
        }

        // Oxygen Saturation/
        CalculationResultMap latestOxygen = Calculations.lastObs(Dictionary.getConcept(Dictionary.OXYGEN_SATURATION),
                Arrays.asList(patient.getId()), context);
        Obs latestOxygenValue = EmrCalculationUtils.obsResultForPatient(latestOxygen, patient.getPatientId());
        if (latestOxygenValue != null) {
            patientSummary.put("oxygenSaturation", latestOxygenValue.getValueNumeric().toString());
        } else {
            patientSummary.put("oxygenSaturation", "");
        }

        // pulse rate
        CalculationResultMap latestPulseRate = Calculations.lastObs(Dictionary.getConcept(Dictionary.PULSE_RATE),
                Arrays.asList(patient.getId()), context);
        Obs latestPulseRates = EmrCalculationUtils.obsResultForPatient(latestPulseRate, patient.getPatientId());
        if (latestPulseRates != null) {
            patientSummary.put("pulseRate", latestPulseRates.getValueNumeric().toString());
        } else {
            patientSummary.put("pulseRate", "");
        }

        // Blood Pressure
        CalculationResultMap bloodPressure = Calculations.lastObs(Dictionary.getConcept(Dictionary.BLOOD_PRESSURE),
                Arrays.asList(patient.getId()), context);
        Obs latestBloodPressure = EmrCalculationUtils.obsResultForPatient(bloodPressure, patient.getPatientId());
        if (latestBloodPressure != null) {
            patientSummary.put("bloodPressure", latestBloodPressure.getValueNumeric().toString());
        } else {
            patientSummary.put("bloodPressure", "");
        }

        // BP_DIASTOLIC
        CalculationResultMap bpDiastolic = Calculations.lastObs(
                Dictionary.getConcept(Dictionary.BLOOD_PRESSURE_DIASTOLIC), Arrays.asList(patient.getId()), context);
        Obs latestBpDiastolic = EmrCalculationUtils.obsResultForPatient(bpDiastolic, patient.getPatientId());
        if (latestBpDiastolic != null) {
            patientSummary.put("bpDiastolic", latestBpDiastolic.getValueNumeric().toString());
        } else {
            patientSummary.put("bpDiastolic", "");
        }

        // LMP
        if (patient.getGender().equals("F")) {
            CalculationResultMap latestLmp = Calculations.lastObs(Dictionary.getConcept(Dictionary.LMP),
                    Arrays.asList(patient.getId()), context);
            Obs latestLmpResults = EmrCalculationUtils.obsResultForPatient(latestLmp, patient.getPatientId());
            if (latestLmpResults != null) {
                patientSummary.put("lmp", formatDate(latestLmpResults.getObsDatetime()));
            } else {
                patientSummary.put("lmp", "");
            }
        }

        // respitatory Rate/
        CalculationResultMap respiratoryRate = Calculations.lastObs(Dictionary.getConcept(Dictionary.RESPIRATORY_RATE),
                Arrays.asList(patient.getId()), context);
        Obs latestRespiratoryRate = EmrCalculationUtils.obsResultForPatient(respiratoryRate, patient.getPatientId());
        if (latestRespiratoryRate != null) {
            patientSummary.put("respiratoryRate", latestRespiratoryRate.getValueNumeric().toString());
        } else {
            patientSummary.put("respiratoryRate", "");
        }

        // date confirmed hiv positive
        CalculationResultMap hivConfirmation = Calculations.lastObs(
                Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), Arrays.asList(patient.getId()), context);
        Date dateConfirmed = EmrCalculationUtils.datetimeObsResultForPatient(hivConfirmation, patient.getPatientId());
        if (dateConfirmed != null) {
            patientSummary.put("dateConfirmedHIVPositive", formatDate(dateConfirmed));
        } else {
            patientSummary.put("dateConfirmedHIVPositive", "");
        }

        // first cd4 count
        CalculationResultMap firstCd4CountMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.CD4_COUNT),
                Arrays.asList(patient.getId()), context);
        Obs cd4Value = EmrCalculationUtils.obsResultForPatient(firstCd4CountMap, patient.getPatientId());
        if (cd4Value != null) {
            patientSummary.put("firstCd4", cd4Value.getValueNumeric().toString());
            patientSummary.put("firstCd4Date", formatDate(cd4Value.getObsDatetime()));
        } else {
            patientSummary.put("firstCd4", "");
            patientSummary.put("firstCd4Date", "N/A");
        }

        // date enrolled into care
        CalculationResultMap enrolled = Calculations.firstEnrollments(hivProgram, Arrays.asList(patient.getPatientId()),
                context);
        PatientProgram program = EmrCalculationUtils.resultForPatient(enrolled, patient.getPatientId());
        if (program != null) {
            patientSummary.put("dateEnrolledIntoCare", formatDate(program.getDateEnrolled()));
        } else {
            patientSummary.put("dateEnrolledIntoCare", "");
        }

        // who staging
        CalculationResultMap whoStage = Calculations.firstObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE),
                Arrays.asList(patient.getPatientId()), context);
        Obs firstWhoStageObs = EmrCalculationUtils.obsResultForPatient(whoStage, patient.getPatientId());
        if (firstWhoStageObs != null) {
            patientSummary.put("whoStagingAtEnrollment", firstWhoStageObs.getValueCoded().getName().getName());
        } else {
            patientSummary.put("whoStagingAtEnrollment", "");
        }

        if (patient.getGender().equals("F")) {
            // CaCx
            CalculationResultMap cacxMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.CACX_SCREENING),
                    Arrays.asList(patient.getPatientId()), context);
            Obs cacxObs = EmrCalculationUtils.obsResultForPatient(cacxMap, patient.getPatientId());
            if (cacxObs != null) {
                patientSummary.put("caxcScreeningOutcome", cacxScreeningOutcome(cacxObs.getValueCoded()));
            } else {
                patientSummary.put("caxcScreeningOutcome", "None");
            }
        }

        // STI SCREENING
        CalculationResultMap stiScreen = Calculations.firstObs(Dictionary.getConcept(Dictionary.STI_SCREENING),
                Arrays.asList(patient.getPatientId()), context);
        Obs stiObs = EmrCalculationUtils.obsResultForPatient(stiScreen, patient.getPatientId());
        if (stiObs != null) {
            patientSummary.put("stiScreeningOutcome", stiScreeningOutcome(stiObs.getValueCoded()));
        } else {
            patientSummary.put("stiScreeningOutcome", "None");
        }

        // Fp protection
        CalculationResultMap fplanning = Calculations.firstObs(
                Dictionary.getConcept(Dictionary.FAMILY_PLANNING_METHODS), Arrays.asList(patient.getPatientId()),
                context);
        Obs fmObs = EmrCalculationUtils.obsResultForPatient(fplanning, patient.getPatientId());
        if (fmObs != null) {
            patientSummary.put("familyProtection", familyPlanningMethods(fmObs.getValueCoded()));
        } else {
            patientSummary.put("familyProtection", "");
        }

        // transfer in date
        CalculationResult transferInResults = EmrCalculationUtils.evaluateForPatient(TransferInDateCalculation.class,
                null, patient);
        if (transferInResults.isEmpty()) {
            patientSummary.put("transferInDate", "N/A");
        } else {
            patientSummary.put("transferInDate", formatDate((Date) transferInResults.getValue()));
        }

        // facility transferred form
        CalculationResultMap transferInFacilty = Calculations.lastObs(
                Dictionary.getConcept(Dictionary.TRANSFER_FROM_FACILITY), Arrays.asList(patient.getPatientId()),
                context);
        Obs faciltyObs = EmrCalculationUtils.obsResultForPatient(transferInFacilty, patient.getPatientId());
        if (faciltyObs != null) {
            patientSummary.put("transferInFacility", faciltyObs.getValueText());
        } else {
            patientSummary.put("transferInFacility", "N/A");
        }

        // patient entry point
        CalculationResultMap entryPointMap = Calculations.firstObs(
                Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT), Arrays.asList(patient.getPatientId()), context);
        Obs entryPointObs = EmrCalculationUtils.obsResultForPatient(entryPointMap, patient.getPatientId());
        if (entryPointObs != null) {
            patientSummary.put("patientEntryPoint", entryPointAbbriviations(entryPointObs.getValueCoded()));
            patientSummary.put("patientEntryPointDate", formatDate(entryPointObs.getObsDatetime()));
        } else {
            patientSummary.put("patientEntryPoint", "");
            patientSummary.put("patientEntryPointDate", "");
        }

        // treatment suppoter details
        CalculationResultMap treatmentSupporterName = Calculations.lastObs(
                Dictionary.getConcept(Dictionary.TREATMENT_SUPPORTER_NAME), Arrays.asList(patient.getPatientId()),
                context);
        CalculationResultMap treatmentSupporterRelation = Calculations.lastObs(
                Dictionary.getConcept(Dictionary.TREATMENT_SUPPORTER_RELATION), Arrays.asList(patient.getPatientId()),
                context);
        CalculationResultMap treatmentSupporterContacts = Calculations.lastObs(
                Dictionary.getConcept(Dictionary.TREATMENT_SUPPORTER_CONTACTS), Arrays.asList(patient.getPatientId()),
                context);

        Obs treatmentSupporterNameObs = EmrCalculationUtils.obsResultForPatient(treatmentSupporterName,
                patient.getPatientId());
        Obs treatmentSupporterRelationObs = EmrCalculationUtils.obsResultForPatient(treatmentSupporterRelation,
                patient.getPatientId());
        Obs treatmentSupporterContactsObs = EmrCalculationUtils.obsResultForPatient(treatmentSupporterContacts,
                patient.getPatientId());
        if (treatmentSupporterNameObs != null) {
            patientSummary.put("nameOfTreatmentSupporter", treatmentSupporterNameObs.getValueText());
        } else {
            patientSummary.put("nameOfTreatmentSupporter", "N/A");
        }

        if (treatmentSupporterRelationObs != null) {
            patientSummary.put("relationshipToTreatmentSupporter",
                    treatmentSupporterRelationObs.getValueCoded().getName().getName());
        } else {
            patientSummary.put("relationshipToTreatmentSupporter", "N/A");
        }

        if (treatmentSupporterContactsObs != null) {
            patientSummary.put("contactOfTreatmentSupporter", treatmentSupporterContactsObs.getValueText());
        } else {
            patientSummary.put("contactOfTreatmentSupporter", "N/A");
        }

        // TB Start date
        CalculationResultMap tbConfirmation = Calculations.firstObs(
                Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE),
                Arrays.asList(patient.getPatientId()), context);
        Obs tbDateConfirmed = EmrCalculationUtils.obsResultForPatient(tbConfirmation, patient.getPatientId());
        if (tbDateConfirmed != null) {
            patientSummary.put("dateEnrolledInTb", formatDate(tbDateConfirmed.getObsDatetime()));
        } else {
            patientSummary.put("dateEnrolledInTb", "None");
        }

        // TB completion
        CalculationResultMap tbEndDate = Calculations.firstObs(Dictionary.getConcept(Dictionary.TB_END_DATE),
                Arrays.asList(patient.getId()), context);
        Obs tbEndDateValue = EmrCalculationUtils.obsResultForPatient(tbEndDate, patient.getPatientId());
        if (tbEndDateValue != null) {
            patientSummary.put("dateCompletedInTb", formatDate(tbEndDateValue.getObsDatetime()));
        } else {
            patientSummary.put("dateCompletedInTb", "None");
        }

        /// TB Screening
        CalculationResultMap tbMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.TB_SCREENING),
                Arrays.asList(patient.getPatientId()), context);
        Obs tbObs = EmrCalculationUtils.obsResultForPatient(tbMap, patient.getPatientId());
        if (tbObs != null) {
            patientSummary.put("tbScreeningOutcome", tbObs.getValueCoded().getName().getName());
        } else {
            patientSummary.put("tbScreeningOutcome", "N/A");

        }

        // chronicDisease
        CalculationResultMap chronicIllness = Calculations.allObs(Dictionary.getConcept(Dictionary.CHRONIC_ILLNESS),
                Arrays.asList(patient.getPatientId()), context);
        ListResult chronicIllnessResults = (ListResult) chronicIllness.get(patient.getPatientId());
        List<Obs> listOfChronicIllness = CalculationUtils.extractResultValues(chronicIllnessResults);
        String chronicDisease = "";
        if (listOfChronicIllness.size() == 0) {
            patientSummary.put("chronicDisease", "None");
        } else if (listOfChronicIllness.size() == 1) {
            patientSummary.put("chronicDisease", listOfChronicIllness.get(0).getValueCoded().getName().getName());
        } else {
            for (Obs obs : listOfChronicIllness) {
                if (obs != null) {
                    chronicDisease += obs.getValueCoded().getName().getName() + " ";
                }
            }
            patientSummary.put("chronicDisease", chronicDisease);
        }

        // allergies
        CalculationResultMap alergies = Calculations.allObs(Dictionary.getConcept(Dictionary.ALLERGIES),
                Arrays.asList(patient.getPatientId()), context);
        ListResult allergyResults = (ListResult) alergies.get(patient.getPatientId());
        List<Obs> listOfAllergies = CalculationUtils.extractResultValues(allergyResults);
        String allergies = "";
        if (listOfAllergies.size() == 0) {
            patientSummary.put("allergies", "None");
        } else if (listOfAllergies.size() == 1) {
            patientSummary.put("allergies", listOfAllergies.get(0).getValueCoded().getName().getName());
        } else {
            for (Obs obs : listOfAllergies) {
                if (obs != null) {
                    allergies += obs.getValueCoded().getName().getName()+" ";
                }
            }
            patientSummary.put("allergies", allergies);
        }

        //previous art details
        CalculationResultMap previousArt = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREVIOUS_ON_ART), Arrays.asList(patient.getPatientId()), context);
        Obs previousArtObs = EmrCalculationUtils.obsResultForPatient(previousArt,patient.getPatientId());
        if (previousArtObs != null && previousArtObs.getValueCoded() != null &&  previousArtObs.getValueCoded().getConceptId() == 1 &&  previousArtObs.getVoided().equals(false)) {
            patientSummary.put("previousArtStatus","Yes");
        } else if (previousArtObs != null && previousArtObs.getValueCoded() != null &&  previousArtObs.getValueCoded().getConceptId() == 2 &&  previousArtObs.getVoided().equals(false)) {
            patientSummary.put("previousArtStatus", "No");
        } else {
            patientSummary.put("previousArtStatus", "None");
        }

        //set the purpose for previous art
        CalculationResultMap previousArtPurposePmtct = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREVIOUS_ON_ART_PURPOSE_PMTCT), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap previousArtPurposePep = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREVIOUS_ON_ART_PURPOSE_PEP), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap previousArtPurposeHaart = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREVIOUS_ON_ART_PURPOSE_HAART), Arrays.asList(patient.getPatientId()), context);
        Obs previousArtPurposePmtctObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposePmtct, patient.getPatientId());
        Obs previousArtPurposePepObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposePep, patient.getPatientId());
        Obs previousArtPurposeHaartObs = EmrCalculationUtils.obsResultForPatient(previousArtPurposeHaart, patient.getPatientId());
        String purposeString = "";
        if(patientSummary.get("previousArtStatus").equals("None") || patientSummary.get("previousArtStatus").equals("No")){
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

        patientSummary.put("artPurpose", purposeString);

        //art start date
        CalculationResult artStartDateResults = EmrCalculationUtils.evaluateForPatient(InitialArtStartDateCalculation.class, null, patient);
        if(artStartDateResults != null) {
            patientSummary.put("dateStartedArt", formatDate((Date) artStartDateResults.getValue()));
        }
        else {
            patientSummary.put("dateStartedArt", "");
        }

        //Clinical stage at art start
        CalculationResult whoStageAtArtStartResults = EmrCalculationUtils.evaluateForPatient(WhoStageAtArtStartCalculation.class, null,patient);
        if(whoStageAtArtStartResults != null){
            patientSummary.put("whoStageAtArtStart", intergerToRoman(whoStageAtArtStartResults.getValue().toString()));
        }
        else {
            patientSummary.put("whoStageAtArtStart", "");
        }

        //cd4 at art initiation
        CalculationResult cd4AtArtStartResults = EmrCalculationUtils.evaluateForPatient(CD4AtARTInitiationCalculation.class, null,patient);
        if(cd4AtArtStartResults != null){
            patientSummary.put("cd4AtArtStart", cd4AtArtStartResults.getValue().toString());
        }
        else {
            patientSummary.put("cd4AtArtStart", "");
        }

        //bmi
        CalculationResult bmiResults = EmrCalculationUtils.evaluateForPatient(BMICalculation.class, null,patient);
        if(bmiResults != null){
            patientSummary.put("bmi", bmiResults.getValue().toString());
        }
        else {
            patientSummary.put("bmi", "");
        }

        //first regimen for the patient
        Encounter firstEnc = EncounterBasedRegimenUtils.getFirstEncounterForCategory(patient, "ARV");
        if(firstEnc != null) {
            patientSummary.put("firstRegimen", EncounterBasedRegimenUtils.buildRegimenChangeObject(firstEnc.getObs(), firstEnc));
        }

        //previous drugs/regimens and dates
        String regimens = "";
        String regimenDates = "";
        CalculationResultMap pmtctRegimenHivEnroll = Calculations.lastObs(Dictionary.getConcept(Dictionary.PMTCT_REGIMEN_HIV_ENROLL), Arrays.asList(patient.getPatientId()), context);
        CalculationResultMap pepAndHaartRegimenHivEnroll = Calculations.allObs(Dictionary.getConcept(Dictionary.PEP_REGIMEN_HIV_ENROLL), Arrays.asList(patient.getPatientId()), context);

        Obs obsPmtctHivEnroll = EmrCalculationUtils.obsResultForPatient(pmtctRegimenHivEnroll, patient.getPatientId());

        ListResult listResults = (ListResult) pepAndHaartRegimenHivEnroll.get(patient.getPatientId());
        List<Obs> pepAndHaartRegimenObsList = CalculationUtils.extractResultValues(listResults);
        if(patientSummary.get("previousArtStatus").equals("None") || patientSummary.get("previousArtStatus").equals("No")){
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
        patientSummary.put("iosResults", iosResults);

        //current art regimen
        Encounter lastEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");
        if(lastEnc != null) {
            patientSummary.put("currentArtRegimen", EncounterBasedRegimenUtils.buildRegimenChangeObject(lastEnc.getObs(), lastEnc));
        }

        //current who staging
        CalculationResult currentWhoStaging = EmrCalculationUtils.evaluateForPatient(LastWhoStageCalculation.class, null, patient);
        if(currentWhoStaging != null){
            patientSummary.put("currentWhoStaging", whoStaging(((Obs) currentWhoStaging.getValue()).getValueCoded()));
        }
        else {
            patientSummary.put("currentWhoStaging", "");
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
            patientSummary.put("ctxValue", ctxValue);
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))){
            patientSummary.put("ctxValue", "Yes");
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NO))){
            patientSummary.put("ctxValue", "No");
        }
        else if(medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))){
            patientSummary.put("ctxValue", "N/A");
        }
        else {
            patientSummary.put("ctxValue", "No");
        }

        //Find if a patient is on dapsone
        CalculationResultMap medOrdersMapDapsone = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), Arrays.asList(patient.getPatientId()), context);
        Obs medOrdersMapObsDapsone = EmrCalculationUtils.obsResultForPatient(medOrdersMapDapsone, patient.getPatientId());
        if(medOrdersMapObsDapsone != null && medOrdersMapObsDapsone.getValueCoded().equals(Dictionary.getConcept(Dictionary.DAPSONE))){
            patientSummary.put("dapsone", "Yes");
        }
        else if(medOrdersMapObsDapsone != null && medOrdersMapObsDapsone.getValueCoded().equals(Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM)) || medicationDispensedCtxObs != null && medicationDispensedCtxObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))){
            patientSummary.put("dapsone", "No");
        }
        else {
            patientSummary.put("dapsone", "No");
        }

        //on IPT
        CalculationResultMap medOrdersMapInh = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), Arrays.asList(patient.getPatientId()), context);
        Obs medOrdersMapObsInh = EmrCalculationUtils.obsResultForPatient(medOrdersMapInh, patient.getPatientId());
        CalculationResultMap medicationDispensedIpt = Calculations.lastObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), Arrays.asList(patient.getPatientId()), context);
        Obs medicationDispensedIptObs = EmrCalculationUtils.obsResultForPatient(medicationDispensedIpt, patient.getPatientId());
        if(medOrdersMapObsInh != null && medOrdersMapObsInh.getValueCoded().equals(Dictionary.getConcept(Dictionary.ISONIAZID))){
            patientSummary.put("onIpt", "Yes");
        }
        else if(medicationDispensedIptObs != null && medicationDispensedIptObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))){
            patientSummary.put("onIpt", "Yes");
        }
        else if(medicationDispensedIptObs != null && medicationDispensedIptObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NO))){
            patientSummary.put("onIpt", "No");
        }
        else if(medicationDispensedIptObs != null && medicationDispensedIptObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))){
            patientSummary.put("onIpt", "N/A");
        }
        else {
            patientSummary.put("onIpt", "No");
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
        patientSummary.put("clinicsEnrolled", clinicValues);

        //most recent cd4
        CalculationResult cd4Results = EmrCalculationUtils.evaluateForPatient(LastCd4CountDateCalculation.class, null, patient);
        if(cd4Results != null && cd4Results.getValue() != null){
            patientSummary.put("mostRecentCd4",((Obs) cd4Results.getValue()).getValueNumeric().toString());
            patientSummary.put("mostRecentCd4Date", formatDate(((Obs) cd4Results.getValue()).getObsDatetime()));
        }
        else{
            patientSummary.put("mostRecentCd4", "");
            patientSummary.put("mostRecentCd4Date", "");
        }



        // find deceased date
        CalculationResult deadResults = EmrCalculationUtils.evaluateForPatient(DateOfDeathCalculation.class, null, patient);
        if(deadResults.isEmpty()){
            patientSummary.put("deathDate", "N/A");
        }
        else {
            patientSummary.put("deathDate", formatDate((Date) deadResults.getValue()));
        }

        // next appointment date
        CalculationResult returnVisitResults = EmrCalculationUtils.evaluateForPatient(LastReturnVisitDateCalculation.class, null, patient);
        if(returnVisitResults != null){
            patientSummary.put("nextAppointmentDate", formatDate((Date) returnVisitResults.getValue()));
        }
        else {
            patientSummary.put("nextAppointmentDate", "");
        }

        // transfer out date
        CalculationResult totResults = EmrCalculationUtils.evaluateForPatient(TransferOutDateCalculation.class, null, patient);
        if(totResults.isEmpty()){
            patientSummary.put("transferOutDate", "N/A");
        }
        else {
            patientSummary.put("transferOutDate", formatDate((Date) totResults.getValue()));
        }

        //transfer out to facility
        CalculationResultMap transferOutFacilty = Calculations.lastObs(Dictionary.getConcept(Dictionary.TRANSFER_OUT_FACILITY), Arrays.asList(patient.getPatientId()), context);
        Obs transferOutFacilityObs = EmrCalculationUtils.obsResultForPatient(transferOutFacilty, patient.getPatientId());
        if(transferOutFacilityObs != null){
            patientSummary.put("transferOutFacility", transferOutFacilityObs.getValueText());
        }
        else {
            patientSummary.put("transferOutFacility", "N/A");
        }

        //All  Vl
        CalculationResult allVlResults = EmrCalculationUtils.evaluateForPatient(AllVlCountCalculation.class, null, patient);
        patientSummary.put("allVlResults", allVlResults);
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
                patientSummary.put("viralLoadValue", splitByEqualSign[0]);

                //for a date from a string
                String dateSplitedBySpace = splitByEqualSign[1].split(" ")[0].trim();
                String yearPart = dateSplitedBySpace.split("-")[0].trim();
                String monthPart = dateSplitedBySpace.split("-")[1].trim();
                String dayPart = dateSplitedBySpace.split("-")[2].trim();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(yearPart));
                calendar.set(Calendar.MONTH, Integer.parseInt(monthPart) - 1);
                calendar.set(Calendar.DATE, Integer.parseInt(dayPart));

                patientSummary.put("viralLoadDate", formatDate(calendar.getTime()));
            }
        }

        //All CD4 Count
        CalculationResult allCd4CountResults = EmrCalculationUtils.evaluateForPatient(AllCd4CountCalculation.class, null, patient);
        patientSummary.put("allCd4CountResults", allCd4CountResults.getValue());

        return patientSummary;

    }

    @RequestMapping(method = RequestMethod.GET, value = "/relationship")
    @ResponseBody
    public String getRelationship(@RequestParam("patientUuid") String patientUuid, @RequestParam("relationshipType") String relationshipType) {
        String personRelationshipName = "";
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        List<Person> people = new ArrayList<Person>();
        for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
            if (relationship.getRelationshipType().getaIsToB().equals("Parent")) {
                if (relationship.getPersonA().getGender().equals("F") && relationshipType.equalsIgnoreCase("Mother")) {
                    people.add(relationship.getPersonA());
                }
                if (relationship.getPersonA().getGender().equals("M") && relationshipType.equalsIgnoreCase("Father")) {
                    people.add(relationship.getPersonA());
                }
            } else {
                if (relationship.getRelationshipType().getaIsToB().equalsIgnoreCase(relationshipType)) {
                    people.add(relationship.getPersonA());
                }
            }
        }

        if(people.size() > 0) {
            personRelationshipName = people.get(0).getPersonName().getFullName();
        }
        return  personRelationshipName;
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

    String cacxScreeningOutcome(Concept concept) {
        String value = "";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept(Dictionary.POSITIVE))) {
                value = "Positive";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.NEGATIVE))) {
                value = "Negative";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.YES))) {
                value = "Yes";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.NO))) {
                value = "No";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.UNKNOWN))) {
                value = "Unknown";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.NOT_DONE))) {
                value = "Not Done";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))) {
                value = "N/A";
            }
        }
        return value;
    }

    String familyPlanningMethods(Concept concept) {
        String value = "Other";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept(Dictionary.INJECTABLE_HORMONES))) {
                value = "INJECTABLE HORMONES";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.CERVICAL_CAP))) {
                value = "CERVICAL CAP";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.INTRAUTERINE_DEVICE))) {
                value = "Intrauterine device";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.FEMALE_STERILIZATION))) {
                value = "Female sterilization";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.CONDOMS))) {
                value = "Condoms";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.BIRTH_CONTROL_PILLS))) {
                value = "Birth control pills";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.NATURAL_FAMILY_PLANNING))) {
                value = "Natural family planning";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.SEXUAL_ABSTINENCE))) {
                value = "Sexual abstinence";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.LEVONORGESTREL))) {
                value = "LEVONORGESTREL";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))) {
                value = "Not applicable";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.TUBUL_LIGATION_PROCEDURE))) {
                value = "Tubal ligation procedure";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.MEDROXYPROGESTERONE_ACETATE))) {
                value = "MEDROXYPROGESTERONE ACETATE";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.VASECTOMY))) {
                value = "Vasectomy";
            } else if (concept.equals(Dictionary.getConcept(Dictionary.IMPLANTABLE_CONTRACEPTIVE))) {
                value = "NORPLANT (IMPLANTABLE CONTRACEPTIVE)";

            } else if (concept.equals(Dictionary.getConcept(Dictionary.NONE))) {
                value = "None";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.IUD_CONTRACEPTION))) {
                value = "IUD Contraception";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.HYSTERECTOMY))) {
                value = "Hysterectomy";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.EMERGENCY_CONTRACEPTIVE_PILLS))) {
                value = "Emergency contraceptive pills";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.LACTATIONAL_AMENORRHEA))) {
                value = "Lactational amenorrhea";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.UNKNOWN))) {
                value = "Unknown";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.CONTRACEPTION_METHOD_UNDECIDED))) {
                value = "Contraception method undecided";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.FEMALE_CONDOM))) {
                value = "Female condom";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.MALE_CONDOM))) {
                value = "Male condom";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.IMPLANTABLE_CONTRACEPTIVE))) {
                value = "Implantable contraceptive (unspecified type)";
            }

        }

        return value;
    }

    String stiScreeningOutcome(Concept concept) {
        String value = "";
        if(concept != null) {
            if (concept.equals(Dictionary.getConcept(Dictionary.POSITIVE))) {
                value = "Positive";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.NEGATIVE))) {
                value = "Negative";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.NOT_DONE))) {
                value = "Not Done";
            }
            else if (concept.equals(Dictionary.getConcept(Dictionary.NOT_APPLICABLE))) {
                value = "N/A";
            }
        }
        return value;
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

    private int age(Date d1, Date d2){
        DateTime birthDate = new DateTime(d1.getTime());
        DateTime today = new DateTime(d2.getTime());

        return Math.abs(Years.yearsBetween(today, birthDate).getYears());
    }

    private String formatDate(Date date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }

    private String mapConceptNamesToShortNames(String conceptUuid) {

        if (conceptUuid.equalsIgnoreCase("84797AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "TDF";
        } else if (conceptUuid.equalsIgnoreCase("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "D4T";
        } else if (conceptUuid.equalsIgnoreCase("86663AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "AZT";
        } else if (conceptUuid.equalsIgnoreCase("78643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "3TC";
        } else if (conceptUuid.equalsIgnoreCase("70057AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "ABC";
        } else if (conceptUuid.equalsIgnoreCase("75628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "FTC";
        } else if (conceptUuid.equalsIgnoreCase("74807AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "DDI";
        } else if (conceptUuid.equalsIgnoreCase("80586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "NVP";
        } else if (conceptUuid.equalsIgnoreCase("75523AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "EFV";
        } else if (conceptUuid.equalsIgnoreCase("794AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "LPV";
        } else if (conceptUuid.equalsIgnoreCase("83412AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "RTV";
        } else if (conceptUuid.equalsIgnoreCase("71648AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "ATV";
        } else if (conceptUuid.equalsIgnoreCase("159810AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "ETR";
        } else if (conceptUuid.equalsIgnoreCase("154378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "RAL";
        } else if (conceptUuid.equalsIgnoreCase("74258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "DRV";
        } else if (conceptUuid.equalsIgnoreCase("d1fd0e18-e0b9-46ae-ac0e-0452a927a94b")) {
            name = "DTG";
        } else if (conceptUuid.equalsIgnoreCase("167205AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            name = "B";
        }

        return name;

    }
}
