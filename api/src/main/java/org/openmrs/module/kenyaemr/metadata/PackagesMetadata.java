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

package org.openmrs.module.kenyaemr.metadata;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * Packages metadata bundle. Kept separate to keep out of unit tests
 */
@Component
public class PackagesMetadata extends AbstractMetadataBundle {

	public static final class _Package {
		public static final String DRUGS = "550a5db0-13be-486d-aec7-de05adac71e7";
		public static final String MISC = "29177ba6-a634-42d5-9314-e12689856ff1";
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(packageFile("metadata/KenyaEMR_Drugs-2.zip", null, _Package.DRUGS));
		install(packageFile("metadata/KenyaEMR_Misc-41.zip", null, _Package.MISC));
	}
}