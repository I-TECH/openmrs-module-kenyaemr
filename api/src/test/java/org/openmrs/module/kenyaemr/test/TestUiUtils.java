/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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