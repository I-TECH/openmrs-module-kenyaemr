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
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
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
