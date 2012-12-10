/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.regimen;

import org.openmrs.Concept;
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
import java.util.*;

/**
 *
 */
public class RegimenManager {

	private static Map<String, Set<Integer>> drugConceptIds = new LinkedHashMap<String, Set<Integer>>();

	private static Map<String, List<RegimenDefinition>> regimenDefinitions = new HashMap<String, List<RegimenDefinition>>();

	private static int definitionsVersion = 0;

	/**
	 * Gets the category codes
	 * @return the category codes
	 */
	public static Set<String> getCategoryCodes() {
		return drugConceptIds.keySet();
	}

	public static Set<Integer> getDrugConceptIds(String category) {
		return drugConceptIds.get(category);
	}

	public static List<RegimenDefinition> getRegimenDefinitions(String category) {
		return regimenDefinitions.get(category);
	}

	public static int getDefinitionsVersion() {
		return definitionsVersion;
	}

	/**
	 * Loads definitions from an input stream containing XML
	 * @param stream the path to XML resource
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void loadDefinitionsFromXML(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();

		Document document = builder.parse(stream);

		Element root = document.getDocumentElement();
		definitionsVersion = Integer.parseInt(root.getAttribute("version"));

		drugConceptIds.clear();
		regimenDefinitions.clear();

		// Parse each category
		NodeList categoryNodes = root.getElementsByTagName("category");
		for (int c = 0; c < categoryNodes.getLength(); c++) {
			Element categoryElement = (Element)categoryNodes.item(c);
			String categoryCode = categoryElement.getAttribute("code");

			Map<String, Integer> drugIds = new HashMap<String, Integer>();
			List<RegimenDefinition> regimens = new ArrayList<RegimenDefinition>();

			// Parse all drug concepts for this category
			NodeList drugNodes = categoryElement.getElementsByTagName("drug");
			for (int d = 0; d < drugNodes.getLength(); d++) {
				Element drugElement = (Element)drugNodes.item(d);
				String drugCode = drugElement.getAttribute("code");
				String drugConceptUuid = drugElement.getAttribute("conceptUuid");

				Concept drugConcept = Context.getConceptService().getConceptByUuid(drugConceptUuid);

				drugIds.put(drugCode, drugConcept.getConceptId());
			}

			// Parse all regimen definitions for this category
			NodeList regimenNodes = categoryElement.getElementsByTagName("regimen");
			for (int r = 0; r < regimenNodes.getLength(); r++) {
				Element regimenElement = (Element)regimenNodes.item(r);
				String name = regimenElement.getAttribute("name");
				RegimenDefinition.Suitability suitability = RegimenDefinition.Suitability.parse(regimenElement.getAttribute("suitability"));

				RegimenDefinition regimenDefinition = new RegimenDefinition(name, suitability);

				// Parse all components for this regimen
				NodeList componentNodes = regimenElement.getElementsByTagName("component");
				for (int p = 0; p < componentNodes.getLength(); p++) {
					Element componentElement = (Element)componentNodes.item(p);
					String drugCode = componentElement.getAttribute("drugCode");
					double dose = Double.parseDouble(componentElement.getAttribute("dose"));
					String units = componentElement.getAttribute("units");

					Integer drugConceptId = drugIds.get(drugCode);
					if (drugConceptId == null)
						throw new RuntimeException("Regimen component references invalid drug: " + drugCode);

					regimenDefinition.addComponent(drugConceptId, dose, units);
				}

				regimens.add(regimenDefinition);
			}

			drugConceptIds.put(categoryCode, new HashSet<Integer>(drugIds.values()));
			regimenDefinitions.put(categoryCode, regimens);
		}
	}
}