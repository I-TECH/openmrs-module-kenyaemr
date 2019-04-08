/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.regimen;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.util.OpenmrsUtil;

/**
 * Reference to a drug. This class only stores UUIDs so that it can be kept in memory between requests
 */
public class DrugReference {

	private String conceptIdentifier;

	private String drugIdentifier;

	/**
	 * Constructs a drug reference
	 * @param conceptIdentifier the concept identifier
	 * @param drugIdentifier the drug UUID
	 */
	private DrugReference(String conceptIdentifier, String drugIdentifier) {
		this.conceptIdentifier = conceptIdentifier;
		this.drugIdentifier = drugIdentifier;
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
		if (drug != null) {
			return new DrugReference(drug.getConcept().getUuid(), drug.getUuid());
		}
		return null;
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
		return drugIdentifier == null;
	}

	/**
	 * Gets the referenced concept
	 * @return the concept
	 */
	public Concept getConcept() {
		return MetadataUtils.existing(Concept.class, conceptIdentifier);
	}

	/**
	 * Gets the referenced drug
	 * @return the drug
	 */
	public Drug getDrug() {
		return Context.getConceptService().getDrugByUuid(drugIdentifier);
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

		if (!this.conceptIdentifier.equals(drugRef.conceptIdentifier)) {
			return false;
		}

		return OpenmrsUtil.nullSafeEquals(this.drugIdentifier, drugRef.drugIdentifier);
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return conceptIdentifier.hashCode() + (drugIdentifier != null ? drugIdentifier.hashCode() : 0);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return isConceptOnly() ? ("C$" + conceptIdentifier) : ("D$" + drugIdentifier);
	}
}