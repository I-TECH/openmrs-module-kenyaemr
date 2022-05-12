/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.upi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.ui.framework.SimpleObject;

import java.io.IOException;
import java.util.List;


public class UpiUtilsDataExchange {

	private Log log = LogFactory.getLog(UpiUtilsDataExchange.class);

	private List<PatientIdentifierType> allPatientIdentifierTypes;

/**
	 * Processes CR response for updating UPI number fetched from CR server
	  */
	public static SimpleObject processUpiResponse(String stringResponse) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = null;
		String message = "";
		String clientNumber = "";
		SimpleObject responseObj = new SimpleObject();

		try {
			jsonNode = mapper.readTree(stringResponse);
			if (jsonNode != null) {
				clientNumber = jsonNode.get("clientNumber").textValue();
				responseObj.put("clientNumber", clientNumber);
				System.out.println("Client Number==>"+clientNumber);
			}
		}
		catch (Exception e) {
				e.printStackTrace();
			}
     return responseObj;
	}

}
