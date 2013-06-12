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

package org.openmrs.module.kenyaemr.identifier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openmrs.util.OpenmrsUtil;

/**
 * Describes how an identifier type can be used in the EMR
 */
public class IdentifierDescriptor implements Comparable<IdentifierDescriptor> {

	private String identifierTypeUuid;

	private Integer order;

	/**
	 * Gets the identifier type uuid
	 * @return the identifier type uuid
	 */
	public String getIdentifierTypeUuid() {
		return identifierTypeUuid;
	}

	/**
	 * Sets the identifier type uuid
	 * @param identifierTypeUuid identifier type uuid
	 */
	public void setIdentifierTypeUuid(String identifierTypeUuid) {
		this.identifierTypeUuid = identifierTypeUuid;
	}

	/**
	 * Gets the order
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * Sets the order
	 * @param order the order
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(IdentifierDescriptor identifierDescriptor) {
		return OpenmrsUtil.compareWithNullAsGreatest(order, identifierDescriptor.order);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("identifierTypeUuid", identifierTypeUuid).toString();
	}
}