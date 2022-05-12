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

import org.openmrs.Patient;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

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

public class UpiDataExchangeFragmentController {

	private static final String POST_URL3 = "https://dhpstagingapi.health.go.ke/visit/registry";


	private static final String POST_PARAMS = "userName=Pankaj";


	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {

	}

	public static SimpleObject postUpiClientRegistrationInfoToCR(@RequestParam("postParams") String params ) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		return sendPOST(params);
	}

	private static SimpleObject sendPOST(String params) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		String stringResponse= "";

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
		System.out.println("Params for request ==>"+params);
		con.setRequestMethod("POST");

		String authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0MUU1QUM5RUIxNTlBMjc1NTY4NjM0MzIxMUJDQzAzMDMyMEUzMTZSUzI1NiIsIng1dCI6IjVCNWF5ZXNWbWlkVmFHTkRJUnZNQXdNZzR4WSIsInR5cCI6ImF0K2p3dCJ9.eyJpc3MiOiJodHRwczovL2RocGlkZW50aXR5c3RhZ2luZ2FwaS5oZWFsdGguZ28ua2UiLCJuYmYiOjE2NTIzNDAxMzUsImlhdCI6MTY1MjM0MDEzNSwiZXhwIjoxNjUyNDI2NTM1LCJhdWQiOlsiREhQLkdhdGV3YXkiLCJESFAuVmlzaXRhdGlvbiJdLCJzY29wZSI6WyJESFAuR2F0ZXdheSIsIkRIUC5WaXNpdGF0aW9uIl0sImNsaWVudF9pZCI6InBhcnRuZXIudGVzdC5jbGllbnQiLCJqdGkiOiI3MjJFNTEyN0I3NDg3RTYyNEM1REQzODk5MjkzMkE2RSJ9.BhTdmiAC8HLW1Uh4ixacdbaBiSzvTlYkcOVL9Wfi67UHwp9SzV7104Y1VUDXT1P_wcGOJbXvuysJyZYlJgc2a5-kglQ29r_UEP9rkI8FdHaYwky76fqmL71hbrW5kdmYwlWoR-RK4CFXm10jaoXmBoTPMShP-l8r-t9kcgrQ9i80HVhbcRNTXk1v5XhiqmykIBstwXVFQBbdhFZ_7Z8ZUiJnc-Oesx1rg8Xf0q1kElxPHwg7jLPvrebmMD8h25PI_dN3xkylBZWl__ZSmU7bwQAp7RAJzGuSQG5FB2MNLyYsEGryal29-cR7sR-Yx48iazlAott5srCXnzj5dcdpRg";
		con.setRequestProperty("Authorization", "Bearer " + authToken);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");

		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(params.getBytes());
		os.flush();
		os.close();
		// For POST only - END

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode + con.getResponseMessage());

		SimpleObject responseObj = null;
		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println("POST request successful");
			stringResponse = response.toString();
			System.out.println(stringResponse);
            UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
            upiUtils.processUpiResponse(stringResponse);

            responseObj = upiUtils.processUpiResponse(stringResponse);
            responseObj.put("status", "success");
            //responseObj.put("status", "success");
            return upiUtils.processUpiResponse(stringResponse);

		} else {

			responseObj = new SimpleObject();
			responseObj.put("status", "fail");
			System.out.println("POST request not worked");
			System.out.println("Using dummy response instead");

			// Dummy response
			stringResponse ="{\"clientNumber\":\"MOH202205052\",\"firstName\":\"TEST\",\"middleName\":\"BUSH\",\"lastName\":\"BUSH\",\"dateOfBirth\":\"08/14/2000 21:00:00\",\"maritalStatus\":null,\"gender\":\"male\",\"occupation\":\"\",\"religion\":\"\",\"educationLevel\":\"\",\"country\":\"Kenya\",\"countyOfBirth\":null,\"isAlive\":true,\"originFacilityKmflCode\":\"None\",\"residence\":{\"county\":\"Nairobi\",\"subCounty\":\"Dagorreti\",\"ward\":\"\",\"village\":\"Ngeria\",\"landMark\":\"\",\"address\":\"\"},\"identifications\":[{\"identificationType\":\"identification-number\",\"identificationNumber\":\"56445570\"}],\"contact\":{\"primaryPhone\":\"0722404040\",\"secondaryPhone\":\"07025464646\",\"emailAddress\":\"\"},\"nextOfKins\":[]}";
			System.out.println(stringResponse);
			UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
			responseObj = upiUtils.processUpiResponse(stringResponse);


		}
		return responseObj;
	}

}