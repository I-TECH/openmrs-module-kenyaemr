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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.lab.LabManager;
import org.openmrs.module.kenyacore.lab.LabTestDefinition;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 */
public class LabTestPickerSubmissionElement implements HtmlGeneratorElement, FormSubmissionControllerAction {

	protected static final Log log = LogFactory.getLog(LabTestPickerSubmissionElement.class);

	private String dynamicObsContainerId;

	private Set<Concept> excludeTests;

	protected Map<Concept, List<Obs>> existingObss = new LinkedHashMap<Concept, List<Obs>>();

	public LabTestPickerSubmissionElement(FormEntryContext context, Map<String, String> parameters) {

		// The DOM element id of the container for dynamic obs fields
		dynamicObsContainerId = parameters.get("dynamicObsContainerId");

		// The set of test concepts to exclude
		if (parameters.containsKey("excludeTests")) {
			excludeTests = new HashSet(EmrUtils.parseConcepts(parameters.get("excludeTests")));
		}
		else {
			excludeTests = new HashSet<Concept>();
		}

		LabManager labManager = CoreContext.getInstance().getManager(LabManager.class);

		// Claim all relevant existing concept/obs
		if (!FormEntryContext.Mode.ENTER.equals(context.getMode())) {
			for (Map.Entry<Concept, List<Obs>> existingForConcept : context.getExistingObs().entrySet()) {
				Concept testConcept = existingForConcept.getKey();
				List<Obs> testObss = existingForConcept.getValue();

				if (labManager.isLabTest(testConcept) && !excludeTests.contains(testConcept)) {
					existingObss.put(testConcept, new ArrayList<Obs>(testObss));
				}
			}

			// Remove claimed concepts so other tags can't bind to them
			for (Concept c : existingObss.keySet()) {
				context.removeExistingObs(c, (Concept) null);
			}
		}
	}

	@Override
	public String generateHtml(FormEntryContext context) {
		StringBuilder sb = new StringBuilder();
		boolean viewMode = context.getMode().equals(FormEntryContext.Mode.VIEW);

		LabManager labManager = CoreContext.getInstance().getManager(LabManager.class);

		if (!viewMode) {
			// Generate HTML for new test control
			sb.append("<span>\n");
			sb.append("  <select id=\"ke-lab-testlist\">\n");

			for (String category : labManager.getCategories()) {
				sb.append("    <optgroup label=\"" + category + "\">\n");

				for (LabTestDefinition labTest : labManager.getTests(category)) {
					Concept testConcept = labTest.getConcept().getTarget();
					if (!excludeTests.contains(labTest.getConcept())) {
						sb.append("      <option value=\"" + testConcept.getConceptId() + "\">" + testConcept.getName() + "</option>\n");
					}
				}

				sb.append("    </optgroup>\n");
			}

			sb.append("  </select>\n");
			sb.append("  <input type=\"button\" value=\"Add\" id=\"ke-lab-addnew\" />\n");
			sb.append("</span>\n");
		}

		// Generate script block
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("\n");
		sb.append("  function ke_labAddNewTest(conceptId, initialValue) {\n");
		sb.append("    var fieldName = '" + dynamicObsContainerId + "-' + conceptId;\n");
		sb.append("    kenyaemr.dynamicObsField('" + dynamicObsContainerId + "', fieldName, conceptId, initialValue, " + (viewMode ? "true" : "false") + ");\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  $j(function() {\n");

		if (!viewMode) {
			sb.append("    $j('#ke-lab-addnew').click(function() {\n");
			sb.append("      var newConceptId = $j('#ke-lab-testlist').val();\n");
			sb.append("      ke_labAddNewTest(newConceptId, null);\n");
			sb.append("    });\n");
		}

		sb.append("\n");

		for (Concept existingConcept : existingObss.keySet()) {
			for (Obs existingObs : existingObss.get(existingConcept)) {
				sb.append("    ke_labAddNewTest(" + existingConcept + ", " + getObsValueJS(existingObs) + ");\n");
			}
		}

		sb.append("\n");
		sb.append("  });\n");
		sb.append("\n");
		sb.append("</script>\n");

		return sb.toString();
	}

	@Override
	public Collection<FormSubmissionError> validateSubmission(FormEntryContext context, HttpServletRequest request) {
		List<FormSubmissionError> errors = new ArrayList<FormSubmissionError>();

		for (Concept concept : getSubmittedTestConcepts(request)) {
			for (int v = 0; v < getSubmittedValueCountForConcept(request, concept); ++v) {
				try {
					getSubmittedValueForConcept(request, concept, v);
				}
				catch (Exception ex) {
					List<String> errorIds = getSubmittedErrorIdsForConcept(request, concept);
					errors.add(new FormSubmissionError(errorIds.get(v), ex.getMessage()));
				}
			}
		}

		return errors;
	}

	@Override
	public void handleSubmission(FormEntrySession session, HttpServletRequest request) {
		// Handle each concept in turn
		for (Concept concept : getSubmittedTestConcepts(request)) {
			List<Obs> availableObssForConcept = existingObss.containsKey(concept) ? new ArrayList<Obs>(existingObss.get(concept)) : new ArrayList<Obs>();

			for (int v = 0; v < getSubmittedValueCountForConcept(request, concept); ++v) {
				Object value = getSubmittedValueForConcept(request, concept, v);
				Obs existingObs = null;

				// Look for an existing obs to modify
				if (availableObssForConcept.size() > 0) {
					existingObs = availableObssForConcept.get(0);
					availableObssForConcept.remove(0);
				}

				if (existingObs != null && session.getContext().getMode() == FormEntryContext.Mode.EDIT) {
					session.getSubmissionActions().modifyObs(existingObs, concept, value, null, null, null);
				} else if (value != null) {
					session.getSubmissionActions().createObs(concept, value, null, null, null);
				}
			}

			// Void any remaining obs for this concept
			for (Obs leftOverObsForConcept : availableObssForConcept) {
				session.getSubmissionActions().modifyObs(leftOverObsForConcept, concept, null, null, null, null);
			}
		}
	}

	/**
	 * Gets the test concepts submitted
	 * @param request the request
	 * @return the list of concepts
	 */
	protected Set<Concept> getSubmittedTestConcepts(HttpServletRequest request) {
		Set<Concept> concepts = new HashSet<Concept>();

		Map<String, String[]> params = request.getParameterMap();

		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String paramName = entry.getKey();

			if (paramName.startsWith(dynamicObsContainerId) && !paramName.endsWith("-errorid")) {
				Concept concept = getTestFieldConcept(paramName);
				concepts.add(concept);
			}
		}

		return concepts;
	}

	/**
	 * Gets the number of values submitted for the given test concept
	 * @param request the request
	 * @return the number of test values
	 */
	protected int getSubmittedValueCountForConcept(HttpServletRequest request, Concept concept) throws NumberFormatException {
		String fieldName = getTestFieldName(concept);
		String[] paramValues = request.getParameterValues(fieldName);
		return paramValues != null ? paramValues.length : 0;
	}

	/**
	 * Gets the nth value submitted for the given test concept
	 * @param request the request
	 * @param concept the concept
	 * @param index the value index
	 * @return the test value
	 * @throws NumberFormatException
	 */
	protected Object getSubmittedValueForConcept(HttpServletRequest request, Concept concept, int index) throws NumberFormatException {
		String fieldName = getTestFieldName(concept);
		String[] paramValues = request.getParameterValues(fieldName);
		String paramValue = paramValues[index];

		if (StringUtils.isEmpty(paramValue)) {
			return null;
		}
		else if (concept.getDatatype().isText()) {
			return paramValue;
		}
		else if (concept.getDatatype().isNumeric()) {
			return Double.parseDouble(paramValue);
		}
		else if (concept.getDatatype().isCoded()) {
			return Context.getConceptService().getConcept(Integer.valueOf(paramValue));
		}
		else {
			throw new RuntimeException("Obs concept not of type text|numeric|coded");
		}
	}

	/**
	 * Utility method to get the test field name to use with a given concept
	 * @param concept the concept
	 * @return the field name
	 */
	protected String getTestFieldName(Concept concept) {
		return dynamicObsContainerId + "-" + concept.getConceptId();
	}

	/**
	 * Gets the dynamic error field names from the request
	 * @param concept the concept the concept
	 * @return error field ids
	 */
	protected List<String> getSubmittedErrorIdsForConcept(HttpServletRequest request, Concept concept) {
		String errorIdLookupField = getTestFieldName(concept) + "-errorid";
		String[] ids = request.getParameterValues(errorIdLookupField);
		return ids != null ? Arrays.asList(ids) : null;
	}

	/**
	 * Parses a concept from the end of a test field name
	 * @param fieldName the field name
	 * @return the concept
	 */
	protected Concept getTestFieldConcept(String fieldName) {
		String fieldNamePrefix = dynamicObsContainerId + "-";
		String conceptId = fieldName.substring(fieldNamePrefix.length());
		return Context.getConceptService().getConcept(Integer.valueOf(conceptId));
	}

	/**
	 * Gets the value of an obs as JS literal (text -> quoted string, numeric -> double, coded -> int)
	 * @param obs the obs
	 * @return the string value
	 */
	protected static String getObsValueJS(Obs obs) {
		ConceptDatatype type = obs.getConcept().getDatatype();

		if (type.isText()) {
			return "\"" + obs.getValueText().replace("\"", "\\\"") + "\"";
		} else if (type.isNumeric()) {
			return obs.getValueNumeric().toString();
		} else if (type.isCoded()) {
			return obs.getValueCoded().getConceptId().toString();
		} else {
			throw new RuntimeException("Obs concept not of type text|numeric|coded");
		}
	}
}