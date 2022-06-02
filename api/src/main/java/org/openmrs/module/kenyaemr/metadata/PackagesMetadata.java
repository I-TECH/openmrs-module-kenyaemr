/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.metadata;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.packageFile;

/**
 * Packages metadata bundle. Kept separate to keep out of unit tests
 */
@Component
public class PackagesMetadata extends AbstractMetadataBundle {

	public static final class _Package {
		public static final String DRUGS = "550a5db0-13be-486d-aec7-de05adac71e7";
		public static final String MISC = "29177ba6-a634-42d5-9314-e12689856ff1";
		public static final String ORDER_SET = "e7d8aef4-e977-11e8-9f32-f2801f1b9fd1";
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		// install(packageFile("metadata/KenyaEMR_Drugs-3.zip", null, _Package.DRUGS));
		// install(packageFile("metadata/KenyaEMR_Misc-41.zip", null, _Package.MISC));
		// install(packageFile("metadata/Order_set_metadata-1.zip", null, _Package.ORDER_SET));
	}
}