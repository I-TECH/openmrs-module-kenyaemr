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
package org.openmrs.module.kenyaemr.api.impl;

import org.openmrs.api.APIException;


/**
 * Indicates that configuration is required before the application can be used.
 * 
 * TODO: consider moving this to the App Framework module
 */
public class ConfigurationRequiredException extends APIException {

    private static final long serialVersionUID = 1L;
    
    
    /**
     * Default constructor
     */
    public ConfigurationRequiredException() {
    }

	/**
     * @param string
     */
    public ConfigurationRequiredException(String message) {
	    super(message);
    }

}
