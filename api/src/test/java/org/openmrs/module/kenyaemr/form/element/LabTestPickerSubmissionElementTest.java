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

package org.openmrs.module.kenyaemr.form.element;

import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Tests for {@link LabTestPickerSubmissionElement}
 */
public class LabTestPickerSubmissionElementTest extends BaseModuleContextSensitiveTest {

	private LabTestPickerSubmissionElement element;

	private MockHttpServletRequest request;

	private FormEntryContext context;

	private Concept cd4, freeText, fpMethod, fpNatural, otherNonCoded;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		// Setup mock request
		request = new MockHttpServletRequest();
		request.addParameter("lab-test-160632", "test \"this\"");
		request.addParameter("lab-test-160632-errorid", "ke-element-1-error");
		request.addParameter("lab-test-5497", "123.4");
		request.addParameter("lab-test-5497-errorid", "ke-element-2-error");
		request.addParameter("lab-test-374", "5277");
		request.addParameter("lab-test-374-errorid", "ke-element-3-error");
		request.addParameter("lab-test-374", "5622");
		request.addParameter("lab-test-374-errorid", "ke-element-4-error");

		// Setup form context
		context = new FormEntryContext(FormEntryContext.Mode.ENTER);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("dynamicObsContainerId", "lab-test");

		element = new LabTestPickerSubmissionElement(context, parameters);

		freeText = Dictionary.getConcept("160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // Text
		cd4 = Dictionary.getConcept("5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // Numeric
		fpMethod = Dictionary.getConcept("374AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // Coded
		fpNatural = Dictionary.getConcept("5277AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // Answer #1
		otherNonCoded = Dictionary.getConcept("5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // Answer #2
	}

	@Test
	public void validateSubmission() {
		// Check with no errors
		Collection<FormSubmissionError> errors = element.validateSubmission(context, request);
		Assert.assertEquals(0, errors.size());

		// Check with number format error
		request = new MockHttpServletRequest();
		request.addParameter("lab-test-5497", "123.4");
		request.addParameter("lab-test-5497-errorid", "test-error-id-1");
		request.addParameter("lab-test-5497", "xx123.4");
		request.addParameter("lab-test-5497-errorid", "test-error-id-2");

		errors = element.validateSubmission(context, request);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("test-error-id-2", errors.iterator().next().getId());
	}

	@Test
	public void getSubmittedTestConcepts() {
		Set<Concept> concepts = element.getSubmittedTestConcepts(request);
		Assert.assertEquals(3, concepts.size());
		Assert.assertTrue(concepts.contains(freeText));
		Assert.assertTrue(concepts.contains(cd4));
		Assert.assertTrue(concepts.contains(fpMethod));
	}

	@Test
	public void getSubmittedValueCountForConcept() {
		Assert.assertEquals(1, element.getSubmittedValueCountForConcept(request, freeText));
		Assert.assertEquals(1, element.getSubmittedValueCountForConcept(request, cd4));
		Assert.assertEquals(2, element.getSubmittedValueCountForConcept(request, fpMethod));
	}

	@Test
	public void getSubmittedValueForConcept() {
		Assert.assertEquals("test \"this\"", element.getSubmittedValueForConcept(request, freeText, 0));
		Assert.assertEquals(new Double(123.4), element.getSubmittedValueForConcept(request, cd4, 0));
		Assert.assertEquals(fpNatural, element.getSubmittedValueForConcept(request, fpMethod, 0));
		Assert.assertEquals(otherNonCoded, element.getSubmittedValueForConcept(request, fpMethod, 1));
	}

	@Test
	public void getSubmittedErrorIdsForConcept() {
		List<String> errorIds = element.getSubmittedErrorIdsForConcept(request, freeText);
		Assert.assertEquals(Arrays.asList("ke-element-1-error"), errorIds);
		errorIds = element.getSubmittedErrorIdsForConcept(request, cd4);
		Assert.assertEquals(Arrays.asList("ke-element-2-error"), errorIds);
		errorIds = element.getSubmittedErrorIdsForConcept(request, fpMethod);
		Assert.assertEquals(Arrays.asList("ke-element-3-error", "ke-element-4-error"), errorIds);
	}

	@Test
	public void getTestFieldName() {
		Assert.assertEquals("lab-test-5497", element.getTestFieldName(cd4));
	}

	@Test
	public void getTestFieldConcept() {
		Assert.assertEquals(cd4, element.getTestFieldConcept("lab-test-5497"));
	}

	@Test
	public void getObsValueJS() {
		Obs o = new Obs();
		o.setConcept(freeText);
		o.setValueText("test \"yes\"");
		Assert.assertEquals("\"test \\\"yes\\\"\"", element.getObsValueJS(o));

		o = new Obs();
		o.setConcept(cd4);
		o.setValueNumeric(123.4);
		Assert.assertEquals("123.4", element.getObsValueJS(o));

		o = new Obs();
		o.setConcept(fpMethod);
		o.setValueCoded(cd4);
		Assert.assertEquals("5497", element.getObsValueJS(o));
	}
}