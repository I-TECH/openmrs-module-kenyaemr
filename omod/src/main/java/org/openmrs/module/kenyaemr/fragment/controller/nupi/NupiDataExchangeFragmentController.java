/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.nupi;

import static org.openmrs.module.kenyaemrorderentry.util.Utils.getDefaultLocationMflCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.nupi.UpiUtilsDataExchange;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Base64;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class NupiDataExchangeFragmentController  {

	//OAuth variables
	private static final Pattern pat = Pattern.compile(".*\"access_token\"\\s*:\\s*\"([^\"]+)\".*");
	
	private String strClientId = ""; // clientId
	
	private String strClientSecret = ""; // client secret
	
	private String strScope = ""; // scope
	
	private String strTokenUrl = ""; // Token URL
	
	private String strDWHbackEndURL = ""; // DWH backend URL
	
	private String strAuthURL = ""; // DWH auth URL
	
	private String strFacilityCode = ""; // Facility Code
	
	private String strPagingThreshold = ""; // Paging of response (Number of items per page)
	
	private long recordsPerPull = 400; // Total number of records per request

	PatientService patientService = Context.getPatientService();

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
	 * Initialize the NDWH OAuth variables
	 * 
	 * @return true on success or false on failure
	 */
	public boolean initNDWHGlobalVars() {
		String dWHbackEndURL = "kenyaemr.ndwh.nupi.backend.url";
		GlobalProperty globalDWHbackEndURL = Context.getAdministrationService().getGlobalPropertyObject(dWHbackEndURL);
		strDWHbackEndURL = globalDWHbackEndURL.getPropertyValue();
		
		String tokenUrl = "kenyaemr.ndwh.nupi.token.url";
		GlobalProperty globalTokenUrl = Context.getAdministrationService().getGlobalPropertyObject(tokenUrl);
		strTokenUrl = globalTokenUrl.getPropertyValue();
		
		String scope = "kenyaemr.ndwh.nupi.scope";
		GlobalProperty globalScope = Context.getAdministrationService().getGlobalPropertyObject(scope);
		strScope = globalScope.getPropertyValue();
		
		String clientSecret = "kenyaemr.ndwh.nupi.client.secret";
		GlobalProperty globalClientSecret = Context.getAdministrationService().getGlobalPropertyObject(clientSecret);
		strClientSecret = globalClientSecret.getPropertyValue();
		
		String clientId = "kenyaemr.ndwh.nupi.client.id";
		GlobalProperty globalClientId = Context.getAdministrationService().getGlobalPropertyObject(clientId);
		strClientId = globalClientId.getPropertyValue();
		
		String authURL = "kenyaemr.ndwh.nupi.authorization.url";
		GlobalProperty globalAuthURL = Context.getAdministrationService().getGlobalPropertyObject(authURL);
		strAuthURL = globalAuthURL.getPropertyValue();
		
		String gpResponsePaging = "kenyaemr.ndwh.nupi.paging";
		GlobalProperty responsePagingString = Context.getAdministrationService().getGlobalPropertyObject(gpResponsePaging);
		strPagingThreshold = responsePagingString.getPropertyValue();
		
		KenyaEmrService emrService = Context.getService(KenyaEmrService.class);
		emrService.getDefaultLocationMflCode();
		strFacilityCode = emrService.getDefaultLocationMflCode();
		
		if (strDWHbackEndURL == null || strTokenUrl == null || strScope == null || strClientSecret == null
		        || strClientId == null || strAuthURL == null) {
			System.err.println("Get NDWH NUPI data: Please set DWH OAuth credentials");
			return (false);
		}
		return (true);
	}

	/**
	 * Get the NDWH Token
	 * 
	 * @return the token as a string and null on failure
	 */
	private String getNDWHClientCredentials() {
		
		String auth = strClientId + ":" + strClientSecret;
		String authentication = Base64.getEncoder().encodeToString(auth.getBytes());
		BufferedReader reader = null;
		HttpsURLConnection connection = null;
		String returnValue = "";
		try {
			StringBuilder parameters = new StringBuilder();
			parameters.append("grant_type=" + URLEncoder.encode("client_credentials", "UTF-8"));
			parameters.append("&");
			parameters.append("scope=" + URLEncoder.encode(strScope, "UTF-8"));
			URL url = new URL(strTokenUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + authentication);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Accept", "application/json");
			PrintStream os = new PrintStream(connection.getOutputStream());
			os.print(parameters);
			os.close();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			String response = out.toString();
			Matcher matcher = pat.matcher(response);
			if (matcher.matches() && matcher.groupCount() > 0) {
				returnValue = matcher.group(1);
			} else {
				System.err.println("IIT ML - Error : Token pattern mismatch");
			}
			
		}
		catch (Exception e) {
			System.err.println("IIT ML - Error : " + e.getMessage());
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {}
			}
			connection.disconnect();
		}
		return returnValue;
	}

	/**
	 * Get the total NUPI duplicate records on remote side
	 * 
	 * @param bearerToken the OAuth2 token
	 * @return long available number of records
	 */
	private long getAvailableRecordsOnRemoteSide(String bearerToken) {
		BufferedReader reader = null;
		HttpsURLConnection connection = null;
		try {
			URL url = new URL(strDWHbackEndURL + "?code=FND&name=DuplicateReport&pageNumber=1&pageSize=1&siteCode=" + strFacilityCode);
			System.out.println("NDWH NUPI Duplicates - Getting available data count using URL: " + url);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			String response = out.toString();
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonNode = (ObjectNode) mapper.readTree(response);
			if (jsonNode != null) {
				long pageCount = jsonNode.get("pageCount").getLongValue();
				System.out.println("NDWH NUPI Duplicates - Got available data count as: " + pageCount);
				return (pageCount);
			} else {
				System.out.println("NDWH NUPI Duplicates - No available data");
				return (0);
			}
		}
		catch (Exception e) {
			System.err.println("NDWH NUPI Duplicates - Error getting total remote records: " + e.getMessage());
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {}
			}
			connection.disconnect();
		}
		return (0);
	}

	/**
	 * Get NUPI Duplicates from NDWH       *
	 * @return
	 */
	public SimpleObject pullDuplicatesFromNDWH() {
		boolean success = false;

		// We have finished the data pull task. We now set the flag.
		// setStatusOfPullDataTask(true);

		// Init the auth vars
		boolean varsOk = initNDWHGlobalVars();
		if (varsOk) {
			//Get the OAuth Token
			String credentials = getNDWHClientCredentials();
			//Get the data
			if (credentials != null) {
				//get total record count
				long totalRemote = getAvailableRecordsOnRemoteSide(credentials);
				System.out.println("NDWH NUPI Duplicates - Total Remote Records: " + totalRemote);
				
				if (totalRemote > 0) {
					//We now pull and save
					pullAndSaveNUPIDuplicates(credentials, totalRemote);
				} else {
					System.err.println("NDWH NUPI Duplicates - No records on remote side");
					// setStatusOfPullDataTask(false);
					success = false;
				}
			} else {
				System.err.println("NDWH NUPI Duplicates - Failed to get the OAuth token");
				// setStatusOfPullDataTask(false);
				success = false;
			}
		} else {
			System.err.println("NDWH NUPI Duplicates - Failed to get the OAuth Vars");
			// setStatusOfPullDataTask(false);
			success = false;
		}

		// We have finished the data pull task. We now set the flag.
		// setStatusOfPullDataTask(false);

		success = true;

		return SimpleObject.create("success", success );
	}

	/**
	 * Pulls records and updates patient status
	 * 
	 * @param bearerToken the OAuth2 token
	 * @param totalRemote the available number of records in NDWH
	 * @return true when successfull and false on failure
	 */
	private boolean pullAndSaveNUPIDuplicates(String bearerToken, long totalRemote) {

		HashSet<Integer> remotePatients = new HashSet<Integer>();
		Integer foundPatients = 0;

		if (StringUtils.isNotBlank(strPagingThreshold)) {
			long configuredValue = Long.valueOf(strPagingThreshold);
			if (configuredValue > 0) {
				recordsPerPull = configuredValue;
			}
		}
		long totalPages = (long) (Math.ceil((totalRemote * 1.0) / (recordsPerPull * 1.0)));
		
		long currentPage = 1;
		for (int i = 0; i < totalPages; i++) {
			// if (!getContinuePullingData()) {
			// 	return (false);
			// }
			BufferedReader reader = null;
			HttpsURLConnection connection = null;
			try {

				String fullURL = strDWHbackEndURL + "?code=FND&name=DuplicateReport&pageNumber=" + currentPage + "&pageSize=" + recordsPerPull + "&siteCode=" + strFacilityCode;
				System.out.println("NDWH NUPI Duplicates - Pulling data using: " + fullURL);
				URL url = new URL(fullURL);
				connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
				connection.setDoOutput(true);
				connection.setRequestMethod("GET");
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}
				String response = out.toString();
				
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode jsonNode = (ObjectNode) mapper.readTree(response);
				if (jsonNode != null) {
					
					JsonNode extract = jsonNode.get("extract");
					if (extract.isArray() && extract.size() > 0) {
						for (JsonNode personObject : extract) {
							foundPatients++;
							// if (!getContinuePullingData()) {
							// 	return (false);
							// }

							try {
								// String nupi = personObject.get("nupi").asText();
								// String cccNumber = personObject.get("cccNumber").asText();
								String patientId = personObject.get("PatientPK").asText();
								String facilityCode = personObject.get("FacilityCode").asText();
								String otherFacilities = personObject.get("OtherSiteCodes").asText();
								Integer totalFacilities = personObject.get("RecordCount").asInt();

								Patient patient = patientService.getPatient(Integer.valueOf(patientId));

								PatientWrapper wrapper = new PatientWrapper(patient);
								wrapper.setNUPIDuplicateStatus(getAttributeSubstring("true"));
								wrapper.setNUPIDuplicateFacility(facilityCode);
								wrapper.setNUPIDuplicateSites(otherFacilities);
								wrapper.setNUPITotalDuplicateSites(String.valueOf(totalFacilities));

								patientService.savePatient(patient);

								// Populate the remote patients list
								remotePatients.add(patient.getId());
							}
							catch (Exception ex) {
								//Failed to save record
								System.err.println("NDWH NUPI Duplicates - Error saving attributes: " + ex.getMessage());
								// ex.printStackTrace();
							}
						}
					} else {
						System.err.println("NDWH NUPI Duplicates - JSON Data extraction problem. Exiting");
						if (reader != null) {
							try {
								reader.close();
							}
							catch (Exception ex) {}
						}
						if (reader != null) {
							try {
								connection.disconnect();
							}
							catch (Exception er) {}
						}
						return(false);
					}
				}
			}
			catch (Exception e) {
				System.err.println("NDWH NUPI Duplicates - Error getting NUPI duplicates: " + e.getMessage());
				// e.printStackTrace();
			}
			finally {
				if (reader != null) {
					try {
						reader.close();
					}
					catch (IOException e) {}
				}
				if (reader != null) {
					try {
						connection.disconnect();
					}
					catch (Exception er) {}
				}
			}
			
			// setDataPullStatus((long)Math.floor(((currentPage * 1.00 / totalPages * 1.00) * totalRemote)), totalRemote);
			currentPage++;

			try {
				//Delay for 5 seconds
				Thread.sleep(5000);
			}
			catch (Exception ie) {
				Thread.currentThread().interrupt();
			}
		}

		// Sync local to remote status. i.e ensure that if a patient is cleared on the remote side, they are also cleared here
		// Ensure that we got all remote records
		if(foundPatients == totalRemote) {
			System.out.println("NDWH NUPI Duplicates - Syncing NUPI Duplicates Status with remote");
			PersonAttributeType duplicateStatusPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.DUPLICATE_NUPI_STATUS_WITH_NATIONAL_REGISTRY);
			List<Patient> allPatients = Context.getPatientService().getAllPatients();

			// Get list of local patients with duplicates
			for (Patient patient : allPatients) {
				if (patient.getAttribute(duplicateStatusPA) != null) {
					// Has a duplicate?
					if (patient.getAttribute(duplicateStatusPA).getValue().trim().equalsIgnoreCase("true")) {
						//Check if exists in remote
						Integer patientId = patient.getId();
						if(!remotePatients.contains(patientId)) {
							//Patient does not exist on the remote side. We can clear the patient.
							PatientWrapper wrapper = new PatientWrapper(patient);
							wrapper.setNUPIDuplicateStatus(getAttributeSubstring("false"));
							wrapper.setNUPIDuplicateFacility("");
							wrapper.setNUPIDuplicateSites("");
							wrapper.setNUPITotalDuplicateSites("");

							patientService.savePatient(patient);
							System.out.println("NDWH NUPI Duplicates - Updating Sync Patient ID: " + patientId);
						}
					}
				}
			}
		} else {
			System.err.println("NDWH NUPI Duplicates - Error: We didnt manage to pull all remote records. We cannot sync local with remote");
		}

		return (true);
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