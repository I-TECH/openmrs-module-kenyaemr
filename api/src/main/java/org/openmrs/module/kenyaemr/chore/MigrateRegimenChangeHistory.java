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
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
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
import org.openmrs.module.metadatadeploy.MetadataUtils;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * handles migration of orders to populate model for DrugRegimenHistory
 */
@Component("kenyaemr.chore.migrateRegimenChangeHistory")
public class MigrateRegimenChangeHistory extends AbstractChore {

    private Map<String, Integer> masterSetConcepts = new LinkedHashMap<String, Integer>();

    private Map<String, Map<String, DrugReference>> drugs = new LinkedHashMap<String, Map<String, DrugReference>>();

    private Map<String, List<RegimenDefinitionGroup>> regimenGroups = new LinkedHashMap<String, List<RegimenDefinitionGroup>>();

    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {

        ConceptService conceptService = Context.getConceptService();
        Concept ARVRegimenConcept = conceptService.getConcept(1085);
        Concept TBRegimenConcept = conceptService.getConcept(160021);
        PatientService patientService = Context.getPatientService();
        EncounterService encounterService = Context.getEncounterService();
        FormService formService = Context.getFormService();

        EncounterType encType = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.CONSULTATION);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);

        int tbRegimenConceptId = 160021;
        int arvRegimenConceptId = 1085;

        refresh();

        for (Integer patientId : getPatientsWithOrders()) {
            Patient patient = patientService.getPatient(patientId);
            RegimenChangeHistory tbRegimenHistory = RegimenChangeHistory.forPatient(patient, TBRegimenConcept);
            RegimenChangeHistory hivRegimenHistory = RegimenChangeHistory.forPatient(patient, ARVRegimenConcept);

            List<RegimenChange> tbRegimenChanges = tbRegimenHistory.getChanges();
            List<RegimenChange> arvRegimenChanges = hivRegimenHistory.getChanges();

            System.out.println("Processing patient: " + patientId + ", " +
                    "TB Rg changes: " + tbRegimenChanges.size() + ", ARV rg changes: " + arvRegimenChanges.size());

            if (tbRegimenChanges.size() < 1 && arvRegimenChanges.size() < 1) {
                continue;
            }

            if (tbRegimenChanges.size() > 0) {
                processRegimenChanges(patient, tbRegimenConceptId, tbRegimenChanges, form, encType);
            }

            if (arvRegimenChanges.size() > 0) {
                processRegimenChanges(patient, arvRegimenConceptId, arvRegimenChanges, form, encType);

            }

        }
        out.println("Completed migration for drug regimen history");

    }

    private void processRegimenChanges(Person patient, int masterSet, List<RegimenChange> changes, Form form, EncounterType encounterType) {
        PatientService patientService = Context.getPatientService();
        String program = masterSet == 1085 ? "ARV" : "TB";
        ConceptService conceptService = Context.getConceptService();
        EncounterService encounterService = Context.getEncounterService();
        String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String CURRENT_DRUGS = "1193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String START_DRUGS = "1256AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        Encounter e = null;

        for (RegimenChange change : changes) {
            RegimenOrder regimenStarted = change.getStarted();
            String conceptRef = null;

           // block for processing start change with/out stopped

            if (regimenStarted != null && regimenStarted.getDrugOrders().size() > 0) {

                Date startDate = change.getDate();
                e = new Encounter();
                e.setPatient(patientService.getPatient(patient.getPersonId()));
                e.setEncounterDatetime(startDate);
                e.setEncounterType(encounterType);
                e.setForm(form);



                List<RegimenDefinition> regimenDefinitions = findDefinitions(program, regimenStarted, false);
                if (regimenDefinitions != null && regimenDefinitions.size() > 0) {
                    conceptRef = regimenDefinitions.get(0).getConceptRef();
                }
                // create event obs
                Obs eventObs = new Obs();
                eventObs.setConcept(conceptService.getConceptByUuid(masterSet == 1085 ? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT));
                eventObs.setPerson(patient);
                eventObs.setObsDatetime(startDate);
                eventObs.setValueCoded(conceptService.getConceptByUuid(START_DRUGS));
                e.addObs(eventObs);

                // create regimen obs
                if (conceptRef != null && regimenStarted != null) {
                    Obs regimenObs = new Obs();
                    regimenObs.setConcept(conceptService.getConceptByUuid(CURRENT_DRUGS));
                    regimenObs.setPerson(patient);
                    regimenObs.setValueCoded(conceptService.getConceptByUuid(conceptRef));
                    regimenObs.setObsDatetime(startDate);
                    e.addObs(regimenObs);
                }
            }

            if (e != null) {
                encounterService.saveEncounter(e);
            }

        }
    }

    public synchronized void refresh() {
        masterSetConcepts.clear();
        drugs.clear();
        regimenGroups.clear();

        for (RegimenConfiguration configuration : Context.getRegisteredComponents(RegimenConfiguration.class)) {
            try {
                ClassLoader loader = configuration.getClassLoader();
                InputStream stream = loader.getResourceAsStream(configuration.getDefinitionsPath());

                loadDefinitionsFromXML(stream);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Unable to load " + configuration.getModuleId() + ":" + configuration.getDefinitionsPath(), ex);
            }
        }
    }

    /**
     * Loads definitions from an input stream containing XML
     * @param stream the path to XML resource
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public void loadDefinitionsFromXML(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbFactory.newDocumentBuilder();

        Document document = builder.parse(stream);

        Element root = document.getDocumentElement();

        // Parse each category
        NodeList categoryNodes = root.getElementsByTagName("category");
        for (int c = 0; c < categoryNodes.getLength(); c++) {
            // extract regimens categories i.e. arv, tb
            Element categoryElement = (Element) categoryNodes.item(c);
            String categoryCode = categoryElement.getAttribute("code");
            String masterSetUuid = categoryElement.getAttribute("masterSetUuid");

            Concept masterSetConcept = MetadataUtils.existing(Concept.class, masterSetUuid);
            masterSetConcepts.put(categoryCode, masterSetConcept.getConceptId());

            // extracts category drugs i.e. arv drugs
            Map<String, DrugReference> categoryDrugs = new HashMap<String, DrugReference>();

            // extracts category groups i.e. adult first line etc
            List<RegimenDefinitionGroup> categoryGroups = new ArrayList<RegimenDefinitionGroup>();

            // Parse all drug concepts for this category
            NodeList drugNodes = categoryElement.getElementsByTagName("drug");
            for (int d = 0; d < drugNodes.getLength(); d++) {
                Element drugElement = (Element) drugNodes.item(d);
                String drugCode = drugElement.getAttribute("code");
                String drugConceptUuid = drugElement.hasAttribute("conceptUuid") ? drugElement.getAttribute("conceptUuid") : null;
                String drugDrugUuid = drugElement.hasAttribute("drugUuid") ? drugElement.getAttribute("drugUuid") : null;

                DrugReference drug = (drugConceptUuid != null) ? DrugReference.fromConceptUuid(drugConceptUuid) : DrugReference.fromDrugUuid(drugDrugUuid);
                if (drug != null) {
                    categoryDrugs.put(drugCode, drug);
                }
            }

            // Parse all groups for this category
            NodeList groupNodes = categoryElement.getElementsByTagName("group");
            for (int g = 0; g < groupNodes.getLength(); g++) {
                Element groupElement = (Element) groupNodes.item(g);
                String groupCode = groupElement.getAttribute("code");
                String groupName = groupElement.getAttribute("name");

                RegimenDefinitionGroup group = new RegimenDefinitionGroup(groupCode, groupName);
                categoryGroups.add(group);

                // Parse all regimen definitions for this group
                NodeList regimenNodes = groupElement.getElementsByTagName("regimen");
                for (int r = 0; r < regimenNodes.getLength(); r++) {
                    Element regimenElement = (Element) regimenNodes.item(r);
                    String name = regimenElement.getAttribute("name");
                    String conceptRef = regimenElement.getAttribute("conceptRef");

                    RegimenDefinition regimenDefinition = new RegimenDefinition(name, group);
                    if (conceptRef != null)
                        regimenDefinition.setConceptRef(conceptRef);
                    // Parse all components for this regimen
                    NodeList componentNodes = regimenElement.getElementsByTagName("component");
                    ConceptService conceptService = Context.getConceptService();
                    for (int p = 0; p < componentNodes.getLength(); p++) {
                        Element componentElement = (Element) componentNodes.item(p);
                        String drugCode = componentElement.getAttribute("drugCode");
                        Double dose = componentElement.hasAttribute("dose") ? Double.parseDouble(componentElement.getAttribute("dose")) : null;
                        Concept units = componentElement.hasAttribute("units") ? conceptService.getConcept(RegimenConversionUtil.getConceptIdFromDoseUnitString(componentElement.getAttribute("units"))) : null;
                        Concept frequency = componentElement.hasAttribute("frequency") ? conceptService.getConcept(RegimenConversionUtil.getConceptIdFromFrequencyString(componentElement.getAttribute("frequency"))) : null;

                        DrugReference drug = categoryDrugs.get(drugCode);

                        if (drug == null)
                            throw new RuntimeException("Regimen component references invalid drug: " + drugCode);

                        regimenDefinition.addComponent(drug, dose, units, frequency);
                    }

                    group.addRegimen(regimenDefinition);
                }
            }

            drugs.put(categoryCode, categoryDrugs);
            regimenGroups.put(categoryCode, categoryGroups);
        }
    }

    /**
     * Finds definitions that match the given regimen
     * @param category the category, e.g. "ARV"
     * @param regimenOrder the regimen
     * @param exact whether matches must be exact (includes dose, units and frequency)
     * @return the definitions
     */
    public List<RegimenDefinition> findDefinitions(String category, RegimenOrder regimenOrder, boolean exact) {
        List<RegimenDefinitionGroup> groups = regimenGroups.get(category);
        if (groups == null) {
            throw new IllegalArgumentException("No such category: " + category);
        }

        List<RegimenDefinition> matches = new ArrayList<RegimenDefinition>();

        for (RegimenDefinitionGroup group : groups) {
            outer:
            for (RegimenDefinition definition : group.getRegimens()) {
                List<RegimenComponent> regimen = definition.getComponents();
                Set<DrugOrder> orders = regimenOrder.getDrugOrders();

                // Skip if regimen doesn't have same number of orders
                if (regimen.size() != orders.size()) {
                    continue;
                }

                // Check each component has an equivalent drug order
                for (RegimenComponent component : regimen) {

                    // Does regimen have a drug order for this component?
                    boolean regimenHasComponent = false;
                    for (DrugOrder order : orders) {
                        DrugReference componentDrugRef = component.getDrugRef();
                        DrugReference orderDrugRef = DrugReference.fromDrugOrder(order);
                        if (componentDrugRef.equals(orderDrugRef)) {

                            if (!exact || (ObjectUtils.equals(order.getDose(), component.getDose()) && order.getDoseUnits().equals(component.getUnits()) && order.getFrequency().getConcept().equals(component.getFrequency()))) {
                                regimenHasComponent = true;
                                break;
                            }
                        }
                    }

                    if (!regimenHasComponent) {
                        continue outer;
                    }
                }

                // Regimen has all components of the definition
                matches.add(definition);
            }
        }

        return matches;
    }

    protected Set<Integer> getPatientsWithOrders() {
        Cohort patientsHavingDrugOrder = Context.getPatientSetService().getPatientsHavingDrugOrder(null, null, null, null, null, null, null, null);
        return patientsHavingDrugOrder.getMemberIds();
    }
}
