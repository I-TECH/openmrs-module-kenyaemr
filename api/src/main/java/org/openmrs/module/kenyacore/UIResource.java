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

package org.openmrs.module.kenyacore;

/**
 * Wrapper for UI Framework managed resource locations
 */
public class UIResource {

	private String provider;

	private String path;

	/**
	 * Constructs a UIResource from a string in the format <provider>:<path>
	 * @param providerAndPath the string value
	 */
	public UIResource(String providerAndPath) {
		String[] components = providerAndPath.split(":");

		if (components.length != 2) {
			throw new IllegalArgumentException("UI resource " + providerAndPath + " is not formatted as <provider>:<path>");
		}

		this.provider = components[0];
		this.path = components[1];
	}

	/**
	 * Constructs a UIResource from the given provider and path values
	 * @param provider the provider
	 * @param path the path
	 */
	public UIResource(String provider, String path) {
		this.provider = provider;
		this.path = path;
	}

	/**
	 * Gets the resource provider
	 * @return the provider name
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * Gets the resource path
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return provider + ":" + path;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UIResource that = (UIResource) o;

		return path.equals(that.path) && provider.equals(that.provider);
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return path.hashCode() + 31 * provider.hashCode();
	}
}