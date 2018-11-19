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

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.globalProperty;

/**
 * Metadata constants
 */
@Component
public class RDQAMetadata extends AbstractMetadataBundle {

	public static final String MODULE_ID = "kenyaemr";
	public static final String RDQA_DEFAULT_SAMPLE_CONFIGURATION = MODULE_ID + ".sampleSizeConfiguration";
    public static final String RDQA_DEFAULT_NO_OF_MONTHS = MODULE_ID + ".defaultNoOfMonths";
	private String defaultConfig = "20,21-30:24,31-40:30,41-50:35,51-60:39,61-70:43,71-80:46,81-90:49,91-100:52,101-119:57,120-139:61,140-159:64,160-179:67,180-199:70,200-249:75,250-299:79,300-349:82,350-399:85,400-449:87,450-499:88,500-749:94,750-999:97,1000-4999:105,5000:107";


	@Override
	public void install() throws Exception {
		install(globalProperty(RDQA_DEFAULT_SAMPLE_CONFIGURATION, "RDQA Sample size calculation configuration", defaultConfig));
        install(globalProperty(RDQA_DEFAULT_NO_OF_MONTHS, "RDQA No of previous months to consider in the report", "3"));

	}
}
