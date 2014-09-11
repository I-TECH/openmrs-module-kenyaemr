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

package org.openmrs.module.kenyaemr.fragment.controller.developer;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.StandardModuleUiConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment for displaying all loaded modules
 */
public class LoadedModulesFragmentController {

	public void controller(FragmentModel model) {
		List<Module> sortedModules = new ArrayList(ModuleFactory.getLoadedModules());
		Collections.sort(sortedModules, new Comparator<Module>() {
			@Override
			public int compare(Module module, Module module2) {
				return module.getName().compareTo(module2.getName());
			}
		});

		Set<String> uiFrameworkModules = getUiConfiguredModuleIds();

		List<SimpleObject> modules = new ArrayList<SimpleObject>();
		for (Module mod : sortedModules) {
			modules.add(SimpleObject.create(
					"id", mod.getModuleId(),
					"name", mod.getName(),
					"version", mod.getVersion(),
					"started", mod.isStarted(),
					"uiFrConfigured", uiFrameworkModules.contains(mod.getModuleId()),
					"uiFrDevEnabled", isUiDevModeEnabled(mod.getModuleId())
			));
		}

		model.addAttribute("modules", modules);
	}

	/**
	 * Gets ids of all modules with a UI framework configuration
	 * @return the module ids
	 */
	protected Set<String> getUiConfiguredModuleIds() {
		Set<String> moduleIds = new HashSet<String>();

		for (StandardModuleUiConfiguration uiConfig : Context.getRegisteredComponents(StandardModuleUiConfiguration.class)) {
			moduleIds.add(uiConfig.getModuleId());
		}
		return moduleIds;
	}

	/**
	 * Gets whether module with given id is UI framework dev mode enabled
	 * @return true if dev mode is enabled
	 */
	protected boolean isUiDevModeEnabled(String moduleId) {
		return StringUtils.isNotEmpty(System.getProperty("uiFramework.development." + moduleId));
	}
}