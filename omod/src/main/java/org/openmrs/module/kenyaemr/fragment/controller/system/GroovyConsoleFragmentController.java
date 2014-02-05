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

package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.openmrs.module.groovy.GroovyUtil;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Groovy console fragment
 */
public class GroovyConsoleFragmentController {

	public void controller() {
	}

	/**
	 * Executes a groovy script
	 * @param script the script
	 * @return the result as a simple object {result, output, stracktrace}
	 */
	@AppAction(EmrConstants.APP_DEVELOPER)
	public SimpleObject execute(@RequestParam("script") String script) {
		String[] result = GroovyUtil.getService().evaluate(script);
		return SimpleObject.create("result", result[0], "output", result[1], "stacktrace", result[2]);
	}
}