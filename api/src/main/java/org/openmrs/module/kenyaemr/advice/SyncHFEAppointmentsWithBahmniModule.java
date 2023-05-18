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
import org.openmrs.module.appointments.model.AppointmentProvider;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.appointments.util.DateUtil;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.springframework.aop.AfterReturningAdvice;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        if (method.getName().equals("saveEncounter")) { // handles both create and edit
            Encounter enc = (Encounter) args[0];
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
                    (enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_DELIVERY) ||
                            enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_POSTNATAL_VISIT) ||
                            enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_ANTENATAL_VISIT) ||
                            enc.getForm().getUuid().equals(MchMetadata._Form.MCHCS_FOLLOW_UP) ||
                            enc.getForm().getUuid().equals(PREP_FOLLOWUP_FORM) ||
                            enc.getForm().getUuid().equals(PREP_INITIAL_FORM) ||
                            enc.getForm().getUuid().equals(PREP_MONTHLY_REFILL_FORM) ||
                            enc.getForm().getUuid().equals(KP_CLINICAL_VISIT_FORM) ||
                            enc.getForm().getUuid().equals(TbMetadata._Form.TB_FOLLOW_UP) )) {

                processProgramEncounter(enc);
            }
        }

    }

    private void processEditHivFollowupEncounter(Encounter enc) throws Throwable {
        // Get appointment obs
        Appointment hivFollowUpAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());
        Appointment drugRefillAppointment = hivFollowUpAppointment.getRelatedAppointment();

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
                if(o.getValueCoded().getConceptId() == 160523 || o.getValueCoded().getConceptId() == 160521 ) {
                    followUpAppointment = true;
                }
            }

            // edit HIV followup appointment
            if((o.getConcept().getUuid().equals(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID)) && hivFollowUpAppointment != null ) {
                nxtAppointment = true;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

                editedFollowUpAppointment.setRelatedAppointment(app);
                Appointment app2 = appointmentsService.validateAndSave(editedFollowUpAppointment);

            }

        }
    }

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
            AppointmentServiceDefinition appointmentServiceDefinition = new AppointmentServiceDefinition();
            if(o.getConcept().getUuid().equals(APPOINTMENT_REASON_CONCEPT_UUID) ) {
                if(o.getValueCoded().getConceptId() == 160523 || o.getValueCoded().getConceptId() == 160521 ) {
                    followUpAppointment = true;
                }
            }

            // create HIV followup appointment
            if(o.getConcept().getUuid().equals(NEXT_CLINICAL_APPOINTMENT_DATE_CONCEPT_UUID) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(HIV_FOLLOWUP_SERVICE) != null ) {
                nxtAppointment = true;

                appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(HIV_FOLLOWUP_SERVICE).getId());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date nextApptStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                Date nextApptEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                nextAppointment.setUuid(enc.getUuid());
                nextAppointment.setPatient(enc.getPatient());
                nextAppointment.setService(appointmentServiceDefinition);
                nextAppointment.setStartDateTime(nextApptStartDateTime);
                nextAppointment.setEndDateTime(nextApptEndDateTime);
                nextAppointment.setLocation(enc.getLocation());
                nextAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                nextAppointment.setAppointmentKind(AppointmentKind.Scheduled);
            }

            if(nxtAppointment && followUpAppointment) {
                Appointment app = appointmentsService.validateAndSave(nextAppointment);
            }

            // create appointment for drug refill
            if(o.getConcept().getUuid().equals(NEXT_DRUG_REFILL_APPOINTMENT_DATE_CONCEPT_UUID) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE) != null) {
                appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(DRUG_REFILL_SERVICE).getId());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                Appointment currentFollowUpAppointment = appointmentsService.getAppointmentByUuid(enc.getUuid());

                Appointment refillAppointment = new Appointment();
                Date refillStartDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T07:00:00.0Z"), DateUtil.DateFormatType.UTC);
                Date refillEndDateTime = DateUtil.convertToDate(dateFormat.format(o.getValueDatetime()).concat("T20:00:00.0Z"), DateUtil.DateFormatType.UTC);
                refillAppointment.setRelatedAppointment(currentFollowUpAppointment);
                refillAppointment.setPatient(enc.getPatient());
                refillAppointment.setService(appointmentServiceDefinition);
                refillAppointment.setStartDateTime(refillStartDateTime);
                refillAppointment.setEndDateTime(refillEndDateTime);
                refillAppointment.setLocation(enc.getLocation());
                refillAppointment.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
                refillAppointment.setAppointmentKind(AppointmentKind.Scheduled);
                Appointment app = appointmentsService.validateAndSave(refillAppointment);

                currentFollowUpAppointment.setRelatedAppointment(app);
                Appointment app2 = appointmentsService.validateAndSave(currentFollowUpAppointment);
            }

        }

    }

    private void processProgramEncounter(Encounter enc) throws Throwable {
        //MCH or PREP or TB or KP appointments
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                if (enc.getForm() != null && (enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_POSTNATAL_VISIT) || enc.getForm() != null && enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_DELIVERY)) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_POSTNATAL_VISIT_SERVICE) != null) {
                    appointmentServiceDefinition.setAppointmentServiceId(appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_POSTNATAL_VISIT_SERVICE).getId());
                } else if (enc.getForm() != null && enc.getForm().getUuid().equals(MchMetadata._Form.MCHMS_ANTENATAL_VISIT) && appointmentServiceDefinitionService.getAppointmentServiceByUuid(MCH_ANTENATAL_VISIT_SERVICE) != null) {
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
}