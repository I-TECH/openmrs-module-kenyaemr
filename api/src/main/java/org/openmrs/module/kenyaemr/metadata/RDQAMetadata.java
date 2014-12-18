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

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.globalProperty;

/**
 * Metadata constants
 */
@Component
public class RDQAMetadata extends AbstractMetadataBundle {

	public static final String MODULE_ID = "kenyaemr";
	public static final String RDQA_DEFAULT_SAMPLE_CONFIGURATION = MODULE_ID + ".sampleSizeConfiguration";
	private String defaultConfig = "20,21-30:24,31-40:30,41-50:35,51-60:39,61-70:43,71-80:46,81-90:49,91-100:52,101-119:57,120-139:61,140-159:64,160-179:67,180-199:70,200-249:75,250-299:79,300-349:82,350-399:85,400-449:87,450-499:88,500-749:94,750-999:97,1000-4999:105,5000:107";


	@Override
	public void install() throws Exception {
		install(globalProperty(RDQA_DEFAULT_SAMPLE_CONFIGURATION, "RDQA Sample size calculation configuration", defaultConfig));

	}
}
