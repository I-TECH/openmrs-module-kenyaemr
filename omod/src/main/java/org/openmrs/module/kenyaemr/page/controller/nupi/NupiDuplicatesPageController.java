/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.nupi;

import static org.mockito.Mockito.validateMockitoUsage;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class NupiDuplicatesPageController {

    public void get(@SpringBean KenyaUiUtils kenyaUi, UiUtils ui, PageModel model) {

        List<SimpleObject> duplicates = new ArrayList<SimpleObject>();

        PersonAttributeType duplicateStatusPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.DUPLICATE_NUPI_STATUS_WITH_NATIONAL_REGISTRY);
        PersonAttributeType duplicateFacilityPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.DUPLICATE_NUPI_FACILITY_WITH_NATIONAL_REGISTRY);
        PersonAttributeType duplicateSitesPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.DUPLICATE_NUPI_SITES_WITH_NATIONAL_REGISTRY);
        PersonAttributeType duplicateTotalSitesPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.DUPLICATE_NUPI_TOTALSITES_WITH_NATIONAL_REGISTRY);

        PatientIdentifierType nupiIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
        PatientIdentifierType upiIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);

        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        for (Patient patient : allPatients) {
            if (patient.getAttribute(duplicateStatusPA) != null) {
                // Has a duplicate?
                if (patient.getAttribute(duplicateStatusPA).getValue().trim().equalsIgnoreCase("true")) {
                    PatientIdentifier nupi = patient.getPatientIdentifier(nupiIdentifierType);
                    PatientIdentifier ccc = patient.getPatientIdentifier(upiIdentifierType);
                    String cccNum = "";
                    String nupiNum = "";
                    try {
                        nupiNum = nupi.getIdentifier();
                        cccNum = ccc.getIdentifier();
                    }catch (Exception e){}

                    SimpleObject patientDuplicateObject = SimpleObject.create(
                        "id", patient.getId(),
                        "uuid", patient.getUuid(), 
                        "givenName", patient.getGivenName(), 
                        "middleName", patient.getMiddleName() != null ? patient.getMiddleName() : "", 
                        "familyName", patient.getFamilyName(),
                        "fullName", getFullName(patient),
                        "identifiers", getIdentifiers(patient),
                        "birthdate", kenyaUi.formatDate(patient.getBirthdate()), 
                        "gender", patient.getGender(),
                        "nupi", nupiNum != null ? nupiNum : "-",
                        "ccc", cccNum != null ? cccNum : "-",
                        "facility", patient.getAttribute(duplicateFacilityPA) != null ? patient.getAttribute(duplicateFacilityPA).getValue().trim() : "-",
                        "otherFacilities", patient.getAttribute(duplicateSitesPA) != null ? patient.getAttribute(duplicateSitesPA).getValue().trim() : "-",
                        "facilityNames", patient.getAttribute(duplicateSitesPA) != null ? getFacilityNames(patient.getAttribute(duplicateSitesPA).getValue().trim()) : "-",
                        "totalFacilities", patient.getAttribute(duplicateTotalSitesPA) != null ? patient.getAttribute(duplicateTotalSitesPA).getValue().trim() : "-",
                        "error", "-");
                    duplicates.add(patientDuplicateObject);
                }
            }
        }
        model.put("patientDuplicatesList", ui.toJson(duplicates));
        model.put("duplicatesCount", duplicates.size());
    }

    /**
     * Returns the facility names given a list of MFL codes
     * @param facilities the MFL codes separated by commas
     * @return
     */
    private String getFacilityNames(String facilities) {
        String ret = "";
        if (facilities != null && !facilities.isEmpty()) {
            String[] ids = facilities.split(",");
            StringBuilder result = new StringBuilder();

            for (String id : ids) {
                try {
                    // int key = Integer.parseInt(id.trim());
                    id = id.trim();
                    Location facility = Context.getService(KenyaEmrService.class).getLocationByMflCode(id);
                    String fname = facility.getName();

                    if (fname != null) {
                        result.append(id + ": " + fname).append(",\n<br />"); // Add the fname, a comma and a newline
                    }
                } catch(Exception e) {}
            }

            // Remove the trailing comma and newline if there are entries
            if (result.length() > 0) {
                try {
                    result.setLength(result.length() - 8);
                } catch(Exception e){}
            }

            ret = result.toString();
        }
        return(ret);
    }

    /**
     * Returns the full name of the patient given a patient
     * @param patient
     * @return
     */
    private String getFullName(Patient patient) {
        String ret = "";
        ret += patient.getGivenName() != null ? patient.getGivenName() + " " : "";
        ret += patient.getMiddleName() != null ? patient.getMiddleName() + " " : "";
        ret += patient.getFamilyName() != null ? patient.getFamilyName() : "";
        ret = ret.trim();
        return(ret);
    }

    /**
     * Returns all the identifiers of the patient given a patient (national id, passport, birth certificate)
     * @param patient
     * @return
     */
    private String getIdentifiers(Patient patient) {
        String ret = "";
        StringBuilder result = new StringBuilder();
        PatientIdentifierType nationalID = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
        PatientIdentifierType passPortNo = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER);
        PatientIdentifierType birthCertNo = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);
		
        // National ID
        PatientIdentifier natID = patient.getPatientIdentifier(nationalID);
        if(natID != null) {
            String natIdentifier = natID.getIdentifier();
            if(natIdentifier != null) {
                result.append("National ID: " + natIdentifier).append(",\n<br />");
            }
        }

        // Passport
        PatientIdentifier passport = patient.getPatientIdentifier(passPortNo);
        if(passport != null) {
            String passIdentifier = passport.getIdentifier();
            if(passIdentifier != null) {
                result.append("Passport No.: " + passIdentifier).append(",\n<br />");
            }
        }

        // Birth Certificate
        PatientIdentifier birthCert = patient.getPatientIdentifier(birthCertNo);
        if(birthCert != null) {
            String birthIdentifier = birthCert.getIdentifier();
            if(birthIdentifier != null) {
                result.append("Birth Cert.: " + birthIdentifier).append(",\n<br />");
            }
        }

        // Remove the trailing comma and newline if there are entries
        if (result.length() > 0) {
            try {
                result.setLength(result.length() - 8);
            } catch(Exception e){}
        }

        ret = result.toString();

        return(ret);
    }

}


