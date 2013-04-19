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

package org.openmrs.module.kenyaemr.lab;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
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
 *
 */
@Component
public class LabManager {

	private Map<String, List<Integer>> tests = new LinkedHashMap<String, List<Integer>>();

	public List<String> getCategories() {
		List<String> categories = new ArrayList<String>();
		categories.addAll(tests.keySet());
		return categories;
	}

	public List<Concept> getTests(String category) {
		List<Concept> testsForCategory = new ArrayList<Concept>();
		for (Integer conceptId : tests.get(category)) {
			testsForCategory.add(Context.getConceptService().getConcept(conceptId));
		}
		return testsForCategory;
	}

	public boolean isLabTest(Concept concept) {
		for (Map.Entry<String, List<Integer>> entry : tests.entrySet()) {
			for (Integer testConceptId : entry.getValue()) {
				if (concept.getConceptId().equals(testConceptId)) {
					return true;
				}
			}
		}

		return false;
	}

	public synchronized void clear() {
		tests.clear();
	}

	public synchronized void loadTestsFromXML(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();

		Document document = builder.parse(stream);

		Element root = document.getDocumentElement();

		// Parse each category
		NodeList categoryNodes = root.getElementsByTagName("category");
		for (int c = 0; c < categoryNodes.getLength(); c++) {
			Element categoryElement = (Element) categoryNodes.item(c);
			String categoryName = categoryElement.getAttribute("name");

			List<Integer> testsForCategory = new ArrayList<Integer>();

			// Parse all tests for this category
			NodeList testNodes = categoryElement.getElementsByTagName("test");
			for (int t = 0; t < testNodes.getLength(); t++) {
				Element testElement = (Element) testNodes.item(t);
				String testConceptUuid = testElement.getAttribute("conceptUuid");

				Concept testConcept = Context.getConceptService().getConceptByUuid(testConceptUuid);
				testsForCategory.add(testConcept.getConceptId());
			}

			tests.put(categoryName, testsForCategory);
		}
	}
}
