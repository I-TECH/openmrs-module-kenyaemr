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

import java.io.IOException;

public class UpiDataExchangeFragmentController {

	private Log log = LogFactory.getLog(UpiDataExchangeFragmentController.class);
	private String url = "http://www.google.com:80/index.html";

	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {

	}
	public void postUpiClientRegistrationInfoToCR(@RequestParam("postParams") String params ) throws IOException {
		generateAccessToken("https://dhpidentitystagingapi.health.go.ke/connect/token");
		System.out.println("Params"+params);
		//Prepare the post request
		String serverUrl ="https://dhpstaging.health.go.ke/visit/registry";
		String API_KEY = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0MUU1QUM5RUIxNTlBMjc1NTY4NjM0MzIxMUJDQzAzMDMyMEUzMTZSUzI1NiIsIng1dCI6IjVCNWF5ZXNWbWlkVmFHTkRJUnZNQXdNZzR4WSIsInR5cCI6ImF0K2p3dCJ9.eyJpc3MiOiJodHRwczovL2RocGlkZW50aXR5c3RhZ2luZ2FwaS5oZWFsdGguZ28ua2UiLCJuYmYiOjE2NTIyNzMzNzksImlhdCI6MTY1MjI3MzM3OSwiZXhwIjoxNjUyMzU5Nzc5LCJhdWQiOlsiREhQLkdhdGV3YXkiLCJESFAuVmlzaXRhdGlvbiJdLCJzY29wZSI6WyJESFAuR2F0ZXdheSIsIkRIUC5WaXNpdGF0aW9uIl0sImNsaWVudF9pZCI6InBhcnRuZXIudGVzdC5jbGllbnQiLCJqdGkiOiJDMDE2RUYyQjhFMkUzQUI5NzIyNDY4Q0EwOUYxOTVGRiJ9.FD_VpzTqoUj3JnWSEZXFuvPyOs-rkDlxOLE_QnVWYY5w-scn1RtkaLuTwLU274t_TWAX3r2NYvft5l-bmEWRkrjevQ87FN9vplbpLx1kMAqgrdNysiLW4PfPFYZ_DjmoeAeSAVLEkQhs86ZjhapffI03QRmT8dFUB_Ta_Aq2l25Sru8Tb7kIE9J186e7RVIK3DgCSCS3srm66Se5HY7w56UEaYNAhjKncn_GaCpUsF6TXEvr-dOE4PdJQrjZopdLfs9DQKk8WKrlbOAZW_bznIEMhOxyTQ4hK3IvR6lcyf-S_9KZgwZF9KXK0IYNPCgzynLl_TYQmQCEPRTTlhPkHw";
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				SSLContexts.createDefault(),
				new String[] { "TLSv1.2"},
				null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());

		CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		try {

				//Define a postRequest request
				HttpPost postRequest = new HttpPost(serverUrl);

				//Set the API media type in http content-type header
				postRequest.addHeader("content-type", "application/json");
				postRequest.addHeader("Authorization", "Bearer"+ API_KEY);
				//Set the request post body
				String payload = params;
				StringEntity userEntity = new StringEntity(payload);
				postRequest.setEntity(userEntity);
			//System.out.println("Post request"+params);

				//Send the request; It will immediately return the response in HttpResponse object if any
				HttpResponse response = httpClient.execute(postRequest);
				//verify the valid error code first
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 429) { // too many requests. just terminate
					System.out.println("Many requests please terminate");
					log.warn("Many requests please terminate");
					return;
				}

				if (statusCode == 200) {
					log.info("Successfully pushed enrollment info with id ");
				} else if (statusCode == 412) {

					JSONParser parser = new JSONParser();
					JSONObject responseObj = (JSONObject) parser.parse(EntityUtils.toString(response
							.getEntity()));
					//	JSONObject errorObj = (JSONObject) responseObj.get("error");
					System.out.println("Error while submitting enrollment sample. " + "Error - "
							+ statusCode + ". Msg" + responseObj.get("message"));
					log.error("Error while submitting enrollment. " + "Error - " + statusCode + ". Msg"
							+ responseObj.get("message"));
				} else {

					JSONParser parser = new JSONParser();
					JSONObject responseObj = (JSONObject) parser.parse(EntityUtils.toString(response
							.getEntity()));
					//JSONObject errorObj = (JSONObject) responseObj.get("error");
					System.out.println("Error while submitting enrollment sample.  " + "Error - "
							+ statusCode + ". Msg" + responseObj.get("message"));
					log.error("Error while submitting enrollment. " + "Error - " + statusCode + ". Msg"
							+ responseObj.get("message"));

				}
				Context.flushSession();
			}
			catch (Exception e) {
				System.out.println("Could not push client information to CR! " + e.getCause());
				log.error("Could not push client information to CR!! " + e.getCause());
				e.printStackTrace();
			}
//			finally {
//				httpClient.close();
//			}

	}

	private String generateAccessToken(String serverUrl) throws IOException {

		CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		String token ="";
		String clientId = "partner.test.client";
		String secretId = "partnerTestPwd";
		String grantType = "client_credentials";
		String scope = "DHP.Gateway DHP.Visitation";
//		client_id:partner.test.client
//		client_secret:partnerTestPwd
//		grant_type:client_credentials
//		scope:DHP.Gateway DHP.Visitation
		HttpPost postRequest = new HttpPost(serverUrl);
		postRequest.addHeader("client_id", clientId);
		postRequest.addHeader("client_secret", secretId);
		postRequest.addHeader("grant_type", grantType);
		postRequest.addHeader("scope", scope);

		HttpResponse response = httpClient.execute(postRequest);
		System.out.println("TOKEN----"+response);


		return token;
	}

}
