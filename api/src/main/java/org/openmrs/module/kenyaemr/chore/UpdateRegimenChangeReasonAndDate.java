/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.chore;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * updates reason and date regimen stopped for migrated orders
 */
@Component("kenyaemr.chore.UpdateRegimenChangeReasonAndDate")
public class UpdateRegimenChangeReasonAndDate extends AbstractChore {

    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {

        ConceptService conceptService = Context.getConceptService();
        PatientService patientService = Context.getPatientService();
        EncounterService encounterService = Context.getEncounterService();
        FormService formService = Context.getFormService();
        OrderService orderService = Context.getOrderService();
        String REASON_REGIMEN_STOPPED_CODED = "1252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String DATE_REGIMEN_STOPPED = "1191AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String REASON_REGIMEN_STOPPED_NON_CODED = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";




        EncounterType encType = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.DRUG_REGIMEN_EDITOR);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);


        Map<Integer, Set<RegimenChangeReason>> records = new HashMap<Integer, Set<RegimenChangeReason>>();
        for (Integer orderId : getDiscontinuedOrders().getMemberIds()) {
            Order o = orderService.getOrder(orderId);
            Integer patientId = orderService.getOrder(orderId).getPatient().getPatientId();
            if (records.get(patientId) != null) {
                records.get(patientId).add(new RegimenChangeReason(o.getPatient().getPatientId(), o.getDateActivated(), o.getDateStopped(), o.getOrderReason(), o.getOrderReasonNonCoded()));
            } else {
                Set<RegimenChangeReason> r = new HashSet<RegimenChangeReason>();
                RegimenChangeReason cr = new RegimenChangeReason(o.getPatient().getPatientId(), o.getDateActivated(), o.getDateStopped(), o.getOrderReason(), o.getOrderReasonNonCoded());
                r.add(cr);
                records.put(patientId, r);
            }
        }


        int counter = 0;
        for (Map.Entry<Integer, Set<RegimenChangeReason>> entry : records.entrySet()) {

            Patient p = patientService.getPatient(entry.getKey());
            Set<RegimenChangeReason> changeReasons = entry.getValue();
            List<Encounter> encounters = EmrUtils.AllEncounters(p, encType, form);
            Map<Date, Encounter> encMap = new HashMap<Date, Encounter>();
            for (Encounter e : encounters) {
                encMap.put(e.getEncounterDatetime(), e);
            }

            for (RegimenChangeReason r : changeReasons) {

                if (encMap.containsKey(r.getDateActivated())) {
                    Encounter encounter = encMap.get(r.getDateActivated());

                    // compose date stopped and reason stopped obs
                    Obs dateStoppedObs = new Obs();
                    dateStoppedObs.setConcept(conceptService.getConceptByUuid(DATE_REGIMEN_STOPPED));
                    dateStoppedObs.setValueDatetime(r.dateStopped);
                    dateStoppedObs.setObsDatetime(r.dateStopped);
                    encounter.addObs(dateStoppedObs);

                    Obs reasonStoppedCodedObs = new Obs();
                    reasonStoppedCodedObs.setConcept(conceptService.getConceptByUuid(REASON_REGIMEN_STOPPED_CODED));
                    reasonStoppedCodedObs.setValueCoded(r.reasonCoded);
                    reasonStoppedCodedObs.setObsDatetime(r.dateStopped);
                    encounter.addObs(reasonStoppedCodedObs);

                    if (r.getReasonNonCoded() != null) {
                        Obs reasonStoppedNonCodedObs = new Obs();
                        reasonStoppedNonCodedObs.setConcept(conceptService.getConceptByUuid(REASON_REGIMEN_STOPPED_NON_CODED));
                        reasonStoppedNonCodedObs.setValueText(r.reasonNonCoded);
                        reasonStoppedNonCodedObs.setObsDatetime(r.dateStopped);
                        encounter.addObs(reasonStoppedNonCodedObs);
                    }
                   encounterService.saveEncounter(encounter);
                }
            }
            counter++;

            if ((counter%500)==0) {
                Context.flushSession();
                Context.clearSession();
                counter=0;
            }
        }

        out.println("Completed updating migrated orders with reason for regimen change");

    }



    private Cohort getDiscontinuedOrders() {
        String sqlQuery = "select order_id from orders where date_stopped is not null and voided=0;";
        Cohort cohort = null;
        SqlCohortDefinition cohortDefinition = new SqlCohortDefinition(sqlQuery);
        try {
            cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
        return cohort;
    }

    class RegimenChangeReason {
        private Integer patientId;
        private Date dateActivated;
        private Date dateStopped;
        private Concept reasonCoded;
        private String reasonNonCoded;

        public RegimenChangeReason(Integer patientId, Date dateActivated, Date dateStopped, Concept reasonCoded, String reasonNonCoded) {
            this.dateActivated = dateActivated;
            this.reasonCoded = reasonCoded;
            this.reasonNonCoded = reasonNonCoded;
            this.patientId = patientId;
            this.dateStopped = dateStopped;
        }

        public Date getDateStopped() {
            return dateStopped;
        }

        public void setDateStopped(Date dateStopped) {
            this.dateStopped = dateStopped;
        }

        public Date getDateActivated() {
            return dateActivated;
        }

        public Integer getPatientId() {
            return patientId;
        }

        public void setPatientId(Integer patientId) {
            this.patientId = patientId;
        }

        public void setDateActivated(Date dateActivated) {
            this.dateActivated = dateActivated;
        }

        public Concept getReasonCoded() {
            return reasonCoded;
        }

        public void setReasonCoded(Concept reasonCoded) {
            this.reasonCoded = reasonCoded;
        }

        public String getReasonNonCoded() {
            return reasonNonCoded;
        }

        public void setReasonNonCoded(String reasonNonCoded) {
            this.reasonNonCoded = reasonNonCoded;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (int) (prime * result + patientId + dateActivated.getTime());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RegimenChangeReason other = (RegimenChangeReason) obj;

            return patientId == other.patientId
                    && dateActivated.equals(other.dateActivated);

        }
    }
}
