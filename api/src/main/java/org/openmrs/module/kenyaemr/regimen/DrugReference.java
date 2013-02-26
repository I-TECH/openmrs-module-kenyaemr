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
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

/**
 * Reference to a drug. This class only stores UUIDs so that it can be kept in memory between requests
 */
public class DrugReference {

	private String conceptUuid;

	private String drugUuid;

	/**
	 * Constructs a drug reference
	 * @param conceptUuid the concept UUID
	 * @param drugUuid the drug UUID
	 */
	private DrugReference(String conceptUuid, String drugUuid) {
		this.conceptUuid = conceptUuid;
		this.drugUuid = drugUuid;
	}

	/**
	 * Creates a drug reference from a concept UUID
	 * @param conceptUuid the concept UUID
	 * @return the drug reference
	 */
	public static DrugReference fromConceptUuid(String conceptUuid) {
		return new DrugReference(conceptUuid, null);
	}

	/**
	 * Creates a drug reference from a drug UUID
	 * @param drugUuid the drug UUID
	 * @return the drug reference
	 */
	public static DrugReference fromDrugUuid(String drugUuid) {
		Drug drug = Context.getConceptService().getDrugByUuid(drugUuid);
		return new DrugReference(drug.getConcept().getUuid(), drug.getUuid());
	}

	/**
	 * Creates a drug reference from a drug order
	 * @param drugOrder the drug order
	 * @return the drug reference
	 */
	public static DrugReference fromDrugOrder(DrugOrder drugOrder) {
		return new DrugReference(drugOrder.getConcept().getUuid(), drugOrder.getDrug() != null ? drugOrder.getDrug().getUuid() : null);
	}

	/**
	 * Gets whether this references a concept only
	 * @return true if reference is to a concept only
	 */
	public boolean isConceptOnly() {
		return drugUuid == null;
	}

	/**
	 * Gets the referenced concept
	 * @return the concept
	 */
	public Concept getConcept() {
		return Context.getConceptService().getConceptByUuid(conceptUuid);
	}

	/**
	 * Gets the referenced drug
	 * @return the drug
	 */
	public Drug getDrug() {
		return Context.getConceptService().getDrugByUuid(drugUuid);
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DrugReference)) {
			return false;
		}

		DrugReference drugRef = (DrugReference)o;

		if (!this.conceptUuid.equals(drugRef.conceptUuid)) {
			return false;
		}

		return OpenmrsUtil.nullSafeEquals(this.drugUuid, drugRef.drugUuid);
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return conceptUuid.hashCode() + (drugUuid != null ? drugUuid.hashCode() : 0);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return isConceptOnly() ? ("C$" + conceptUuid) : ("D$" + drugUuid);
	}
}