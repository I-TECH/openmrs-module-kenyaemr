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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.StopWatch;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
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
        User defaultUser = Context.getUserService().getUser(1);

        EncounterType encType = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.DRUG_REGIMEN_EDITOR);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);

        int tbRegimenConceptId = 160021;
        int arvRegimenConceptId = 1085;

        refresh();

        int counter=0;

        for (Integer patientId : getPatientsWithOrders()) {
            Patient patient = patientService.getPatient(patientId);
            RegimenChangeHistory tbRegimenHistory = RegimenChangeHistory.forPatient(patient, TBRegimenConcept);
            RegimenChangeHistory hivRegimenHistory = RegimenChangeHistory.forPatient(patient, ARVRegimenConcept);

            List<RegimenChange> tbRegimenChanges = tbRegimenHistory.getChanges();
            List<RegimenChange> arvRegimenChanges = hivRegimenHistory.getChanges();

            if (tbRegimenChanges.size() < 1 && arvRegimenChanges.size() < 1) {
                continue;
            }

            if (tbRegimenChanges.size() > 0) {
                processRegimenChanges(patient, tbRegimenConceptId, tbRegimenChanges, form, encType);
            }

            if (arvRegimenChanges.size() > 0) {
                processRegimenChanges(patient, arvRegimenConceptId, arvRegimenChanges, form, encType);

            }
            counter++;

            if ((counter%400)==0) {
                Context.flushSession();
                Context.clearSession();
                counter=0;

            }

        }
        out.println("Completed migration for drug regimen history");
        out.println("Voiding encounters with null regimen....");
        voidAllEncountersWithNullRegimen();
        discontinueAllActiveOrders();
        out.println("Successfully completed all drug regimen migration operations");

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
                } else {
                    e.setVoided(true);
                    e.setDateVoided(new Date());
                    e.setVoidReason("No matching regimen for regimen order. Needs re-entering");
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

    protected void voidAllEncountersWithNullRegimen(){

        String encountersWithNullRegimenQry = "update encounter e inner join \n" +
                "(\n" +
                "select \n" +
                "e.encounter_id,\n" +
                "max(if(o.concept_id=1193,o.value_coded,null)) as regimen \n" +
                "from encounter e \n" +
                "inner join obs o on e.encounter_id = o.encounter_id and o.voided=0 and o.concept_id in(1193,1252,5622,1191,1255,1268) \n" +
                "inner join ( select encounter_type, uuid,name from form where uuid in('da687480-e197-11e8-9f32-f2801f1b9fd1') ) f on f.encounter_type=e.encounter_type \n" +
                "group by e.encounter_id having regimen is null\n" +
                ") t on e.encounter_id=t.encounter_id\n" +
                "set e.voided=1;";

        AdministrationService as = Context.getAdministrationService();
        List<List<Object>> rowsAffected = as.executeSQL(encountersWithNullRegimenQry, false);
    }

    protected void discontinueAllActiveOrders(){

        String activeOrdersQry = "update orders set order_action='DISCONTINUE', order_reason_non_coded='previously existing orders' WHERE order_action='NEW';";

        AdministrationService as = Context.getAdministrationService();
        List<List<Object>> rowsAffected = as.executeSQL(activeOrdersQry, false);
        System.out.println("Rows affected: " + rowsAffected.size());
    }
    public static String getRegimensJson() {
        String regimensJson = "[{\n" +
                "   \"version\": \"3\",\n" +
                "   \"category\": [\n" +
                "      {\n" +
                "         \"code\": \"ARV\",\n" +
                "         \"masterSetUuid\": \"${metadata.concept.ANTIRETROVIRAL_DRUGS}\",\n" +
                "         \"drugs\": {\n" +
                "               {\n" +
                "            \"drug\": [\n" +
                "                  \"code\": \"D4T\",\n" +
                "                  \"conceptUuid\": \"84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"2135AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"AZT\",\n" +
                "                  \"conceptUuid\": \"86663AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"2209AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"TDF\",\n" +
                "                  \"conceptUuid\": \"84795AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"2153AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"3TC\",\n" +
                "                  \"conceptUuid\": \"78643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1815AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"ABC\",\n" +
                "                  \"conceptUuid\": \"70057AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1324AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"FTC\",\n" +
                "                  \"conceptUuid\": \"75628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1645AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"DDI\",\n" +
                "                  \"conceptUuid\": \"74807AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1605AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"NVP\",\n" +
                "                  \"conceptUuid\": \"80586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1940AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"EFV\",\n" +
                "                  \"conceptUuid\": \"75523AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1641AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"LPV\",\n" +
                "                  \"conceptUuid\": \"79040AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1859AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"RTV\",\n" +
                "                  \"conceptUuid\": \"83412AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"ATV\",\n" +
                "                  \"conceptUuid\": \"71648AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"1431AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"ETR\",\n" +
                "                  \"conceptUuid\": \"159810AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"121b19f9-ef41-4b09-98b9-0d5ae2fdd597\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"RAL\",\n" +
                "                  \"conceptUuid\": \"154378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"8f897f8a-9afa-4eee-9685-6e47965a8bcd\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"DRV\",\n" +
                "                  \"conceptUuid\": \"74258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                  \"drugUuid\": \"d2fc1b80-1c95-4b24-8db1-f60234694654\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"DTG\",\n" +
                "                  \"conceptUuid\": \"d1fd0e18-e0b9-46ae-ac0e-0452a927a94b\",\n" +
                "                  \"drugUuid\": \"c52cec20-e6a4-483d-8698-5d7dba588998\"\n" +
                "               }\n" +
                "            ]\n" +
                "         },\n" +
                "         \"group\": [\n" +
                "            {\n" +
                "               \"code\": \"adult-first\",\n" +
                "               \"name\": \"Adult (first line)\",\n" +
                "               \"regimen\": [\n" +
                "                  {\n" +
                "                     \"name\": \"TDF/3TC/NVP\",\n" +
                "                     \"conceptRef\": \"162565AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"28e97aa1-e184-42c9-9e6f-ae6455df2b47\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"NVP\",\n" +
                "                           \"dose\": \"200\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"TDF/3TC/EFV\",\n" +
                "                     \"conceptRef\": \"164505AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"5e42f2d0-29bd-45b3-b493-77c18fc85439\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"EFV\",\n" +
                "                           \"dose\": \"600\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"AZT/3TC/NVP\",\n" +
                "                     \"conceptRef\": \"1652AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"8c7a7ed8-2237-4415-8bd3-a4de29a2d0f7\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"NVP\",\n" +
                "                           \"dose\": \"200\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"AZT/3TC/EFV\",\n" +
                "                     \"conceptRef\": \"160124AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"344c5b71-0e65-4b9d-abce-10bd2193780b\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"EFV\",\n" +
                "                           \"dose\": \"600\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"NOCTE\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"D4T/3TC/NVP\",\n" +
                "                     \"conceptRef\": \"792AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"4f1016e7-5500-4a77-b722-048c9974bada\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"D4T\",\n" +
                "                           \"dose\": \"30\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"NVP\",\n" +
                "                           \"dose\": \"200\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"D4T/3TC/EFV\",\n" +
                "                     \"conceptRef\": \"160104AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"4c11b951-fb16-4b42-8314-b8cd4d1d4a04\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"D4T\",\n" +
                "                           \"dose\": \"30\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"EFV\",\n" +
                "                           \"dose\": \"600\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"NOCTE\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"TDF/3TC/AZT\",\n" +
                "                     \"conceptRef\": \"98e38a9c-435d-4a94-9b66-5ca524159d0e\",\n" +
                "                     \"orderSetRef\": \"b256ea8c-7f5d-4d66-8a5e-3bc9efae56c7\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"AZT/3TC/DTG\",\n" +
                "                     \"conceptRef\": \"6dec7d7d-0fda-4e8d-8295-cb6ef426878d\",\n" +
                "                     \"orderSetRef\": \"b3af5ba7-bac7-4af9-9bd4-b43bdfb020de\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"DTG\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"TDF/3TC/DTG\",\n" +
                "                     \"conceptRef\": \"9fb85385-b4fb-468c-b7c1-22f75834b4b0\",\n" +
                "                     \"orderSetRef\": \"fcc936c7-ff55-4f40-bb05-007796192bc2\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"DTG\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"ABC/3TC/DTG\",\n" +
                "                     \"conceptRef\": \"4dc0119b-b2a6-4565-8d90-174b97ba31db\",\n" +
                "                     \"orderSetRef\": \"4fead254-26af-479f-8958-df2fa108d013\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"DTG\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"code\": \"adult-second\",\n" +
                "               \"name\": \"Adult (second line)\",\n" +
                "               \"regimen\": [\n" +
                "                  {\n" +
                "                     \"name\": \"AZT/3TC/LPV/r\",\n" +
                "                     \"conceptRef\": \"162561AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"c56e7dbf-ec9c-47f5-b5d9-15d5cdfee002\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\",\n" +
                "                           \"dose\": \"400\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"dose\": \"100\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"AZT/3TC/ATV/r\",\n" +
                "                     \"conceptRef\": \"164511AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"3fdc7351-9eeb-4141-84fc-d51b518cc14f\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ATV\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"dose\": \"100\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"TDF/3TC/LPV/r\",\n" +
                "                     \"conceptRef\": \"162201AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"ffa2aee4-f795-4bb7-bc63-eebe75462e9f\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\",\n" +
                "                           \"dose\": \"400\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"dose\": \"100\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"TDF/3TC/ATV/r\",\n" +
                "                     \"conceptRef\": \"164512AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"38138537-dccf-4860-b017-459e99b25fab\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"dose\": \"150\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"BD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ATV\",\n" +
                "                           \"dose\": \"300\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"dose\": \"100\",\n" +
                "                           \"units\": \"mg\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"D4T/3TC/LPV/r\",\n" +
                "                     \"conceptRef\": \"162560AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"113ed536-3148-4fab-bc5a-04e0c7fe5091\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"D4T\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"AZT/TDF/3TC/LPV/r\",\n" +
                "                     \"conceptRef\": \"c421d8e7-4f43-43b4-8d2f-c7d4cfb976a4\",\n" +
                "                     \"orderSetRef\": \"e322f2fa-de3c-4c60-9402-7c24e5cc97f0\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"ETR/RAL/DRV/RTV\",\n" +
                "                     \"conceptRef\": \"337b6cfd-9fa7-47dc-82b4-d479c39ef355\",\n" +
                "                     \"orderSetRef\": \"b966de95-84ee-4b21-90b0-2382df053444\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ETR\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RAL\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"DRV\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"ETR/TDF/3TC/LPV/r\",\n" +
                "                     \"conceptRef\": \"7a6c51c4-2b68-4d5a-b5a2-7ba420dde203\",\n" +
                "                     \"orderSetRef\": \"ebe999c2-fd44-415b-8a9f-5adbc24d7a8c\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ETR\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"code\": \"child-first\",\n" +
                "               \"name\": \"Child\",\n" +
                "               \"regimen\": [\n" +
                "                  {\n" +
                "                     \"name\": \"ABC/3TC/LPV/r\",\n" +
                "                     \"conceptRef\": \"162200AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"07040910-04c9-4007-801f-66c7f7e981c6\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"ABC/3TC/NVP\",\n" +
                "                     \"conceptRef\": \"162199AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"791f8ae3-6112-4ffa-9152-5946fe18a4f0\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"NVP\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"ABC/3TC/EFV\",\n" +
                "                     \"conceptRef\": \"162563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"9225f40d-207e-48b6-8fee-b87f766d0625\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"EFV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"AZT/3TC/ABC\",\n" +
                "                     \"conceptRef\": \"817AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"f551efdb-2b30-4a00-82a6-90e8acfcb6c1\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"AZT\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"D4T/3TC/ABC\",\n" +
                "                     \"conceptRef\": \"b9fea00f-e462-4ea5-8d40-cc10e4be697e\",\n" +
                "                     \"orderSetRef\": \"77cccd76-586b-4f55-8642-ad1cfe45af19\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"D4T\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"TDF/ABC/LPV/r\",\n" +
                "                     \"conceptRef\": \"162562AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"e72c4abd-e960-44fd-9158-d558f0c7f819\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"ABC/DDI/LPV/r\",\n" +
                "                     \"conceptRef\": \"162559AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"1c21f856-3b72-4627-b765-8a5e04765341\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"DDI\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"ABC/TDF/3TC/LPV/r\",\n" +
                "                     \"conceptRef\": \"077966a6-4fbd-40ce-9807-2d5c2e8eb685\",\n" +
                "                     \"orderSetRef\": \"e9fa5f90-26a0-4233-ba23-63a171c37969\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"ABC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"TDF\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"3TC\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"LPV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"RTV\",\n" +
                "                           \"units\": \"tab\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  }\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"code\": \"TB\",\n" +
                "         \"masterSetUuid\": \"160021AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "         \"drugs\": {\n" +
                "            \"drug\": [\n" +
                "               {\n" +
                "                  \"code\": \"R150\",\n" +
                "                  \"drugUuid\": \"a74fefc6-931d-47b9-b282-5d6c8c8d8060\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"R60\",\n" +
                "                  \"drugUuid\": \"161f633d-ab5d-4cd1-94c5-7a012dedc388\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"H150\",\n" +
                "                  \"drugUuid\": \"89b72116-108d-4b65-b7fe-5a841192e095\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"H75\",\n" +
                "                  \"drugUuid\": \"97810e6b-cfcf-44fa-b63c-5d3e12cbe8d7\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"H30\",\n" +
                "                  \"drugUuid\": \"9a012906-3778-4e2e-848f-e0272bfe9e27\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"Z400\",\n" +
                "                  \"drugUuid\": \"0dbc4971-67ea-40d7-8089-7c327e5f3c6e\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"Z150\",\n" +
                "                  \"drugUuid\": \"c97032a4-2136-4e92-8ff8-3fc6faab4133\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"E400\",\n" +
                "                  \"drugUuid\": \"37e185a3-ba6b-447a-83e0-7ff3edbe76f7\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"E275\",\n" +
                "                  \"drugUuid\": \"52690c5a-e9cd-4561-8aad-3b646fb7deaa\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"S1\",\n" +
                "                  \"drugUuid\": \"13b56178-a15b-4070-951d-2ce813cccc32\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"code\": \"Rfb150\",\n" +
                "                  \"drugUuid\": \"c0b4caf2-3fc4-42db-b4ea-5d082136b651\"\n" +
                "               }\n" +
                "            ]\n" +
                "         },\n" +
                "         \"group\": [\n" +
                "            {\n" +
                "               \"code\": \"adult-intensive\",\n" +
                "               \"name\": \"Intensive Phase (Adult)\",\n" +
                "               \"regimen\": [\n" +
                "                  {\n" +
                "                     \"name\": \"RHZE\",\n" +
                "                     \"conceptRef\": \"1675AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"d647932b-5825-46e5-b0d7-8ddcff5f3303\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"R150\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H75\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Z400\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"E275\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"RHZ\",\n" +
                "                     \"conceptRef\": \"768AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"07add7bb-77b0-492a-820e-d0a6ebab6e36\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"R150\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H75\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Z400\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"SRHZE\",\n" +
                "                     \"conceptRef\": \"1674AAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"9e3dba82-5e95-4f67-838a-cf4e185d25f9\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"S1\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"g\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"R150\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H75\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Z400\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"E275\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"RfbHZE\",\n" +
                "                     \"conceptRef\": \"07c72be8-c575-4e26-af09-9a98624bce67\",\n" +
                "                     \"orderSetRef\": \"491a36cd-6dbb-407f-80f5-d1c9dc468fd2\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Rfb150\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H75\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Z400\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"E275\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"RfbHZ\",\n" +
                "                     \"conceptRef\": \"9ba203ec-516f-4493-9b2c-4ded6cc318bc\",\n" +
                "                     \"orderSetRef\": \"a225be8c-632a-4e29-a164-2fa40b479938\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Rfb150\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H75\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Z400\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"SRfbHZE\",\n" +
                "                     \"conceptRef\": \"fce8ba26-8524-43d1-b0e1-53d8a3c06c00\",\n" +
                "                     \"orderSetRef\": \"fa208a9d-53a7-447d-8cc9-55c2343de87c\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"S1\",\n" +
                "                           \"dose\": \"1\",\n" +
                "                           \"units\": \"g\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Rfb150\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H75\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"Z400\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"E275\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"S (1 gm vial)\",\n" +
                "                     \"conceptRef\": \"84360AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"6a9f20b1-c898-435e-bc89-8c0322e0204b\",\n" +
                "                     \"component\": {\n" +
                "                        \"drugCode\": \"S1\",\n" +
                "                        \"dose\": \"1\",\n" +
                "                        \"units\": \"g\",\n" +
                "                        \"frequency\": \"OD\"\n" +
                "                     }\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"code\": \"child-intensive\",\n" +
                "               \"name\": \"Intensive Phase (Child)\",\n" +
                "               \"regimen\": [\n" +
                "                  {\n" +
                "                     \"name\": \"E\",\n" +
                "                     \"conceptRef\": \"75948AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"f73703c8-de30-4064-b93b-38d74c171a7a\",\n" +
                "                     \"component\": {\n" +
                "                        \"drugCode\": \"E400\",\n" +
                "                        \"units\": \"tab\",\n" +
                "                        \"frequency\": \"OD\"\n" +
                "                     }\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"RH\",\n" +
                "                     \"conceptRef\": \"1194AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"919f3a41-0904-4342-a9d0-9c15fbf8a736\",\n" +
                "                     \"component\": {\n" +
                "                        \"drugCode\": \"R60\",\n" +
                "                        \"units\": \"tab\",\n" +
                "                        \"frequency\": \"OD\"\n" +
                "                     }\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"code\": \"adult-continuation\",\n" +
                "               \"name\": \"Continuation Phase (Adult)\",\n" +
                "               \"regimen\": [\n" +
                "                  {\n" +
                "                     \"name\": \"RHE\",\n" +
                "                     \"conceptRef\": \"159851AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"db2189a2-6d29-46aa-8197-9a04a19cc426\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"R150\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H75\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"E275\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"name\": \"EH\",\n" +
                "                     \"conceptRef\": \"1108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "                     \"orderSetRef\": \"e3ac35bf-6e3c-4328-9e55-7b7c686daa21\",\n" +
                "                     \"component\": [\n" +
                "                        {\n" +
                "                           \"drugCode\": \"E400\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                           \"drugCode\": \"H150\",\n" +
                "                           \"dose\": \"2\",\n" +
                "                           \"units\": \"tab\",\n" +
                "                           \"frequency\": \"OD\"\n" +
                "                        }\n" +
                "                     ]\n" +
                "                  }\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ]\n" +
                "}]";
        return regimensJson;
    }

}
