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
import org.openmrs.OrderFrequency;
import org.openmrs.OrderSet;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegimenJsonGenerator {

    public String generateRegimenJsonFromRegimensConfigFile() {
        for (RegimenConfiguration configuration : Context.getRegisteredComponents(RegimenConfiguration.class)) {
            try {
                ClassLoader loader = configuration.getClassLoader();
                InputStream stream = loader.getResourceAsStream(configuration.getDefinitionsPath());

                return loadDefinitionsFromXML(stream).toString();

            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Unable to load " + configuration.getModuleId() + ":" + configuration.getDefinitionsPath(), ex);
            }
        }
        return "";
    }

    private String getFrequencyUuIdFromConcept(List<OrderFrequency> frequencies, Concept frequencyConcept) {
        for(OrderFrequency frequency : frequencies) {
            if(frequency.getConcept().equals(frequencyConcept))
                return frequency.getUuid();
        }
        return null;
    }

    private Integer getDrugIdFromConcept(Concept concept) {
        List<Drug> drugs =  Context.getConceptService().getDrugs(String.valueOf(concept.getConceptId()));
        if (drugs != null && drugs.size() > 0) {
            return drugs.get(0).getDrugId();
        }
        return null;
    }

    public ObjectNode loadDefinitionsFromXML(InputStream stream) throws ParserConfigurationException, IOException, SAXException {

        // get order sets
        List<OrderSet> orderSetList = getOrderSets();
        System.out.println("Ordersets: " + orderSetList);
        ObjectNode regimenJson = JsonNodeFactory.instance.objectNode();
        ArrayNode regimenArray = JsonNodeFactory.instance.arrayNode();
        // get orderFrequency ids
        List<OrderFrequency> frequencyList = Context.getOrderService().getOrderFrequencies(false);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbFactory.newDocumentBuilder();

        Document document = builder.parse(stream);

        Element root = document.getDocumentElement();

        // Parse each category
        NodeList categoryNodes = root.getElementsByTagName("category");
        for (int c = 0; c < categoryNodes.getLength(); c++) {
            // extract regimens categories i.e. arv, tb
            Element categoryElement = (Element)categoryNodes.item(c);
            String categoryCode = categoryElement.getAttribute("code");


            // extracts category drugs i.e. arv drugs
            Map<String, DrugReference> categoryDrugs = new HashMap<String, DrugReference>();

            // extracts category groups i.e. adult first line etc
            List<RegimenDefinitionGroup> categoryGroups = new ArrayList<RegimenDefinitionGroup>();

            // Parse all drug concepts for this category
            NodeList drugNodes = categoryElement.getElementsByTagName("drug");
            for (int d = 0; d < drugNodes.getLength(); d++) {
                Element drugElement = (Element)drugNodes.item(d);
                String drugCode = drugElement.getAttribute("code");
                String drugConceptUuid = drugElement.hasAttribute("conceptUuid") ? drugElement.getAttribute("conceptUuid") : null;
                String drugDrugUuid = drugElement.hasAttribute("drugUuid") ? drugElement.getAttribute("drugUuid") : null;

                DrugReference drug = (drugDrugUuid != null) ? DrugReference.fromDrugUuid(drugDrugUuid) : DrugReference.fromConceptUuid(drugConceptUuid);
                if (drug != null) {
                    categoryDrugs.put(drugCode, drug);
                }
            }

            ObjectNode programRegimen = JsonNodeFactory.instance.objectNode();
            ArrayNode programRegimenArray = JsonNodeFactory.instance.arrayNode();
            // Parse all groups for this category
            NodeList groupNodes = categoryElement.getElementsByTagName("group");
            for (int g = 0; g < groupNodes.getLength(); g++) {
                Element groupElement = (Element)groupNodes.item(g);
                String groupCode = groupElement.getAttribute("code");
                String groupName = groupElement.getAttribute("name");

                RegimenDefinitionGroup group = new RegimenDefinitionGroup(groupCode, groupName);
                categoryGroups.add(group);

                ObjectNode regimenGroup = JsonNodeFactory.instance.objectNode();
                ArrayNode regimenGroupArray = JsonNodeFactory.instance.arrayNode();


                // Parse all regimen definitions for this group
                NodeList regimenNodes = groupElement.getElementsByTagName("regimen");
                for (int r = 0; r < regimenNodes.getLength(); r++) {
                    Element regimenElement = (Element)regimenNodes.item(r);
                    String name = regimenElement.getAttribute("name");
                    ObjectNode regimen = JsonNodeFactory.instance.objectNode();
                    RegimenDefinition regimenDefinition = new RegimenDefinition(name, group);

                    // Parse all components for this regimen
                    NodeList componentNodes = regimenElement.getElementsByTagName("component");
                    ConceptService conceptService = Context.getConceptService();
                    ArrayNode regimenComponentsArray = JsonNodeFactory.instance.arrayNode();
                    for (int p = 0; p < componentNodes.getLength(); p++) {
                        Element componentElement = (Element)componentNodes.item(p);
                        ObjectNode drugMemberObject = JsonNodeFactory.instance.objectNode();

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

                        drugMemberObject.put("name", drugCode);
                        drugMemberObject.put("dose",  componentElement.getAttribute("dose") != null ? componentElement.getAttribute("dose"): "");
                        drugMemberObject.put("units", componentElement.getAttribute("units") != null ? componentElement.getAttribute("units") : "");
                        drugMemberObject.put("units_uuid", units != null ? units.getUuid() : "");
                        drugMemberObject.put("frequency", orderFrequencyUuId != null ? orderFrequencyUuId : "");
                        drugMemberObject.put("drug_id", getDrugIdFromConcept(drug.getConcept()) != null ? String.valueOf(getDrugIdFromConcept(drug.getConcept())) : "");
                        regimenComponentsArray.add(drugMemberObject);
                        regimenDefinition.addComponent(drug, dose, units, frequency);
                    }

                    group.addRegimen(regimenDefinition);
                    regimen.put("name", name);
                    regimen.put("components", regimenComponentsArray);
                    regimen.put("orderSetId", getOrdersetIdFromList(name, orderSetList));
                    // add order set id


                    regimenGroupArray.add(regimen);
                }

                regimenGroup.put("name", groupName );
                regimenGroup.put("regimens", regimenGroupArray);

                programRegimenArray.add(regimenGroup);

            }

            programRegimen.put("name", categoryCode);
            programRegimen.put("regimen_lines", programRegimenArray);
            regimenArray.add(programRegimen);
        }

        regimenJson.put("programs", regimenArray);
        return regimenJson;
    }

    private Integer getOrdersetIdFromList(String ordersetname, List<OrderSet> orderSets) {

        if (ordersetname == null || ordersetname.equals("") || orderSets == null || orderSets.size() == 0)
            return null;
        for (OrderSet set : orderSets) {
            if (set.getName() != null && ordersetname.trim().equals(set.getName().trim()))
                return set.getOrderSetId();
        }
        return null;
    }

    private List<OrderSet> getOrderSets() {
        return Context.getOrderSetService().getOrderSets(false);
    }

}
