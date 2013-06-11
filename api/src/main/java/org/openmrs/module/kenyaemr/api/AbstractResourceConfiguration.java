package org.openmrs.module.kenyaemr.api;

import org.openmrs.module.ModuleFactory;

/**
 * Abstract base class for configuration components based on a single external resource
 */
public abstract class AbstractResourceConfiguration {

	private String moduleId;

	private String path;

	/**
	 * Gets the class loader to use with this configuration
	 * @return the class loader
	 */
	public ClassLoader getClassLoader() {
		return (moduleId != null) ? ModuleFactory.getModuleClassLoader(moduleId) : getClass().getClassLoader();
	}

	/**
	 * Gets the module id of the owner module
	 * @return the module id
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * Sets the module id of the owner module
	 * @param moduleId the module id
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * Gets the path to the resource
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path to the resource
	 * @param path the path
	 */
	public void setPath(String path) {
		this.path = path;
	}
}