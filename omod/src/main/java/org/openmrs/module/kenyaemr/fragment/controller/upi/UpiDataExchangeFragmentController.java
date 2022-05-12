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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

public class UpiDataExchangeFragmentController {

	private static final String POST_URL3 = "https://dhpstagingapi.health.go.ke/visit/registry";


	private static final String POST_PARAMS = "userName=Pankaj";


	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {

	}

	public static void postUpiClientRegistrationInfoToCR(@RequestParam("postParams") String params ) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		sendPOST(params);
	}

	private static void sendPOST(String params) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };


		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		URL url = new URL(POST_URL3);

		HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");

		String authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0MUU1QUM5RUIxNTlBMjc1NTY4NjM0MzIxMUJDQzAzMDMyMEUzMTZSUzI1NiIsIng1dCI6IjVCNWF5ZXNWbWlkVmFHTkRJUnZNQXdNZzR4WSIsInR5cCI6ImF0K2p3dCJ9.eyJpc3MiOiJodHRwczovL2RocGlkZW50aXR5c3RhZ2luZ2FwaS5oZWFsdGguZ28ua2UiLCJuYmYiOjE2NTIzNDAxMzUsImlhdCI6MTY1MjM0MDEzNSwiZXhwIjoxNjUyNDI2NTM1LCJhdWQiOlsiREhQLkdhdGV3YXkiLCJESFAuVmlzaXRhdGlvbiJdLCJzY29wZSI6WyJESFAuR2F0ZXdheSIsIkRIUC5WaXNpdGF0aW9uIl0sImNsaWVudF9pZCI6InBhcnRuZXIudGVzdC5jbGllbnQiLCJqdGkiOiI3MjJFNTEyN0I3NDg3RTYyNEM1REQzODk5MjkzMkE2RSJ9.BhTdmiAC8HLW1Uh4ixacdbaBiSzvTlYkcOVL9Wfi67UHwp9SzV7104Y1VUDXT1P_wcGOJbXvuysJyZYlJgc2a5-kglQ29r_UEP9rkI8FdHaYwky76fqmL71hbrW5kdmYwlWoR-RK4CFXm10jaoXmBoTPMShP-l8r-t9kcgrQ9i80HVhbcRNTXk1v5XhiqmykIBstwXVFQBbdhFZ_7Z8ZUiJnc-Oesx1rg8Xf0q1kElxPHwg7jLPvrebmMD8h25PI_dN3xkylBZWl__ZSmU7bwQAp7RAJzGuSQG5FB2MNLyYsEGryal29-cR7sR-Yx48iazlAott5srCXnzj5dcdpRg";
		con.setRequestProperty("Authorization", "Bearer " + authToken);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");

		// For POST only - START

		String input = "{\n" +
				"    \"firstName\": \"TEST\",\n" +
				"    \"middleName\": \"BUSH\",\n" +
				"    \"lastName\": \"BUSH\",\n" +
				"    \"dateOfBirth\": \"2000-08-14T21:00:00.000Z\",\n" +
				"    \"maritalStatus\": null,\n" +
				"    \"gender\": \"male\",\n" +
				"    \"occupation\": \"\",\n" +
				"    \"religion\": \"\",\n" +
				"    \"educationLevel\": \"\",\n" +
				"    \"country\": \"Kenya\",\n" +
				"    \"countryOfBirth\": \"\",\n" +
				"    \"residence\": {\n" +
				"        \"county\": \"Nairobi\",\n" +
				"        \"subCounty\": \"Dagorreti\",\n" +
				"        \"ward\": \"\",\n" +
				"        \"village\": \"Ngeria\",\n" +
				"        \"landmark\": \"\",\n" +
				"        \"address\": \"\"\n" +
				"    },\n" +
				"    \"identifications\": [\n" +
				"        {\n" +
				"            \"IdentificationType\": \"identification-number\",\n" +
				"            \"IdentificationNumber\": "+ params +"\"\n" +
				"        }\n" +
				"    ],\n" +
				"    \"contact\": {\n" +
				"        \"primaryPhone\": \"0722404040\",\n" +
				"        \"secondaryPhone\": \"07025464646\",\n" +
				"        \"emailAddress\": \"\"\n" +
				"    },\n" +
				"    \"nextOfKins\": []\n" +
				"}";
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(input.getBytes());
		os.flush();
		os.close();
		// For POST only - END

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode + con.getResponseMessage());

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String stringResponse = response.toString();
			System.out.println(stringResponse);
            UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
            upiUtils.processUpiResponse(stringResponse);

		} else {
			System.out.println("POST request not worked");
		}
	}

}