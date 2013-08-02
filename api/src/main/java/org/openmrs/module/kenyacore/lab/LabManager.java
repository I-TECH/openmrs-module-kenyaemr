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

package org.openmrs.module.kenyacore.lab;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.ContentManager;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lab manager
 */
@Component
public class LabManager implements ContentManager {

	private Map<String, List<LabTestDefinition>> tests = new LinkedHashMap<String, List<LabTestDefinition>>();

	/**
	 * Gets the categories
	 * @return the list of categories
	 */
	public List<String> getCategories() {
		List<String> categories = new ArrayList<String>();
		categories.addAll(tests.keySet());
		return categories;
	}

	/**
	 * Gets the lab tests for the given category
	 * @param category
	 * @return the list of tests
	 */
	public List<LabTestDefinition> getTests(String category) {
		return tests.get(category);
	}

	/**
	 * Gets whether the given concept is a registered lab test concept
	 * @param concept the concept
	 * @return true if concept is a lab test
	 */
	public boolean isLabTest(Concept concept) {
		for (Map.Entry<String, List<LabTestDefinition>> entry : tests.entrySet()) {
			for (LabTestDefinition test : entry.getValue()) {
				if (test.getConcept().equals(concept)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Reloads all lab data from configurations
	 */
	@Override
	public synchronized void refresh() {
		tests.clear();

		for (LabConfiguration configuration : Context.getRegisteredComponents(LabConfiguration.class)) {
			try {
				ClassLoader loader = configuration.getClassLoader();
				InputStream stream = loader.getResourceAsStream(configuration.getDefinitionsPath());

				loadTestsFromXML(stream);
			}
			catch (Exception ex) {
				throw new RuntimeException("Unable to load " + configuration.getModuleId() + ":" + configuration.getDefinitionsPath(), ex);
			}
		}
	}

	/**
	 * Loads the lab definitions from an XML resource
	 * @param stream the stream containing the XML
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public void loadTestsFromXML(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();

		Document document = builder.parse(stream);

		Element root = document.getDocumentElement();

		// Parse each category
		NodeList categoryNodes = root.getElementsByTagName("category");
		for (int c = 0; c < categoryNodes.getLength(); c++) {
			Element categoryElement = (Element) categoryNodes.item(c);
			String categoryName = categoryElement.getAttribute("name");

			List<LabTestDefinition> testsForCategory = new ArrayList<LabTestDefinition>();

			// Parse all tests for this category
			NodeList testNodes = categoryElement.getElementsByTagName("test");
			for (int t = 0; t < testNodes.getLength(); t++) {
				Element testElement = (Element) testNodes.item(t);
				String testConceptIdentifier = testElement.getAttribute("concept");

				LabTestDefinition testDefinition = new LabTestDefinition(testConceptIdentifier);

				testsForCategory.add(testDefinition);
			}

			tests.put(categoryName, testsForCategory);
		}
	}
}
