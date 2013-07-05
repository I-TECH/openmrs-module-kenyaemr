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

package org.openmrs.module.kenyaemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.calculation.CalculationManager;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.kenyaemr.identifier.IdentifierManager;
import org.openmrs.module.kenyaemr.lab.LabManager;
import org.openmrs.module.kenyaemr.metadata.MetadataManager;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.reporting.ReportManager;
import org.openmrs.module.kenyaemr.util.BuildProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * KenyaEMR master content manager, used as a registered singleton
 */
@Component
public class KenyaEmr {

	protected static final Log log = LogFactory.getLog(KenyaEmrActivator.class);

	@Autowired
	private MetadataManager metadataManager;

	@Autowired
	private IdentifierManager identifierManager;

	@Autowired
	private RegimenManager regimenManager;

	@Autowired
	private FormManager formManager;

	@Autowired
	private CalculationManager calculationManager;

	@Autowired
	private ReportManager reportManager;

	@Autowired
	private LabManager labManager;

	boolean refreshed = false;

	/**
	 * Gets the module version
	 * @return the version
	 */
	public String getModuleVersion() {
		return ModuleFactory.getModuleById(KenyaEmrConstants.MODULE_ID).getVersion();
	}

	/**
	 * Gets the module build properties
	 * @return the build properties map or null if not available
	 */
	public BuildProperties getModuleBuildProperties() {
		return getSingletonComponent(BuildProperties.class);
	}

	/**
	 * Gets the metadata manager
	 * @return the metadata manager
	 */
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	/**
	 * Gets the identifier manager
	 * @return the identifier manager
	 */
	public IdentifierManager getIdentifierManager() {
		return identifierManager;
	}

	/**
	 * Gets the regimen manager
	 * @return the regimen manager
	 */
	public RegimenManager getRegimenManager() {
		return regimenManager;
	}

	/**
	 * Gets the form manager
	 * @return the form manager
	 */
	public FormManager getFormManager() {
		return formManager;
	}

	/**
	 * Gets the calculation manager
	 * @return the calculation manager
	 */
	public CalculationManager getCalculationManager() {
		return calculationManager;
	}

	/**
	 * Gets the report manager
	 * @return the report manager
	 */
	public ReportManager getReportManager() {
		return reportManager;
	}

	/**
	 * Gets the lab manager
	 * @return the lab manager
	 */
	public LabManager getLabManager() {
		return labManager;
	}

	/**
	 * Utility method to get the singleton instance from the application context in situations where you
	 * can't use @Autowired or @SpringBean. Use as a last resort.
	 * @return the singleton instance
	 */
	public static KenyaEmr getInstance() {
		return getSingletonComponent(KenyaEmr.class);
	}

	/**
	 * Refresh all content
	 */
	public synchronized void refresh() {
		refreshed = false;

		metadataManager.refresh(); // First because others will use metadata loaded by it
		labManager.refresh();
		regimenManager.refresh();
		identifierManager.refresh();
		calculationManager.refresh();
		formManager.refresh();
		reportManager.refresh();

		refreshed = true;
	}

	/**
	 * Returns whether all content was successfully refreshed
	 * @return
	 */
	public boolean isRefreshed() {
		return refreshed;
	}

	/**
	 * Fetches a singleton component from the application context
	 * @param clazz the class of the component
	 * @param <T> the class of the component
	 * @return the singleton instance
	 * @throws RuntimeException if no such instance exists or more than one instance exists
	 */
	protected static <T> T getSingletonComponent(Class<T> clazz) {
		List<T> all = Context.getRegisteredComponents(clazz);
		if (all.size() == 0) {
			throw new RuntimeException("No such object in the application context");
		}
		// Because of TRUNK-3889, singleton beans get instantiated twice
		//else if (all.size() > 1) {
		//	throw new RuntimeException("Object is not a singleton in the application context");
		//}
		else {
			return all.get(0);
		}
	}
}