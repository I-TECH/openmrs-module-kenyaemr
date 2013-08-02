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

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.calculation.BaseFlagCalculation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying all loaded modules
 */
public class LoadedModulesFragmentController {

	public void controller(FragmentModel model) {
		List<SimpleObject> modules = new ArrayList<SimpleObject>();
		for (Module mod : ModuleFactory.getLoadedModules()) {
			modules.add(SimpleObject.create("name", mod.getName(), "version", mod.getVersion(), "started", mod.isStarted()));
		}

		model.addAttribute("modules", modules);
	}
}