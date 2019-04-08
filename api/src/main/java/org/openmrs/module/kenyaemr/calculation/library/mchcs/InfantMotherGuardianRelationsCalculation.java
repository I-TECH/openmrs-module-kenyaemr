/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Returns a list of patients with ids for mother/guardian relations
 */
public class InfantMotherGuardianRelationsCalculation extends AbstractPatientCalculation {

	public static String PARENT_RELATIONSHIP = "Parent";
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		PersonService service = Context.getPersonService();
		RelationshipType guardian_rel = service.getRelationshipTypeByUuid(CommonMetadata._RelationshipType.GUARDIAN_DEPENDANT);
		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			Person person = service.getPerson(ptId);
			Set<Integer> ids = new HashSet<Integer>();
			//fetch all relationships for the patient
			List<Relationship> parentRelationship = service.getRelationships(null, person, null);
			if (parentRelationship != null){
				for (Relationship r : parentRelationship) {

					if (r.getRelationshipType().getaIsToB().equals(PARENT_RELATIONSHIP)) {
						//look for mother's id
						Integer parent_id = r.getPersonA().getId();
						Person parent = service.getPerson(parent_id);

						if (parent.getGender().equals("F")) {
							ids.add(r.getPersonA().getId());

						}
					} else if (r.getRelationshipType().equals(guardian_rel)) {
						ids.add(r.getPersonA().getId());

					}
				}
		}
			resultMap.put(ptId, new SimpleResult(ids, this));

		}
		return resultMap;

	}
}
