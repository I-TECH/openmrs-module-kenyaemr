/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Miscellaneous utility methods
 */
public class EmrUtils {

	/**
	 * Checks whether a date has any time value
	 * @param date the date
	 * @return true if the date has time
	 * @should return true only if date has time
	 */
	public static boolean dateHasTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR) != 0 || cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0 || cal.get(Calendar.MILLISECOND) != 0;
	}

	/**
	 * Checks if a given date is today
	 * @param date the date
	 * @return true if date is today
	 */
	public static boolean isToday(Date date) {
		return DateUtils.isSameDay(date, new Date());
	}

	/**
	 * Converts a WHO stage concept to a WHO stage number
	 * @param c the WHO stage concept
	 * @return the WHO stage number (null if the concept isn't a WHO stage)
	 */
	public static Integer whoStage(Concept c) {
		if (c != null) {
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_PEDS))) {
				return 1;
			}
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_PEDS))) {
				return 2;
			}
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS))) {
				return 3;
			}
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_PEDS))) {
				return 4;
			}
		}
		return null;
	}

	/**
	 * Parses a CSV list of strings, returning all trimmed non-empty values
	 * @param csv the CSV string
	 * @return the concepts
	 */
	public static List<String> parseCsv(String csv) {
		List<String> values = new ArrayList<String>();

		for (String token : csv.split(",")) {
			token = token.trim();

			if (!StringUtils.isEmpty(token)) {
				values.add(token);
			}
		}
		return values;
	}

	/**
	 * Parses a CSV list of concept ids, UUIDs or mappings
	 * @param csv the CSV string
	 * @return the concepts
	 */
	public static List<Concept> parseConcepts(String csv) {
		List<String> identifiers = parseCsv(csv);
		List<Concept> concepts = new ArrayList<Concept>();

		for (String identifier : identifiers) {
			if (StringUtils.isNumeric(identifier)) {
				concepts.add(Context.getConceptService().getConcept(Integer.valueOf(identifier)));
			}
			else {
				concepts.add(Dictionary.getConcept(identifier));
			}
		}
		return concepts;
	}

	/**
	 * Unlike in OpenMRS core, a user can only be one provider in KenyaEMR
	 * @param user the user
	 * @return the provider or null
	 */
	public static Provider getProvider(User user) {
		Person person = user.getPerson();
		Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(person);
		return providers.size() > 0 ? providers.iterator().next() : null;
	}

	/**
	 * Finds the last encounter during the program enrollment with the given encounter type
	 *
	 * @param type the encounter type
	 *
	 * @return the encounter
	 */
	public static Encounter lastEncounter(Patient patient, EncounterType type) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, null, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}

	public static Encounter lastEncounter(Patient patient, EncounterType type, Form form) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Collections.singleton(form), Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}

	public static Encounter lastEncounter(Patient patient, EncounterType type, List<Form> forms) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, forms, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}

	public static List<Encounter> AllEncounters(Patient patient, EncounterType type, Form form) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Collections.singleton(form), Collections.singleton(type), null, null, null, false);
		return encounters;
	}

	/**
	 * Finds the first encounter during the program enrollment with the given encounter type
	 *
	 * @param type the encounter type
	 *
	 * @return the encounter
	 */
	public static Encounter firstEncounter(Patient patient, EncounterType type) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, null, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(0) : null;
	}

	/**
	 * Finds the last encounter of a given type entered via a given form.
	 *
	 * @param encounterType the type of encounter
	 * @param form          the form through which the encounter was entered.
	 */
	public static Encounter encounterByForm(Patient patient, EncounterType encounterType, Form form) {
		List<Form> forms = null;
		if (form != null) {
			forms = new ArrayList<Form>();
			forms.add(form);
		}
		EncounterService encounterService = Context.getEncounterService();
		List<Encounter> encounters = encounterService.getEncounters
				(
						patient,
						null,
						null,
						null,
						forms,
						Collections.singleton(encounterType),
						null,
						null,
						null,
						false
				);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}

	/**
	 *
	 * @param patient
	 * @param careSetting
	 * @return
	 */
	public static List<DrugOrder> drugOrdersFromOrders(Patient patient, CareSetting careSetting) {

		OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
		List<Order> allDrugOrders = null;
		if (careSetting == null) {
			allDrugOrders = Context.getOrderService().getAllOrdersByPatient(patient);
		} else {
			allDrugOrders = Context.getOrderService().getOrders(patient, careSetting, drugOrderType, false);
		}

		List<DrugOrder> drugOrdersOnly = new ArrayList<DrugOrder>();
		for (Order o:allDrugOrders) {
			DrugOrder order = null;
			if (o.getOrderType().equals(drugOrderType)) {
				order = (DrugOrder)o;
				drugOrdersOnly.add(order);
			}

		}
		return drugOrdersOnly;
	}

	public static ObjectNode getDatasetMappingForReport(String reportName, String mappingString) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode conf = (ArrayNode) mapper.readTree(mappingString);

		for (Iterator<JsonNode> it = conf.iterator(); it.hasNext(); ) {
			ObjectNode node = (ObjectNode) it.next();
			if (node.get("reportName").asText().equals(reportName)) {
				return node;
			}
		}

		return null;
	}

	public static boolean encounterThatPassCodedAnswer(Encounter enc, Concept question, Concept answer) {
		boolean passed = false;
		for (Obs obs : enc.getAllObs()) {
			if (obs.getConcept().getConceptId().intValue() == question.getConceptId().intValue()
					&& obs.getValueCoded().getConceptId().intValue() == answer.getConceptId().intValue()) {
				passed = true;
				break;
			}
		}
		return passed;
	}

	/**
	 * a helper method that checks if an encounter has obs for a concept
	 * @param enc
	 * @param question
	 * @return
	 */
	public static boolean encounterHasObsForConcept(Encounter enc, Concept question) {
		boolean passed = false;
		for (Obs obs : enc.getAllObs()) {
			if (obs.getConcept().getConceptId().intValue() == question.getConceptId().intValue()) {
				passed = true;
				break;
			}
		}
		return passed;
	}

	public static List<String> getCountryList() {
		String countryNames = "Afghanistan, Äland Islands, Albania, Algeria, American Samoa, Andorra, Angola, Anguilla, Antarctica, Antigua and Barbuda, Argentina, Armenia, Aruba, Australia, Austria, Azerbaijan, Bahamas, Bahrain, Bangladesh, Barbados, Belarus, Belgium, Belize, Benin, Bermuda, Bhutan, Bolivia, Sint Eustatius and Saba Bonaire, Bosnia and Herzegovina, Botswana, Bouvet Island, Brazil, British Indian Ocean Territory, Brunei Darussalam, Bulgaria, Burkina Faso, Burundi, Cabo Verde, Cambodia, Cameroon, Canada, Cayman Islands, Central African Republic, Chad, Chile, China, Christmas Island, Cocos (Keeling) Islands, Colombia, Comoros, Congo, Congo, Democratic Republic of the, Cook Islands, Costa Rica, Cv¥te d'Ivoire, Croatia, Cuba, Curavßao, Cyprus, Czechia, Denmark, Djibouti, Dominica, Dominican Republic, Ecuador, Egypt, El Salvador, Equatorial Guinea, Eritrea, Estonia, Eswatini, Ethiopia, Falkland Islands (Malvinas), Faroe Islands, Fiji, Finland, France, French Guiana, French Polynesia, French Southern Territories, Gabon, Gambia, Georgia, Germany, Ghana, Gibraltar, Greece, Greenland, Grenada, Guadeloupe, Guam, Guatemala, Guernsey, Guinea, Guinea-Bissau, Guyana, Haiti, Heard Island and McDonald Islands, Holy See, Honduras, Hong Kong, Hungary, Iceland, India, Indonesia, Iran, Iraq, Ireland, Isle of Man, Israel, Italy, Jamaica, Japan, Jersey, Jordan, Kazakhstan, Kenya, Kiribati, Democratic People's Republic of Korea, Republic of Korea, Kuwait, Kyrgyzstan, Lao People's Democratic Republic, Latvia, Lebanon, Lesotho, Liberia, Libya, Liechtenstein, Lithuania, Luxembourg, Macao, Madagascar, Malawi, Malaysia, Maldives, Mali, Malta, Marshall Islands, Martinique, Mauritania, Mauritius, Mayotte, Mexico, Micronesia, Moldova, Monaco, Mongolia, Montenegro, Montserrat, Morocco, Mozambique, Myanmar, Namibia, Nauru, Nepal, Netherlands, New Caledonia, New Zealand, Nicaragua, Niger, Nigeria, Niue, Norfolk Island, North Macedonia, Northern Mariana Islands, Norway, Oman, Pakistan, Palau, Palestine, Panama, Papua New Guinea, Paraguay, Peru, Philippines, Pitcairn, Poland, Portugal, Puerto Rico, Qatar, Réunion, Romania, Russian Federation, Rwanda, Saint Barthélemy, Saint Helena, Ascension and Tristan da Cunha, Saint Kitts and Nevis, Saint Lucia, Saint Martin, Saint Pierre and Miquelon, Saint Vincent and the Grenadines, Samoa, San Marino, Sao Tome and Principe, Saudi Arabia, Senegal, Serbia, Seychelles, Sierra Leone, Singapore, Sint Maarten, Slovakia, Slovenia, Solomon Islands, Somalia, South Africa, South Georgia and the South Sandwich Islands, South Sudan, Spain, Sri Lanka, Sudan, Suriname, Svalbard and Jan Mayen, Sweden, Switzerland, Syrian Arab Republic, Taiwan, Tajikistan, Tanzania, Thailand, Timor-Leste, Togo, Tokelau, Tonga, Trinidad and Tobago, Tunisia, Turkey, Turkmenistan, Turks and Caicos Islands, Tuvalu, Uganda, Ukraine, United Arab Emirates, United Kingdom of Great Britain and Northern Ireland, United States of America, United States Minor Outlying Islands, Uruguay, Uzbekistan, Vanuatu, Venezuela, Viet Nam, Virgin Islands (British), Virgin Islands (U.S.), Wallis and Futuna, Western Sahara, Yemen, Zambia, Zimbabwe";
		return Arrays.asList(countryNames.split(","));
	}

}