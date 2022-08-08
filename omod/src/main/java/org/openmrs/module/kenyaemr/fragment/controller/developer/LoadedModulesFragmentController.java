/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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