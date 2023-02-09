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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import com.fasterxml.jackson.databind.node.ObjectNode;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;
import org.openmrs.module.kenyaemr.nupi.UpiUtilsDataExchange;

import static org.openmrs.module.kenyaemrorderentry.util.Utils.getDefaultLocationMflCode;
import static org.openmrs.util.LocationUtility.getDefaultLocation;

public class UpiDataExchangeFragmentController  {

	// Trust all certs
	static {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
 
                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                    }
 
                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                    }
                }
        };
 
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            System.out.println(e.getMessage());
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 
        // Optional 
        // Create all-trusting host name verifier
        HostnameVerifier validHosts = new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        // All hosts will be valid
        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
 
    }

	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {

	}

	public SimpleObject postUpiClientRegistrationInfoToCR(@RequestParam("postParams") String params) throws IOException, NoSuchAlgorithmException, KeyManagementException {
	    //Check whether this client already has NUPI number hence this is an error verification
			  String clientNupi = nationalUniquePatientNumberForClient(params);
			if (!clientNupi.trim().equalsIgnoreCase("")) {
			     return sendPUT(params,clientNupi);
		     } else {
			     return sendPOST(params);
	     	}
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

		GlobalProperty globalPostUrl = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_POST_END_POINT);
		String strPostUrl = globalPostUrl.getPropertyValue();

		URL url = new URL(strPostUrl);

		HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
		String authToken = upiUtils.getToken();

		con.setRequestProperty("Authorization", "Bearer " + authToken);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");
		con.setConnectTimeout(10000); // set timeout to 10 seconds

		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(params.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		SimpleObject responseObj = null;

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			stringResponse = response.toString();

            responseObj = upiUtils.processUpiResponse(stringResponse);
            responseObj.put("status", responseCode);

			return(responseObj);

		} else {
			if (con != null && con.getErrorStream() != null) {
				BufferedReader in = null;
				// BufferedReader in = new BufferedReader(new InputStreamReader(
				// 		con.getErrorStream()));
				in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				stringResponse = response.toString();
			}

			responseObj = new SimpleObject();
			responseObj.put("status", responseCode);
			responseObj.put("message", stringResponse);
			return(responseObj);
		}
		//return responseObj;
	}

	private static SimpleObject sendPUT(String params, String clientNupi) throws IOException, NoSuchAlgorithmException, KeyManagementException {
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

		GlobalProperty globalPostUrl = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_UPDATE_END_POINT);
		String strPostUrl = globalPostUrl.getPropertyValue();
	     //https://afyakenyaapi.health.go.ke/partners/registry/{clientNumber}/update
		String putUrl = strPostUrl + "/" + clientNupi + "/update";
		System.out.println("Using NUPI UPDATE URL: " + putUrl);

		URL url = new URL(putUrl);


		HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
		con.setRequestMethod("PUT");

		UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
		String authToken = upiUtils.getToken();

		con.setRequestProperty("Authorization", "Bearer " + authToken);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");
		con.setConnectTimeout(10000); // set timeout to 10 seconds

		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(params.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		SimpleObject responseObj = null;

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			stringResponse = response.toString();

			responseObj = upiUtils.processUpiResponse(stringResponse);
			responseObj.put("status", responseCode);

			return(responseObj);

		} else {
			if (con != null && con.getErrorStream() != null) {
				BufferedReader in = null;
				// BufferedReader in = new BufferedReader(new InputStreamReader(
				// 		con.getErrorStream()));
				in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				stringResponse = response.toString();
			}

			responseObj = new SimpleObject();
			responseObj.put("status", responseCode);
			responseObj.put("message", stringResponse);
			return(responseObj);
		}
		//return responseObj;
	}

	public static String getAuthToken() throws IOException, NoSuchAlgorithmException, KeyManagementException {
		UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
		return(upiUtils.getToken());
	}

	/**
	 * Get IPRS Verification error clients and error description        *
	 * @return
	 */
	public SimpleObject pullVerificationErrorsFromCR() {
		UpiUtilsDataExchange upiUtilsDataExchange = new UpiUtilsDataExchange();
		String authToken = upiUtilsDataExchange.getToken();
		GlobalProperty globalGetUrl = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_GET_END_POINT);
		String strGetUrl = globalGetUrl.getPropertyValue();
		Location location = Context.getService(KenyaEmrService.class).getDefaultLocation();
		String facilityMfl = getDefaultLocationMflCode(location);
		SimpleObject responseObj = new SimpleObject();
		String success = "";
		try {
			String getUrl = strGetUrl + "/validation-results/kmfl/" + facilityMfl;
			System.out.println("Using NUPI GET URL: " + getUrl);
			URL url = new URL(getUrl);

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			con.setRequestProperty("Authorization", "Bearer " + authToken);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");
			con.setConnectTimeout(10000); // set timeout to 10 seconds

			int responseCode = con.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				BufferedReader in = null;
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				String stringResponse = response.toString();
				success = processIPRSVerificationErrorsResponse(stringResponse);

			}else{
				System.out.println("Http connection response code ==> "+ responseCode);
			}

			} catch (Exception ex) {
		}
		return SimpleObject.create("success", success );

	}

	/**
	 * Processes IPRS Verification error clients and error description
	 *
	 * @param stringResponse the NUPI payload
	 * @return SimpleObject the processed data
	 */
	public  String processIPRSVerificationErrorsResponse(String stringResponse) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = null;
		String clientNumber = "";
		String errorDescription = "";
		String success = "false";
		try {
			objectNode = (ObjectNode) mapper.readTree(stringResponse);
			if (objectNode != null) {
				ArrayNode resultsArrayNode = (ArrayNode) objectNode.get("results");
				if (resultsArrayNode.size() > 0) {
					for (int i = 0; i < resultsArrayNode.size(); i++) {

						clientNumber = resultsArrayNode.get(i).get("clientNumber").asText();
						errorDescription = resultsArrayNode.get(i).get("errorDescription").asText();
						// Get client by clientNumber: NUPI Identifier type
						PatientIdentifierType nupiIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
						PatientService patientService = Context.getPatientService();
					    List<Patient> patients = patientService.getPatients(null, clientNumber.trim(), Arrays.asList(nupiIdentifierType), false);
						if (patients.size() > 0) {
							Patient patient = patients.get(0);
							PatientWrapper wrapper = new PatientWrapper(patient);
							wrapper.setCRVerificationStatus(getAttributeSubstring("Failed IPRS Check"));
							wrapper.setCRIPRSVerificationErrorDescription(getAttributeSubstring(errorDescription));
							patientService.savePatient(patient);
							success = "true";
						}

					}
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}
  return success;
	}

	/**
	 * Processes POST Request to determine if client already has NUPI
	 *
	 * @param params the POST payload
	 * @return NUPI from the processed data
	 */
	public  String nationalUniquePatientNumberForClient(String params) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = null;
		String identificationType = "";
		String identificationNumber = "";
		String nupiNumber = "";
		try {
			objectNode = (ObjectNode) mapper.readTree(params);
			if (objectNode != null) {
				ArrayNode resultsArrayNode = (ArrayNode) objectNode.get("identifications");
				if (resultsArrayNode.size() > 0) {
					for (int i = 0; i < resultsArrayNode.size(); i++) {

						identificationType = resultsArrayNode.get(i).get("identificationType").asText();
						identificationNumber = resultsArrayNode.get(i).get("identificationNumber").asText();

						// Get client by identificationType:  Identifier type used
						PatientIdentifierType natIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
						PatientIdentifierType birtCertIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);

						PatientService patientService = Context.getPatientService();
						List<Patient> patients = patientService.getPatients(null, identificationNumber.trim(), Arrays.asList(natIdentifierType,birtCertIdentifierType), false);

						if (patients.size() > 0) {
							Patient patient = patients.get(0);

							// Got patient
							// Check whether patient already has NUPI
							PatientIdentifierType nupiIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
							PatientIdentifier nupiObject = patient.getPatientIdentifier(nupiIdentifierType);

							  nupiNumber = nupiObject.getIdentifier();

							}

						}

					}
				}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return nupiNumber;
	}
	/**
	 * This gets a substring of max 50 chars for PersonAttribute which is a limitation in the DB
	 * @param input
	 * @return
	 */
	public String getAttributeSubstring(String input) {
		String output = "";
		if(input != null) {
			int endIndex = (input.length()) > 50 ? 50 : (input.length());
			output = input.substring(0, endIndex);
		}
		return(output);
	}

}