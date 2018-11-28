/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.chore;

import org.apache.commons.lang.ObjectUtils;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.regimen.DrugReference;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenComponent;
import org.openmrs.module.kenyaemr.regimen.RegimenConfiguration;
import org.openmrs.module.kenyaemr.regimen.RegimenConversionUtil;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinition;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinitionGroup;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * updates reason and date regimen stopped for migrated orders
 */
@Component("kenyaemr.chore.UpdateRegimenChangeReasonAndDate")
public class UpdateRegimenChangeReasonAndDate extends AbstractChore {

    private Map<String, Integer> masterSetConcepts = new LinkedHashMap<String, Integer>();

    private Map<String, Map<String, DrugReference>> drugs = new LinkedHashMap<String, Map<String, DrugReference>>();

    private Map<String, List<RegimenDefinitionGroup>> regimenGroups = new LinkedHashMap<String, List<RegimenDefinitionGroup>>();

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


        EncounterType encType = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.CONSULTATION);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);


        Map<Integer, Set<RegimenChangeReason>> records = new HashMap<Integer, Set<RegimenChangeReason>>();
        for (Integer orderId : getDiscontinuedOrders().getMemberIds()) {
            Order o = orderService.getOrder(orderId);
            Integer patientId = orderService.getOrder(orderId).getPatient().getPatientId();
            if (records.get(patientId) != null) {
                records.get(patientId).add(new RegimenChangeReason(o.getDateActivated(), o.getOrderReason(), o.getOrderReasonNonCoded()));
            } else {
                Set<RegimenChangeReason> r = new HashSet<RegimenChangeReason>();
                RegimenChangeReason cr = new RegimenChangeReason(o.getDateActivated(), o.getOrderReason(), o.getOrderReasonNonCoded());
                r.add(cr);
                records.put(patientId, r);
            }
        }



        for (Map.Entry<Integer, Set<RegimenChangeReason>> entry : records.entrySet()) {
            Patient p = patientService.getPatient(entry.getKey());
            Set<RegimenChangeReason> changeReasons = entry.getValue();
            List<Encounter> encounters = EmrUtils.AllEncounters(p, encType, form);
            Map<Date, Encounter> encMap = new HashMap<Date, Encounter>();
            for (Encounter e : encounters) {
                encMap.put(e.getEncounterDatetime(), e);
            }

            for (RegimenChangeReason r : changeReasons) {
                if (encMap.containsKey(r.changeDate)) {
                    Encounter encounter = encMap.get(r.changeDate);

                    // compose date stopped and reason stopped obs
                    Obs reasonStoppedCodedObs = new Obs();
                    reasonStoppedCodedObs.setConcept(conceptService.getConceptByUuid(REASON_REGIMEN_STOPPED_CODED));
                    reasonStoppedCodedObs.setValueCoded(r.reasonCoded);
                    encounter.addObs(reasonStoppedCodedObs);
                    encounterService.saveEncounter(encounter);
                }
            }
        }

        out.println("Completed updating migrated orders with reason for regimen change");

    }



    private Cohort getDiscontinuedOrders() {
        String sqlQuery = "select order_id from orders where order_reason is not null or order_reason_non_coded is not null;";

        //EvaluationContext evaluationContext = new EvaluationContext();
        //evaluationContext.setParameterValues(parameterValues);
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
        Date changeDate;
        Concept reasonCoded;
        String reasonNonCoded;

        public RegimenChangeReason(Date changeDate, Concept reasonCoded, String reasonNonCoded) {
            this.changeDate = changeDate;
            this.reasonCoded = reasonCoded;
            this.reasonNonCoded = reasonNonCoded;
        }

        public Date getChangeDate() {
            return changeDate;
        }

        public void setChangeDate(Date changeDate) {
            this.changeDate = changeDate;
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
            result = (int) (prime * result + changeDate.getTime());
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
            if (changeDate != other.changeDate)
                return false;
            return true;

        }
    }
}
