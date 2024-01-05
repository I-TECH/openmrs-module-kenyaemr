/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.advice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentKind;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.appointments.util.DateUtil;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.springframework.aop.AfterReturningAdvice;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.api.PatientService;
import org.openmrs.Patient;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Synchronizes appointments documented in HTML forms with Bahmni appointments module
 * Invoked after saving HFE forms
 */
public class SyncHFEAppointmentsWithBahmniModule implements AfterReturningAdvice {

    private Log log = LogFactory.getLog(this.getClass());

    // HIV appointments
    public static final String NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID = "5096AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String NEXT_DRUG_REFILL_APPOINTMENT_DATE_CONCEPT_UUID = "162549AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    public static final String APPOINTMENT_REASON_CONCEPT_UUID = "160288AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    public static final String HIV_FOLLOWUP_SERVICE = "885b4ad3-fd4c-4a16-8ed3-08813e6b01fa";
    public static final String DRUG_REFILL_SERVICE = "a96921a1-b89e-4dd2-b6b4-7310f13bbabe";
    public static final String HIV_LAB_TEST_SERVICE = "61488cf6-fad4-11ed-be56-0242ac120002";
    public static final String COUNSELLING_SERVICE = "c6ce2119-c084-49c7-aa3f-be9fa1f3863e";

    // MCH appointments
    public static final String MCH_POSTNATAL_VISIT_SERVICE = "dcde8ca4-32a5-4c67-9982-33346e39813f";
    public static final String MCH_ANTENATAL_VISIT_SERVICE = "372eed95-6493-490a-8891-8374fe566aeb";
    public static final String CWC_FOLLOWUP_SERVICE = "b696161d-3755-4ff0-94a9-91f0148c18ab";

    // Prep appointments
    public static final String PREP_INITIAL_SERVICE = "242f74b9-b0a3-4ba6-9be3-8f57591e3dff";
    public static final String PREP_FOLLOWUP_SERVICE = "6f9b19f6-ac25-41f9-a75c-b8b125dec3da";
    public static final String PREP_MONTHLY_REFILL_SERVICE = "b8c3efd9-e106-4409-ae0e-b9c651484a20";
    public static final String PREP_INITIAL_FORM = "1bfb09fc-56d7-4108-bd59-b2765fd312b8";
    public static final String PREP_MONTHLY_REFILL_FORM = "291c03c8-a216-11e9-a2a3-2a2ae2dbcce4";
    public static final String PREP_FOLLOWUP_FORM = "ee3e2017-52c0-4a54-99ab-ebb542fb8984";

    // Tb appointments
    public static final String TB_SERVICE = "e4737031-7e3b-4c63-a929-588613b2c832";

    // KP appointments
    public static final String KP_CLINICAL_SERVICE = "b1b75503-5175-433c-a25d-763bc9650ebd";
    public static final String KP_CLINICAL_VISIT_FORM = "92e041ac-9686-11e9-bc42-526af7764f64";


    AppointmentsService appointmentsService = Context.getService(AppointmentsService.class);
    AppointmentServiceDefinitionService appointmentServiceDefinitionService  = Context.getService(AppointmentServiceDefinitionService.class);
    ObsService obsService = Context.getObsService();
    ConceptService conceptService = Context.getConceptService();
    PersonService personService = Context.getPersonService();
    boolean followUpAppointment = false;
    boolean nxtAppointment = false;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date nextApptDate = null;
    Date refillApptDate = null;
    Integer appointmentReason = null;

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        if (method.getName().equals("saveEncounter")) { // handles both create and edit
            Encounter enc = (Encounter) args[0];
            List<String> appointmentForms = Arrays.asList(HivMetadata._Form.MOH_257_VISIT_SUMMARY, HivMetadata._Form.HIV_GREEN_CARD, MchMetadata._Form.MCHMS_ANTENATAL_VISIT, MchMetadata._Form.MCHCS_FOLLOW_UP, MchMetadata._Form.MCHMS_POSTNATAL_VISIT,  PREP_FOLLOWUP_FORM, PREP_INITIAL_FORM, PREP_MONTHLY_REFILL_FORM, KP_CLINICAL_VISIT_FORM, TbMetadata._Form.TB_FOLLOW_UP, HivMetadata._Form.FAST_TRACK );

            if (enc != null && enc.getForm() != null && appointmentForms.contains(enc.getForm().getUuid())) {
                Appointment editAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());

                if(enc.getVoided() == true && editAppointment != null && enc.getForm() != null){
                    // Get appointment obs
                    Appointment hivFollowUpAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());
                    Appointment drugRefillAppointment = hivFollowUpAppointment.getRelatedAppointment();

                    // delete HIV followup appointment
                    if(hivFollowUpAppointment != null ) {
                        nxtAppointment = true;
                        hivFollowUpAppointment.setVoided(true);
                        hivFollowUpAppointment.setDateVoided(new Date());
                        hivFollowUpAppointment.setVoidedBy(Context.getAuthenticatedUser());
                    }

                    if(nxtAppointment && followUpAppointment) {
                        Appointment app = appointmentsService.validateAndSave(hivFollowUpAppointment);
                    }

                    // delete existing appointment for drug refill
                    if(drugRefillAppointment != null) {
                        drugRefillAppointment.setVoided(true);
                        drugRefillAppointment.setDateVoided(new Date());
                        drugRefillAppointment.setVoidedBy(Context.getAuthenticatedUser());
                        Appointment app = appointmentsService.validateAndSave(drugRefillAppointment);
                    }

                } else if (editAppointment != null && enc.getForm() != null &&
                        (enc.getForm().getUuid().equals(HivMetadata._Form.HIV_GREEN_CARD) || enc.getForm().getUuid().equals(HivMetadata._Form.MOH_257_VISIT_SUMMARY))) {
                    // pick HIV followup forms
                    processEditHivFollowupEncounter(enc);

                } else if (enc != null && enc.getForm() != null &&
                        (enc.getForm().getUuid().equals(HivMetadata._Form.HIV_GREEN_CARD) || enc.getForm().getUuid().equals(HivMetadata._Form.MOH_257_VISIT_SUMMARY))) {
                    // pick HIV followup forms
                    processCreateHivFollowupEncounter(enc);

                } else if(enc != null && enc.getForm() != null &&
                        (enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_ANTENATAL_VISIT) ||
                                enc.getForm().getUuid().equals(MchMetadata._Form.MCHCS_FOLLOW_UP) ||
                                enc.getForm().getUuid().equals(PREP_FOLLOWUP_FORM) ||
                                enc.getForm().getUuid().equals(PREP_INITIAL_FORM) ||
                                enc.getForm().getUuid().equals(PREP_MONTHLY_REFILL_FORM) ||
                                enc.getForm().getUuid().equals(KP_CLINICAL_VISIT_FORM) ||
                                enc.getForm().getUuid().equals(TbMetadata._Form.TB_FOLLOW_UP) ||
                                enc.getForm().getUuid().equals(HivMetadata._Form.FAST_TRACK) )) {

                    processProgramAppointments(enc);
                } else if(enc != null && (enc.getForm() != null && (enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_POSTNATAL_VISIT) || enc.getForm() != null && enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_DELIVERY)))) {

                    processMCHEncounter(enc);
                }
            }
        }

    }

    /**
     * Edit HIV appointments ie HIV consultation, Drug refill and lab
     */
    private void processEditHivFollowupEncounter(Encounter enc) throws Throwable {
        // Get appointment obs
        Appointment hivFollowUpAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());
        Appointment drugRefillAppointment = hivFollowUpAppointment.getRelatedAppointment();
        Integer appointmentReasonToEdit = null;

        List<Obs> obs = obsService.getObservations(
                Arrays.asList(personService.getPerson(enc.getPatient().getPersonId())),
                Arrays.asList(enc),
                Arrays.asList(
                        conceptService.getConceptByUuid(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID),
                        conceptService.getConceptByUuid(NEXT_DRUG_REFILL_APPOINTMENT_DATE_CONCEPT_UUID),
                        conceptService.getConceptByUuid(APPOINTMENT_REASON_CONCEPT_UUID)
                ),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        for (Obs o : obs) { // Loop through the obs and compose Appointment object for Bahmni
            if((o.getConcept().getUuid().equals(APPOINTMENT_REASON_CONCEPT_UUID)) && hivFollowUpAppointment != null ) {
                appointmentReasonToEdit = o.getValueCoded().getConceptId();
                 AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();
                if(appointmentReasonToEdit != null) {
                    // Allow for editing of appointment service based on updated appointment reasons from green card
                    String serviceUuid = getAppointmentServiceUuidFromConcept(appointmentReasonToEdit);
                    if(serviceUuid != null) {
                        appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(serviceUuid).getId());
                        hivFollowUpAppointment.setService(appointmentServiceDefinition);
                    }  
                }
                if(o.getValueCoded().getConceptId() == 160523 || o.getValueCoded().getConceptId() == 160521 ) {
                    followUpAppointment = true;
                }
            }

            // edit HIV followup appointment
            if((o.getConcept().getUuid().equals(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID)) && hivFollowUpAppointment != null  ) {
                nxtAppointment = true;
                Date nextApptStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                Date nextApptEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                hivFollowUpAppointment.setStartDateTime(nextApptStartDateTime);
                hivFollowUpAppointment.setEndDateTime(nextApptEndDateTime);
                if(enc.getVoided() == true) {
                    hivFollowUpAppointment.setVoided(true);
                    hivFollowUpAppointment.setDateVoided(new Date());
                    hivFollowUpAppointment.setVoidedBy(Context.getAuthenticatedUser());
                }
            }

            if(nxtAppointment && followUpAppointment) {
                Appointment app = appointmentsService.validateAndSave(hivFollowUpAppointment);
            }

            // edit existing appointment for drug refill
            if((o.getConcept().getUuid().equals(NEXT_DRUG_REFILL_APPOINTMENT_DATE_CONCEPT_UUID)) && drugRefillAppointment != null) {
                Date refillStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                Date refillEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                drugRefillAppointment.setStartDateTime(refillStartDateTime);
                drugRefillAppointment.setEndDateTime(refillEndDateTime);
                drugRefillAppointment.setVoided(enc.getVoided());
                if(enc.getVoided() == true) {
                    drugRefillAppointment.setVoided(true);
                    drugRefillAppointment.setDateVoided(new Date());
                    drugRefillAppointment.setVoidedBy(Context.getAuthenticatedUser());
                }
                Appointment app = appointmentsService.validateAndSave(drugRefillAppointment);
            }

            //create new refill appointment if added on editing followup
            if((o.getConcept().getUuid().equals(NEXT_DRUG_REFILL_APPOINTMENT_DATE_CONCEPT_UUID)) && drugRefillAppointment == null &&
                    appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE) != null) {
                AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();
                appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE).getId());

                Appointment editedFollowUpAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());
                Appointment refillAppointment = new Appointment();
                Date refillStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                Date refillEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                refillAppointment.setRelatedAppointment(editedFollowUpAppointment);
                refillAppointment.setPatient(enc.getPatient());
                refillAppointment.setService(appointmentServiceDefinition);
                refillAppointment.setStartDateTime(refillStartDateTime);
                refillAppointment.setEndDateTime(refillEndDateTime);
                refillAppointment.setLocation(enc.getLocation());
                refillAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                refillAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                Appointment app = appointmentsService.validateAndSave(refillAppointment);
                
                if (editedFollowUpAppointment != null) {
                    editedFollowUpAppointment.setRelatedAppointment(app);
                    Appointment app2 = appointmentsService.validateAndSave(editedFollowUpAppointment);
                }
            }

        }
    }

    /**
     * Create HIV appointments ie HIV consultation, Drug refill and lab
     */
    private void processCreateHivFollowupEncounter(Encounter enc) throws Throwable {
        Appointment nextAppointment = new Appointment();

        // Get appointment obs
        List<Obs> obs = obsService.getObservations(
                Arrays.asList(personService.getPerson(enc.getPatient().getPersonId())),
                Arrays.asList(enc),
                Arrays.asList(
                        conceptService.getConceptByUuid(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID),
                        conceptService.getConceptByUuid(NEXT_DRUG_REFILL_APPOINTMENT_DATE_CONCEPT_UUID),
                        conceptService.getConceptByUuid(APPOINTMENT_REASON_CONCEPT_UUID)
                ),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        for (Obs o : obs) { // Loop through the obs and compose Appointment object for Bahmni
            if (o.getConcept().getUuid().equals(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID)) {
                nextApptDate = o.getValueDatetime();
            }
            if (o.getConcept().getUuid().equals(APPOINTMENT_REASON_CONCEPT_UUID)) {
                appointmentReason = o.getValueCoded().getConceptId();
            }
            if (o.getConcept().getUuid().equals(NEXT_DRUG_REFILL_APPOINTMENT_DATE_CONCEPT_UUID)) {
                refillApptDate = o.getValueDatetime();
            }

            if((appointmentReason != null && appointmentReason == 160523) || (appointmentReason != null && appointmentReason == 160521) ) {
                AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();

                // create HIV followup appointment
                if(nextApptDate != null && appointmentServiceDefinitionService.getAppointmentServiceByUuid(HIV_FOLLOWUP_SERVICE) != null ) {

                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(HIV_FOLLOWUP_SERVICE).getId());
                    Date nextApptStartDateTime = DateUtil.convertToDate(dateFormat.format(nextApptDate).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                    Date nextApptEndDateTime = DateUtil.convertToDate(dateFormat.format(nextApptDate).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                    nextAppointment.setUuid(enc.getUuid());
                    nextAppointment.setPatient(enc.getPatient());
                    nextAppointment.setService(appointmentServiceDefinition);
                    nextAppointment.setStartDateTime(nextApptStartDateTime);
                    nextAppointment.setEndDateTime(nextApptEndDateTime);
                    nextAppointment.setLocation(enc.getLocation());
                    nextAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                    nextAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                    Appointment app = appointmentsService.validateAndSave(nextAppointment);
                }

                            // create appointment for drug refill
                if (refillApptDate != null &&  nextApptDate != null &&  appointmentReason == 160521 &&
                 appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE) != null ) {
                    AppointmentServiceDefinition refillApptServiceDefinition = new AppointmentServiceDefinition();
                    refillApptServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE).getId());
                    Appointment currentFollowUpAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());
                    Appointment refillAppointment = new Appointment();
                    Date refillStartDateTime = DateUtil.convertToDate(dateFormat.format(refillApptDate).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                    Date refillEndDateTime = DateUtil.convertToDate(dateFormat.format(refillApptDate).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                    refillAppointment.setRelatedAppointment(currentFollowUpAppointment);
                    refillAppointment.setPatient(enc.getPatient());
                    refillAppointment.setService(refillApptServiceDefinition);
                    refillAppointment.setStartDateTime(refillStartDateTime);
                    refillAppointment.setEndDateTime(refillEndDateTime);
                    refillAppointment.setLocation(enc.getLocation());
                    refillAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                    refillAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                    Appointment app = appointmentsService.validateAndSave(refillAppointment);

                    if (currentFollowUpAppointment != null ) {
                        currentFollowUpAppointment.setRelatedAppointment(app);
                        Appointment app2 = appointmentsService.validateAndSave(currentFollowUpAppointment);
                    }
                }

            } else if ( appointmentReason != null && appointmentReason == 1283) {
                // create lab tests appointment
                if ( appointmentServiceDefinitionService.getAppointmentServiceByUuid(HIV_LAB_TEST_SERVICE) != null && nextApptDate != null ) {
                    AppointmentServiceDefinition labApptServiceDefinition = new AppointmentServiceDefinition();
                    labApptServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(HIV_LAB_TEST_SERVICE).getId());
                    Date labApptStartDateTime = DateUtil.convertToDate(dateFormat.format(nextApptDate).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                    Date labApptEndDateTime = DateUtil.convertToDate(dateFormat.format(nextApptDate).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                    Appointment labAppointment = new Appointment();
                    labAppointment.setUuid(enc.getUuid());
                    labAppointment.setPatient(enc.getPatient());
                    labAppointment.setService(labApptServiceDefinition);
                    labAppointment.setStartDateTime(labApptStartDateTime);
                    labAppointment.setEndDateTime(labApptEndDateTime);
                    labAppointment.setLocation(enc.getLocation());
                    labAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                    labAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                    Appointment app = appointmentsService.validateAndSave(labAppointment);
                }  
            } 

        }

    }

    /**
     * Create and edit PREP or TB or KP appointments
     * Create MCH appointments
     */
    private void processProgramAppointments(Encounter enc) throws Throwable {
        // MCH or PREP or TB or KP appointment
        List<Obs> obs = obsService.getObservations(
                Arrays.asList(personService.getPerson(enc.getPatient().getPersonId())),
                Arrays.asList(enc),
                Arrays.asList(
                        conceptService.getConceptByUuid(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID)
                ),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        for (Obs o : obs) { // Loop through the obs and compose Appointment object for Bahmni
            AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();
            Appointment editAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());

            if(editAppointment != null) {
                //edit MCH or PREP or TB or KP appointment
                Date nextApptStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                Date nextApptEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                editAppointment.setStartDateTime(nextApptStartDateTime);
                editAppointment.setEndDateTime(nextApptEndDateTime);
                Appointment app = appointmentsService.validateAndSave(editAppointment);

            } else {
                // create MCH or TB or KP or PREP appointment
                 if (enc.getForm() != null && enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_ANTENATAL_VISIT) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_ANTENATAL_VISIT_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_ANTENATAL_VISIT_SERVICE).getId());
                } else if (enc.getForm() != null && enc.getForm().getUuid().equals(MchMetadata._Form.MCHCS_FOLLOW_UP) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(CWC_FOLLOWUP_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(CWC_FOLLOWUP_SERVICE).getId());
                } else if (enc.getForm() != null && enc.getForm().getUuid().equals(PREP_INITIAL_FORM) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(PREP_INITIAL_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(PREP_INITIAL_SERVICE).getId());
                } else if (enc.getForm() != null && enc.getForm().getUuid().equals(PREP_FOLLOWUP_FORM) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(PREP_FOLLOWUP_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(PREP_FOLLOWUP_SERVICE).getId());
                } else if (enc.getForm() != null && enc.getForm().getUuid().equals(PREP_MONTHLY_REFILL_FORM) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(PREP_MONTHLY_REFILL_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(PREP_MONTHLY_REFILL_SERVICE).getId());
                } else if (enc.getForm() != null && enc.getForm().getUuid().equals(TbMetadata._Form.TB_FOLLOW_UP) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(TB_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(TB_SERVICE).getId());
                } else if (enc.getForm() != null && enc.getForm().getUuid().equals(KP_CLINICAL_VISIT_FORM) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(KP_CLINICAL_SERVICE) != null) {
                     appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(KP_CLINICAL_SERVICE).getId());
                 } else if (enc.getForm() != null && enc.getForm().getUuid().equals(HivMetadata._Form.FAST_TRACK) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE).getId());
                } else {
                    return;
                }
                Appointment appointment = new Appointment();
                Date appointmentStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                Date appointmentEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                appointment.setUuid(enc.getUuid());
                appointment.setPatient(enc.getPatient());
                appointment.setService(appointmentServiceDefinition);
                appointment.setStartDateTime(appointmentStartDateTime);
                appointment.setEndDateTime(appointmentEndDateTime);
                appointment.setLocation(enc.getLocation());
                appointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                appointment.setAppointmentKind(AppointmentKind.Scheduled);
                Appointment app = appointmentsService.validateAndSave(appointment);

            }
        }
    }

    /**
     * Edit the mother and baby appointments which are related
     */
    private void processMCHEncounter(Encounter enc) throws Throwable {
        //MCH appointments
        List<Obs> obs = obsService.getObservations(
                Arrays.asList(personService.getPerson(enc.getPatient().getPersonId())),
                Arrays.asList(enc),
                Arrays.asList(
                         conceptService.getConceptByUuid(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID)
                ),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        for (Obs o : obs) { // Loop through the obs and compose Appointment object for Bahmni
            Date nextApptStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
            Date nextApptEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
            Appointment editAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());

            if (editAppointment != null) {
                AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();
                Appointment postNatalAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());
                Appointment cwcFollowUpAppointment = postNatalAppointment.getRelatedAppointment();

                if(postNatalAppointment != null) {
                    postNatalAppointment.setStartDateTime(nextApptStartDateTime);
                    postNatalAppointment.setEndDateTime(nextApptEndDateTime);
                    Appointment app = appointmentsService.validateAndSave(postNatalAppointment);
                }
                if(cwcFollowUpAppointment != null) {
                    cwcFollowUpAppointment.setStartDateTime(nextApptEndDateTime);
                    cwcFollowUpAppointment.setEndDateTime(nextApptEndDateTime);
                    Appointment app2 = appointmentsService.validateAndSave(cwcFollowUpAppointment);
                }
                if(postNatalAppointment != null && cwcFollowUpAppointment == null && appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_POSTNATAL_VISIT_SERVICE) != null ) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(CWC_FOLLOWUP_SERVICE).getId());
                    List<Person> children = EmrUtils.getPersonChildren(enc.getPatient());
                    for (Person child : children) {
                        int ageYears = child.getAge();
                        int id = child.getId();
                        PatientService patientService = Context.getPatientService();
                        Patient patient = patientService.getPatient(id);
                        // add mothers appointment for all children who are HEI
                        if (ageYears <= 2) {
                            Appointment followUpAppointment = new Appointment();
                            followUpAppointment.setPatient(patient);
                            followUpAppointment.setService(appointmentServiceDefinition);
                            followUpAppointment.setStartDateTime(nextApptStartDateTime);
                            followUpAppointment.setEndDateTime(nextApptEndDateTime);
                            followUpAppointment.setLocation(enc.getLocation());
                            followUpAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                            followUpAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                            cwcFollowUpAppointment.setRelatedAppointment(postNatalAppointment);
                            Appointment app3 = appointmentsService.validateAndSave(followUpAppointment);

                            postNatalAppointment.setRelatedAppointment(app3);
                            Appointment app4 = appointmentsService.validateAndSave(postNatalAppointment);
                        }
                    }
                }
            }

            // create MCH postnatal appointment
            if (editAppointment == null && enc.getForm() != null && (enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_POSTNATAL_VISIT) || enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_DELIVERY)) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_POSTNATAL_VISIT_SERVICE) != null) {
                Appointment mchPostnatalAppointment = new Appointment();

                if(enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_POSTNATAL_VISIT) || enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_DELIVERY)) {
                    nxtAppointment = true;
                    AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_POSTNATAL_VISIT_SERVICE).getId());

                    mchPostnatalAppointment.setUuid(enc.getUuid());
                    mchPostnatalAppointment.setPatient(enc.getPatient());
                    mchPostnatalAppointment.setService(appointmentServiceDefinition);
                    mchPostnatalAppointment.setStartDateTime(nextApptStartDateTime);
                    mchPostnatalAppointment.setEndDateTime(nextApptEndDateTime);
                    mchPostnatalAppointment.setLocation(enc.getLocation());
                    mchPostnatalAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                    mchPostnatalAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                }

                if(nxtAppointment) {
                    Appointment app = appointmentsService.validateAndSave(mchPostnatalAppointment);
                }
                if(appointmentServiceDefinitionService.getAppointmentServiceByUuid(CWC_FOLLOWUP_SERVICE) != null) {
                    // create CWC followup appointment for child
                    List<Person> children = EmrUtils.getPersonChildren(enc.getPatient());
                    for(Person child : children) {
                        int ageYears = child.getAge();
                        int id = child.getId();
                        PatientService patientService = Context.getPatientService();
                        Patient patient = patientService.getPatient(id);
                        if(ageYears <= 2) {
                            AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();
                            appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(CWC_FOLLOWUP_SERVICE).getId());
                            Appointment cwcFollowUpAppointment = new Appointment();
                            Appointment relatedAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());
                            cwcFollowUpAppointment.setRelatedAppointment(relatedAppointment);
                            cwcFollowUpAppointment.setPatient(patient);
                            cwcFollowUpAppointment.setService(appointmentServiceDefinition);
                            cwcFollowUpAppointment.setStartDateTime(nextApptStartDateTime);
                            cwcFollowUpAppointment.setEndDateTime(nextApptEndDateTime);
                            cwcFollowUpAppointment.setLocation(enc.getLocation());
                            cwcFollowUpAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                            cwcFollowUpAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                            Appointment app2 = appointmentsService.validateAndSave(cwcFollowUpAppointment);

                            relatedAppointment.setRelatedAppointment(app2);
                            Appointment app3 = appointmentsService.validateAndSave(relatedAppointment);
                        }
                    }
                }
            }

        }
    }
    
    private String getAppointmentServiceUuidFromConcept( int conceptId) {
        Map<Integer, String> idToUuidMap = new HashMap<Integer, String>();
        idToUuidMap.put(1283, "61488cf6-fad4-11ed-be56-0242ac120002"); // lab tests
        idToUuidMap.put(160523, "885b4ad3-fd4c-4a16-8ed3-08813e6b01fa"); // HIV consultation
        idToUuidMap.put(159382, "c6ce2119-c084-49c7-aa3f-be9fa1f3863e"); // Counselling
        return idToUuidMap.get(conceptId);
       
    }
}