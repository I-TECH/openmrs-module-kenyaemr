/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.kenyaemr.regimen;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.OrderSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegimenJsonGenerator {

    Patient patient;

    public RegimenJsonGenerator(Patient patient) {
        this.patient = patient;
    }

    public String generateRegimenJsonFromRegimensConfigFile() {
        Encounter lastRegimenEncounter = EmrUtils.lastEncounter(patient,
                Context.getEncounterService().getEncounterTypeByUuid(CommonMetadata._EncounterType.DRUG_REGIMEN_EDITOR), Context
                        .getFormService().getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR));
        ArrayNode components = JsonNodeFactory.instance.arrayNode();
        StringBuilder nonstandardRegimenShort = new StringBuilder();
        String CURRENT_DRUG_NON_STANDARD ="1088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String nonStandard = null;

        if (lastRegimenEncounter != null) {
            for (Obs o : lastRegimenEncounter.getObs()) {
                ObjectNode regimenComponent = JsonNodeFactory.instance.objectNode();

                if (o.getConcept().getConceptId() == 1088) {
                    nonStandard = o.getConcept().getUuid();
                    if(o.getValueCoded().getShortNameInLocale(CoreConstants.LOCALE) != null) {
                        nonstandardRegimenShort.append(o.getValueCoded().getShortNameInLocale(CoreConstants.LOCALE).getName() + "/");
                    }else {
                        nonstandardRegimenShort.append(o.getValueCoded().getFullySpecifiedName(CoreConstants.LOCALE).getName() + "/");

                    }
                    regimenComponent.put("name", o.getValueCoded().getShortNameInLocale(CoreConstants.LOCALE) != null ? o.getValueCoded().getShortNameInLocale(CoreConstants.LOCALE).getName()
                            : o.getValueCoded().getFullySpecifiedName(CoreConstants.LOCALE).getName());
                    regimenComponent.put("drug_id", getDrugForConceptId(o.getValueCoded().getConceptId()));
                    components.add(regimenComponent);
                }

            }
            ObjectNode regimenEntryNoneStandard = JsonNodeFactory.instance.objectNode();
            ArrayNode regimensNoneStandard = JsonNodeFactory.instance.arrayNode();
            if(nonstandardRegimenShort.length() > 0) {
                regimenEntryNoneStandard.put("regimenName", (nonstandardRegimenShort.toString()).substring(0, nonstandardRegimenShort.length() - 1));
            }
            regimenEntryNoneStandard.put("regimenLine", getRegimenLine(patient));
            regimenEntryNoneStandard.put("program", "ARV");
            regimenEntryNoneStandard.put("groupCodeName", "");
            regimenEntryNoneStandard.put("conceptRef", "");
            regimenEntryNoneStandard.put("orderSetComponents", components);
            regimensNoneStandard.add(regimenEntryNoneStandard);

            if(CURRENT_DRUG_NON_STANDARD.equalsIgnoreCase(nonStandard)) {
                return regimensNoneStandard.toString();
            }else {
                for (RegimenConfiguration configuration : Context.getRegisteredComponents(RegimenConfiguration.class)) {
                    try {
                        ClassLoader loader = configuration.getClassLoader();
                        InputStream stream = loader.getResourceAsStream(configuration.getDefinitionsPath());
                        if(stream != null) {
                            return loadDefinitionsFromXML(stream, patient).toString();
                        } else {

                        }



                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException("Unable to load " + configuration.getModuleId() + ":" + configuration.getDefinitionsPath(), ex);
                    }
                }

            }
        }

        return "";
    }

    private String getFrequencyUuIdFromConcept(List<OrderFrequency> frequencies, Concept frequencyConcept) {
        for (OrderFrequency frequency : frequencies) {
            if (frequency.getConcept().equals(frequencyConcept))
                return frequency.getUuid();
        }
        return null;
    }

    private Integer getDrugIdFromConcept(Concept concept) {
        List<Drug> drugs = Context.getConceptService().getDrugs(String.valueOf(concept.getConceptId()));
        if (drugs != null && drugs.size() > 0) {
            return drugs.get(0).getDrugId();
        }
        return null;
    }

    public ArrayNode loadDefinitionsFromXML(InputStream stream, Patient patient) throws ParserConfigurationException, IOException, SAXException {

        OrderSetService setService = Context.getOrderSetService();
        // current patients current regimen
        Map<String,Encounter> currentRegimens = getLastRegimenChangeEncounters(patient);
        if (currentRegimens == null)
            return null;

        // get orderFrequency ids
        List<OrderFrequency> frequencyList = Context.getOrderService().getOrderFrequencies(false);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbFactory.newDocumentBuilder();

        Document document = builder.parse(stream);

        Element root = document.getDocumentElement();
        ArrayNode regimens = JsonNodeFactory.instance.arrayNode();

        // Parse each category
        NodeList categoryNodes = root.getElementsByTagName("category");

        for (Map.Entry<String, Encounter> activeProgram : currentRegimens.entrySet()) {
            String key = activeProgram.getKey();
            Encounter enc = activeProgram.getValue();
            String activeRegimenConceptRef = getRegimenConceptRefFromObsList(enc.getObs());

            for (int c = 0; c < categoryNodes.getLength(); c++) {
                // extract regimens categories i.e. arv, tb
                Element categoryElement = (Element) categoryNodes.item(c);
                String categoryCode = categoryElement.getAttribute("code");

                // skip if not does not match current program code
                if (!key.equals(categoryCode))
                    continue;

                // extracts category drugs i.e. arv drugs
                Map<String, DrugReference> categoryDrugs = new HashMap<String, DrugReference>();

                // extracts category groups i.e. adult first line etc
                // Parse all drug concepts for this category
                NodeList drugNodes = categoryElement.getElementsByTagName("drug");
                for (int d = 0; d < drugNodes.getLength(); d++) {
                    Element drugElement = (Element) drugNodes.item(d);
                    String drugCode = drugElement.getAttribute("code");
                    String drugConceptUuid = drugElement.hasAttribute("conceptUuid") ? drugElement.getAttribute("conceptUuid") : null;
                    String drugDrugUuid = drugElement.hasAttribute("drugUuid") ? drugElement.getAttribute("drugUuid") : null;

                    DrugReference drug = (drugDrugUuid != null) ? DrugReference.fromDrugUuid(drugDrugUuid) : DrugReference.fromConceptUuid(drugConceptUuid);
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

                    // Parse all regimen definitions for this group
                    NodeList regimenNodes = groupElement.getElementsByTagName("regimen");
                    for (int r = 0; r < regimenNodes.getLength(); r++) {
                        Element regimenElement = (Element) regimenNodes.item(r);
                        String name = regimenElement.getAttribute("name");
                        String conceptRef = regimenElement.getAttribute("conceptRef");
                        String orderSetRef = regimenElement.getAttribute("orderSetRef");
                        if (!conceptRef.equals(activeRegimenConceptRef))
                            continue;
                        CurrentRegimen cr = new CurrentRegimen(categoryCode, conceptRef, groupName, orderSetRef, name);

                        // Parse all components for this regimen
                        NodeList componentNodes = regimenElement.getElementsByTagName("component");
                        ConceptService conceptService = Context.getConceptService();
                        ObjectNode regimenEntry = JsonNodeFactory.instance.objectNode();
                        ArrayNode components = JsonNodeFactory.instance.arrayNode();
                        for (int p = 0; p < componentNodes.getLength(); p++) {
                            Element componentElement = (Element) componentNodes.item(p);

                            String drugCode = componentElement.getAttribute("drugCode");
                            Double dose = componentElement.hasAttribute("dose") ? Double.parseDouble(componentElement.getAttribute("dose")) : null;
                            Concept units = componentElement.hasAttribute("units") ? conceptService.getConcept(RegimenConversionUtil.getConceptIdFromDoseUnitString(componentElement.getAttribute("units"))) : null;
                            Concept frequency = componentElement.hasAttribute("frequency") ? conceptService.getConcept(RegimenConversionUtil.getConceptIdFromFrequencyString(componentElement.getAttribute("frequency"))) : null;

                            String orderFrequencyUuId = null;
                            if (frequency != null)
                                orderFrequencyUuId = getFrequencyUuIdFromConcept(frequencyList, frequency);
                            DrugReference drug = categoryDrugs.get(drugCode);

                            if (drug == null)
                                throw new RuntimeException("Regimen component references invalid drug: " + drugCode);


                            ObjectNode regimenComponent = JsonNodeFactory.instance.objectNode();
                            regimenComponent.put("name", drugCode);
                            regimenComponent.put("dose", componentElement.getAttribute("dose") != null ? componentElement.getAttribute("dose") : "");
                            regimenComponent.put("units", componentElement.getAttribute("units") != null ? componentElement.getAttribute("units") : "");
                            regimenComponent.put("units_uuid", units != null ? units.getUuid() : "");
                            regimenComponent.put("frequency", orderFrequencyUuId != null ? orderFrequencyUuId : "");
                            regimenComponent.put("drug_id", getDrugIdFromConcept(drug.getConcept()) != null ? String.valueOf(getDrugIdFromConcept(drug.getConcept())) : "");

                            components.add(regimenComponent);
                        }

                        regimenEntry.put("regimenName", cr.getName());
                        regimenEntry.put("regimenLine", getRegimenLine(patient));
                        regimenEntry.put("program", cr.getProgram());
                        regimenEntry.put("groupCodeName", cr.getRegimenGroup());
                        regimenEntry.put("conceptRef", cr.getConceptRef());
                        regimenEntry.put("orderSetId", (cr.getOrderSetRef() != null && !"".equals(cr.getOrderSetRef())) ? String.valueOf(setService.getOrderSetByUuid(cr.getOrderSetRef()).getOrderSetId()): "");
                        regimenEntry.put("orderSetComponents", components);
                        regimens.add(regimenEntry);

                    }

                }
            }
        }
        return regimens;
    }

    private String getRegimenConceptRefFromObsList(Set<Obs> obsList) {

        String CURRENT_DRUGS = "1193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        for (Obs obs : obsList) {
            if (obs.getConcept().getUuid().equals(CURRENT_DRUGS)) {
                return (obs.getValueCoded() != null) ? obs.getValueCoded().getUuid() : null;
            }
        }
        return null;
    }
    private Map<String, Encounter> getLastRegimenChangeEncounters(Patient patient) {

        String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        List<String> treatmentConcepts = Arrays.asList(
                ARV_TREATMENT_PLAN_EVENT_CONCEPT,
                TB_TREATMENT_PLAN_CONCEPT);
        Map<String, Encounter> changeEncounters = new HashMap<String, Encounter>();
        EncounterService encounterService = Context.getEncounterService();
        FormService formService = Context.getFormService();
        EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.DRUG_REGIMEN_EDITOR);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);
        List<Encounter> encounters = EmrUtils.AllEncounters(patient, et, form);
        List<Encounter> hivEncounters = new ArrayList<Encounter>();
        List<Encounter> tbEncounters = new ArrayList<Encounter>();

        for (String categoryConceptUuid : treatmentConcepts) {
            if (encounters != null && encounters.size() > 0) {
                for (Encounter e : encounters) {
                    Set<Obs> obs = e.getObs();
                    if (programEncounterMatching(obs, categoryConceptUuid)) {
                        if (categoryConceptUuid.equals(ARV_TREATMENT_PLAN_EVENT_CONCEPT)) {
                            hivEncounters.add(e);
                        } else {
                            tbEncounters.add(e);
                        }
                    }
                }
            }
        }


        if (hivEncounters != null && hivEncounters.size() > 0) {
            Collections.sort(hivEncounters, new Comparator<Encounter>() {
                @Override
                public int compare(Encounter u1, Encounter u2) {
                    return u2.getEncounterDatetime().compareTo(u1.getEncounterDatetime());
                }
            });
            changeEncounters.put("ARV", hivEncounters.get(0));
        }
        if (tbEncounters != null && tbEncounters.size() >0) {
            Collections.sort(tbEncounters, new Comparator<Encounter>() {
                @Override
                public int compare(Encounter u1, Encounter u2) {
                    return u2.getEncounterDatetime().compareTo(u1.getEncounterDatetime());
                }
            });
            changeEncounters.put("TB", tbEncounters.get(0));
        }
        return changeEncounters;
    }

    private String getRegimenLine(Patient patient) {
        String REGIMEN_LINE_CONCEPT = "163104AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; // concept should be changed to correct one
        String regimenLine = null;
        Encounter lastRegimenEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");
        if (lastRegimenEncounter != null) {
            for(Obs obs:lastRegimenEncounter.getObs()) {
                if (obs.getConcept() != null && obs.getConcept().getUuid().equals(REGIMEN_LINE_CONCEPT)) {
                    if(obs.getValueText() != null){
                        if (obs.getValueText().equals("AF")) {
                            regimenLine = "Adult first line";
                        } else if (obs.getValueText().equals("AS")) {
                            regimenLine = "Adult second line";
                        } else if (obs.getValueText().equals("AT")) {
                            regimenLine = "Adult third line";
                        } else if (obs.getValueText().equals("CF")) {
                            regimenLine = "Child first line";
                        } else if (obs.getValueText().equals("CS")) {
                            regimenLine = "Child second line";
                        } else if (obs.getValueText().equals("CT")) {
                            regimenLine = "Child third line";
                        }
                    }
                }
            }
        }
        return  regimenLine;
    }

    private boolean programEncounterMatching(Set<Obs> obs, String conceptUuidToMatch) {
        for (Obs o : obs) {
            if (o.getConcept().getUuid().equals(conceptUuidToMatch)) {
                return true;
            }
        }
        return false;
    }

    class CurrentRegimen {
        String program;
        String conceptRef;
        String name;
        String regimenGroup;
        String orderSetRef;
        List<SimpleObject> components;

        public CurrentRegimen(String program, String conceptRef, String regimenGroup, String orderSetUuid, String name) {
            this.program = program;
            this.conceptRef = conceptRef;
            this.regimenGroup = regimenGroup;
            this.orderSetRef = orderSetUuid;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getConceptRef() {
            return conceptRef;
        }

        public void setConceptRef(String conceptRef) {
            this.conceptRef = conceptRef;
        }

        public String getRegimenGroup() {
            return regimenGroup;
        }

        public void setRegimenGroup(String regimenGroup) {
            this.regimenGroup = regimenGroup;
        }

        public List<SimpleObject> getComponents() {
            return components;
        }

        public void setComponents(List<SimpleObject> components) {
            this.components = components;
        }

        public String getOrderSetRef() {
            return orderSetRef;
        }

        public void setOrderSetRef(String orderSetRef) {
            this.orderSetRef = orderSetRef;
        }
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    private Integer getDrugForConceptId(Integer conceptId) {

        Map<Integer, Integer> drugForConcept = new HashMap<Integer, Integer>();
        drugForConcept.put(84309, 2135);
        drugForConcept.put(86663, 2209);
        drugForConcept.put(84795, 2153);
        drugForConcept.put(78643, 1815);
        drugForConcept.put(70057, 1324);
        drugForConcept.put(75628, 1645);
        drugForConcept.put(74807, 1605);
        drugForConcept.put(80586, 1940);
        drugForConcept.put(75523, 1641);
        drugForConcept.put(79040, 1859);
        drugForConcept.put(83412, 2102);
        drugForConcept.put(71648, 1431);
        drugForConcept.put(159810, 2211);
        drugForConcept.put(154378, 2214);
        drugForConcept.put(74258, 2219);
        drugForConcept.put(164967, 2223);

        return drugForConcept.get(conceptId);
    }
}
