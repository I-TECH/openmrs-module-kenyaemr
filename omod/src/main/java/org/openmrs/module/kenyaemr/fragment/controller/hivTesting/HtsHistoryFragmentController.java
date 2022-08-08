/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.hivTesting;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.wrapper.VisitWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visit summary fragment
 */
public class HtsHistoryFragmentController {

	ConceptService conceptService = Context.getConceptService();
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {

		Form htsInitialForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_INITIAL_TEST);
		Form htsRetestForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_CONFIRMATORY_TEST);
		Form htsLinkageForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_LINKAGE);

		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Arrays.asList(htsInitialForm, htsRetestForm), null, null, null, null, false);
		List<Encounter> linkageEncounters = Context.getEncounterService().getEncounters(patient, null, null, null, Arrays.asList(htsLinkageForm), null, null, null, null, false);

		Collections.reverse(encounters);
		Collections.reverse(linkageEncounters);

		List<SimpleObject> encDetails = new ArrayList<SimpleObject>();
		List<SimpleObject> linkageList = new ArrayList<SimpleObject>();
		for (Encounter e : encounters) {
			SimpleObject o = getEncDetails(e.getObs(), e);
			encDetails.add(o);
		}

		// get linkage encounters
		for (Encounter e : linkageEncounters) {
			SimpleObject o = getLinkageDetails(e.getObs(), e);
			linkageList.add(o);
		}

		model.put("encounters", encDetails);
		model.put("linkageDetails", linkageList);
	}

	SimpleObject getLinkageDetails (Set<Obs> obsList, Encounter e) {

		Integer facilityLinkedConcept = 162724;
		Integer	upnAssignedConcept = 162053;

		String facilityLinkedTo = null;
		String upn = null;


		for(Obs obs:obsList) {

			if (obs.getConcept().getConceptId().equals(facilityLinkedConcept) ) {
				facilityLinkedTo = obs.getValueText();
			} else if (obs.getConcept().getConceptId().equals(upnAssignedConcept)) { // get age
				upn = String.valueOf(obs.getValueNumeric().intValue());
			}
		}

		return SimpleObject.create(
				"facilityLinkedTo", facilityLinkedTo != null ? facilityLinkedTo : "",
				"upn", upn != null ? upn : "",
				"encDate", DATE_FORMAT.format(e.getEncounterDatetime())
		);

	}


	SimpleObject getEncDetails (Set<Obs> obsList, Encounter e) {

		Integer populationTypeConcept = 164930;
		Integer	htsStrategyConcept = 164956;
		Integer htsEntryPointConcept = 160540;
		Integer finalResultConcept = 159427;

		String populationType = null;
		String htsStrategy = null;
		String entryPoint = null;
		String finalResult = null;


		for(Obs obs:obsList) {

			if (obs.getConcept().getConceptId().equals(populationTypeConcept) ) {
				populationType = popTypeConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(htsStrategyConcept )) { // get age
				htsStrategy = htsStrategyConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(htsEntryPointConcept) ) {
				entryPoint = entryPointConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(finalResultConcept )) { // current HIV status
				finalResult = hivStatusConverter(obs.getValueCoded());
			}
		}

		return SimpleObject.create(
				"popType", populationType != null ? populationType : "",
				"htsStrategy", htsStrategy != null ? htsStrategy : "",
				"entryPoint", entryPoint != null ? entryPoint : "",
				"finalResult", finalResult != null ? finalResult : "",
				"encDate", DATE_FORMAT.format(e.getEncounterDatetime())
		);

	}

	String hivStatusConverter (Concept key) {
		Map<Concept, String> hivStatusList = new HashMap<Concept, String>();
		hivStatusList.put(conceptService.getConcept(703), "Positive");
		hivStatusList.put(conceptService.getConcept(664), "Negative");
		hivStatusList.put(conceptService.getConcept(1405), "Exposed");
		hivStatusList.put(conceptService.getConcept(1067), "Unknown");
		hivStatusList.put(conceptService.getConcept(1138), "Inconclusive");
		return hivStatusList.get(key);
	}

	String popTypeConverter (Concept key) {
		Map<Concept, String> popTypeList = new HashMap<Concept, String>();
		popTypeList.put(conceptService.getConcept(164928), "General Population");
		popTypeList.put(conceptService.getConcept(164929), "Key Population");
		popTypeList.put(conceptService.getConcept(138643), "Priority Population");
		return popTypeList.get(key);

	}

	String htsStrategyConverter (Concept key) {
		Map<Concept, String> htsStrategyList = new HashMap<Concept, String>();
		htsStrategyList.put(conceptService.getConcept(164163), "HP: Hospital Patient Testing");
		htsStrategyList.put(conceptService.getConcept(164953), "NP: HTS for non-patients");
		htsStrategyList.put(conceptService.getConcept(164954), "VI:Integrated VCT Center");
		htsStrategyList.put(conceptService.getConcept(164955), "Stand Alone VCT Center");
		htsStrategyList.put(conceptService.getConcept(159938), "Home Based Testing");
		htsStrategyList.put(conceptService.getConcept(159939), "MO: Mobile Outreach HTS");
		htsStrategyList.put(conceptService.getConcept(161557), "Index testing");
		htsStrategyList.put(conceptService.getConcept(166606), "SNS - Social Networks");
		htsStrategyList.put(conceptService.getConcept(5622), "Other");
		return htsStrategyList.get(key);

	}

	String entryPointConverter (Concept key) {
		Map<Concept, String> entryPointList = new HashMap<Concept, String>();
		entryPointList.put(conceptService.getConcept(5485), "In Patient Department(IPD)");
		entryPointList.put(conceptService.getConcept(160542), "Out Patient Department(OPD)");
		entryPointList.put(conceptService.getConcept(162181), "Peadiatric Clinic");
		entryPointList.put(conceptService.getConcept(160552), "Nutrition Clinic");
		entryPointList.put(conceptService.getConcept(160538), "PMTCT - ANC");
		entryPointList.put(conceptService.getConcept(160456), "PMTCT - MAT");
		entryPointList.put(conceptService.getConcept(1623), "PMTCT - PNC");
		entryPointList.put(conceptService.getConcept(160541), "TB");
		entryPointList.put(conceptService.getConcept(159940), "VCT");
		entryPointList.put(conceptService.getConcept(159938), "Home Based Testing");
		entryPointList.put(conceptService.getConcept(159939), "Mobile Outreach");
		entryPointList.put(conceptService.getConcept(162223), "VMMC");
		entryPointList.put(conceptService.getConcept(160546), "STI Clinic");
		entryPointList.put(conceptService.getConcept(160522), "Emergency");
		entryPointList.put(conceptService.getConcept(163096), "Community Testing");
		entryPointList.put(conceptService.getConcept(5622), "Other");
		return entryPointList.get(key);
	}

}