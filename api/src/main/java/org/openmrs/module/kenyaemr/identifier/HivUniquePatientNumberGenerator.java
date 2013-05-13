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

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdgenUtil;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;

/**
 * Identifier generator for HIV Unique patient numbers
 */
public class HivUniquePatientNumberGenerator extends SequentialIdentifierGenerator {

	/**
	 * @see SequentialIdentifierGenerator#getIdentifierForSeed(long)
	 */
	public String getIdentifierForSeed(long seed) {

		// Convert the next sequence integer into a String with the appropriate Base characters
		int minLength = getFirstIdentifierBase() == null ? 1 : getFirstIdentifierBase().length();
		String identifier = IdgenUtil.convertToBase(seed, getBaseCharacterSet().toCharArray(), minLength);

		String facilityPrefix = Context.getService(KenyaEmrService.class).getDefaultLocationMflCode();

		return facilityPrefix + identifier;
	}
}