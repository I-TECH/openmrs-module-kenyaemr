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

package org.openmrs.module.kenyaemr.test;

import org.junit.Ignore;
import org.mockito.Mockito;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.kenyaui.KenyaUiConstants;
import org.openmrs.ui.framework.BasicUiUtils;
import org.openmrs.ui.framework.FormatterImpl;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.springframework.stereotype.Component;

import static org.mockito.Mockito.mock;

/**
 * Basic UiUtils implementation for unit tests
 */
@Ignore
@Component
public class TestUiUtils extends BasicUiUtils {

	/**
	 * Default constructor
	 */
	public TestUiUtils() {
		// Use a mock administration service which uses standard formatting from Kenya UI
		AdministrationService administrationService = mock(AdministrationService.class);
		Mockito.when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT)).thenReturn(KenyaUiConstants.DATETIME_FORMAT);
		Mockito.when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT)).thenReturn(KenyaUiConstants.DATE_FORMAT);

		this.formatter = new FormatterImpl(null, administrationService);
	}
}