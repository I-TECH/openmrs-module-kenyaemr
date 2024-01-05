/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.nupi;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;


public class UpiUtilsDataExchange {

	private Log log = LogFactory.getLog(UpiUtilsDataExchange.class);

	private List<PatientIdentifierType> allPatientIdentifierTypes;

	//OAuth variables
	private static final Pattern pat = Pattern.compile(".*\"access_token\"\\s*:\\s*\"([^\"]+)\".*");
	
	private String strClientId = ""; // clientId
	
	private String strClientSecret = ""; // client secret
	
	private String strScope = ""; // scope
	
	private String strTokenUrl = ""; // Token URL

	private CountryCodeList countryCodeList = new CountryCodeList();

	private CountyCodeList countyCodeList = new CountyCodeList();

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

	/**
	 * Processes CR response for updating UPI number fetched from CR server
	 * 
	 * @param stringResponse the upi payload
	 * @return SimpleObject the processed data
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
				clientNumber = jsonNode.get("clientNumber").getTextValue();
				responseObj.put("clientNumber", clientNumber);
			}
		}
		catch (Exception e) {
				e.printStackTrace();
			}
     return responseObj;
	}

	/**
	 * Initialize the OAuth variables
	 * 
	 * @return true on success or false on failure
	 */
	public boolean initAuthVars() {
		
		GlobalProperty globalTokenUrl = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_TOKEN_URL);
		strTokenUrl = globalTokenUrl.getPropertyValue();
		
		GlobalProperty globalScope = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_OAUTH2_SCOPE);
		strScope = globalScope.getPropertyValue();
		
		GlobalProperty globalClientSecret = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_OAUTH2_CLIENT_SECRET);
		strClientSecret = globalClientSecret.getPropertyValue();
		
		GlobalProperty globalClientId = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_OAUTH2_CLIENT_ID);
		strClientId = globalClientId.getPropertyValue();
		
		if (strTokenUrl == null || strScope == null || strClientSecret == null || strClientId == null) {
			System.err.println("Get oauth data: Please set OAuth2 credentials");
			return (false);
		}
		return (true);
	}

	/**
	 * Get the Token
	 * 
	 * @return the token as a string and null on failure
	 */
	private String getClientCredentials() {
		
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
			connection.setConnectTimeout(10000); // set timeout to 10 seconds
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
				System.err.println("OAUTH2 Error : Token pattern mismatch");
			}
			
		}
		catch (Exception e) {
			System.err.println("OAUTH2 - Error : " + e.getMessage());
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
	 * Checks if the current token is valid and not expired
	 * 
	 * @return true if valid and false if invalid
	 */
	private boolean isValidToken() {
		String currentToken = Context.getAdministrationService().getGlobalProperty(CommonMetadata.GP_CLIENT_VERIFICATION_API_TOKEN);
		ObjectMapper mapper = new ObjectMapper();
		try {
			ObjectNode jsonNode = (ObjectNode) mapper.readTree(currentToken);
			if (jsonNode != null) {
				long expiresSeconds = jsonNode.get("expires_in").getLongValue();
				String token = jsonNode.get("access_token").getTextValue();
				if(token != null && token.length() > 0)
				{
					String[] chunks = token.split("\\.");
					Base64.Decoder decoder = Base64.getUrlDecoder();

					String header = new String(decoder.decode(chunks[0]));
					String payload = new String(decoder.decode(chunks[1]));

					ObjectNode payloadNode = (ObjectNode) mapper.readTree(payload);
					long expiryTime = payloadNode.get("exp").getLongValue();

					long currentTime = System.currentTimeMillis()/1000;

					// check if expired
					if (currentTime < expiryTime) {
						return(true);
					} else {
						return(false);
					}
				}
				return(false);
			} else {
				return(false);
			}
		} catch(Exception e) {
			return(false);
		}
	}

	/**
	 * Gets the OAUTH2 token
	 * 
	 * @return String the token or empty on failure
	 */
	public String getToken() {
		//check if current token is valid
		if(isValidToken()) {
			return(Context.getAdministrationService().getGlobalProperty(CommonMetadata.GP_CLIENT_VERIFICATION_API_TOKEN));
		} else {
			// Init the auth vars
			boolean varsOk = initAuthVars();
			if (varsOk) {
				//Get the OAuth Token
				String credentials = getClientCredentials();
				//Save on global and return token
				if (credentials != null) {
					Context.getAdministrationService().setGlobalProperty(CommonMetadata.GP_CLIENT_VERIFICATION_API_TOKEN, credentials);
					return(credentials);
				}
			}
		}
		return(null);
	}

	/**
	 * Update patients CCC numbers for NUPI on Client Registry
	 * 
	 * @return Integer number of records updated
	 */
	public Integer updateNUPIcccNumbers(HashSet<Patient> patientsGroup) {
		Integer ret = 0;
		PatientIdentifierType ccc = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        PatientIdentifierType nupi = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);

		for (Patient patient : patientsGroup) {
			try {
				PatientIdentifier piccc = patient.getPatientIdentifier(ccc);
				String cccNum = piccc.getIdentifier();
				PatientIdentifier pinupi = patient.getPatientIdentifier(nupi);
				String nupiNum = pinupi.getIdentifier();
				String authToken = getToken();
				GlobalProperty globalPostUrl = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_POST_END_POINT);
				String strUpdateCCCUrl = globalPostUrl.getPropertyValue();
				strUpdateCCCUrl = strUpdateCCCUrl + "/" + nupiNum  + "/" + "update-ccc";

				URL url = new URL(strUpdateCCCUrl);

				HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
				con.setRequestMethod("PUT");

				con.setRequestProperty("Authorization", "Bearer " + authToken);
				con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				con.setRequestProperty("Accept", "application/json");
				con.setConnectTimeout(10000); // set timeout to 10 seconds

				// Payload
				SimpleObject payloadObj = new SimpleObject();
				payloadObj.put("nascopCCCNumber", cccNum);
				String payload = payloadObj.toJson();

				con.setDoOutput(true);
				OutputStream os = con.getOutputStream();
				os.write(payload.getBytes());
				os.flush();
				os.close();

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
		
					SimpleObject responseObj = processCCCUpdateResponse(stringResponse);
					responseObj.put("status", responseCode);

					// update the patient CCC sync status
					PatientWrapper wrapper = new PatientWrapper(patient);
					wrapper.setCRcccSyncStatus(getAttributeSubstring("Done"));
					wrapper.setCRcccSyncMessage("");
					Context.getPatientService().savePatient(patient);
					System.out.println("Successfully synced ccc for patient: " + patient.getPatientId());
		
				} else {
					String stringResponse = "";
					if(con != null && con.getErrorStream() != null) {
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
		
					SimpleObject responseObj = new SimpleObject();
					responseObj.put("status", responseCode);
					responseObj.put("message", stringResponse);
					
					String err = responseObj.get("status").toString() + " : " + responseObj.get("message").toString();

					// update the patient CCC sync status
					PatientWrapper wrapper = new PatientWrapper(patient);
					wrapper.setCRcccSyncStatus(getAttributeSubstring("Pending"));
					wrapper.setCRcccSyncMessage(getAttributeSubstring(err));
					Context.getPatientService().savePatient(patient);
					System.out.println("Error: Failed to sync ccc for patient: " + patient.getPatientId());
				}
			} catch(Exception ex) {
				System.err.println("Error updating CCC for client on CR: " + ex.getMessage());
				ex.printStackTrace();
			}

			try {
				//Delay for 5 seconds
				Thread.sleep(5000);
			} catch (Exception ie) {
				Thread.currentThread().interrupt();
			}
			ret++;
		}
		
		return(ret);
	}

	/**
	 * Processes CR response for updating CCC number on CR server
	 * 
	 * @param stringResponse the upi payload
	 * @return SimpleObject the processed data
	*/
	public static SimpleObject processCCCUpdateResponse(String stringResponse) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = null;
		String message = "";
		SimpleObject responseObj = new SimpleObject();

		try {
			jsonNode = mapper.readTree(stringResponse);
			if (jsonNode != null) {
				message = jsonNode.get("message").getTextValue();
				responseObj.put("message", message);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
     	return responseObj;
	}

	/**
	 * Get NUPI for all patients
	 * 
	 * @return Integer number of records updated
	 */
	public Integer getNUPIforAll(HashSet<Patient> patientsGroup) {
		Integer ret = 0;
		PatientIdentifierType nationalID = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
		PatientIdentifierType passportNumber = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER);
		PatientIdentifierType birthCertificateNumber = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);

		for (Patient patient : patientsGroup) {
			System.err.println("NUPI for All; Got the patient as: " + patient.getPatientId() + " : " + ret  + "/" + patientsGroup.size());
			String natID = "";
			Boolean hasNatID = false;
			String passportNum = "";
			Boolean hasPassNum = false;
			String birthCert = "";
			Boolean hasBirthCert = false;
			try {
				PatientWrapper wrapper = new PatientWrapper(patient);
				//national id
				PatientIdentifier piNatId = patient.getPatientIdentifier(nationalID);
				if(piNatId != null) {
					natID = piNatId.getIdentifier();
					hasNatID = true;
					System.err.println("Got the national id as: " + natID);
				}
				//passport number
				PatientIdentifier piPassNum = patient.getPatientIdentifier(passportNumber);
				if(piPassNum != null) {
					passportNum = piPassNum.getIdentifier();
					hasPassNum = true;
					System.err.println("Got the passport number as: " + passportNum);
				}
				//birth certificate
				PatientIdentifier piBirthCert = patient.getPatientIdentifier(birthCertificateNumber);
				if(piBirthCert != null) {
					birthCert = piBirthCert.getIdentifier();
					hasBirthCert = true;
					System.err.println("Got the birth certificate as: " + birthCert);
				}
				//country
				Obs obsCountry = getLatestObs(patient, Dictionary.COUNTRY);
				String countryCode = "KE";
				if (obsCountry != null) {
					Concept conCountry = obsCountry.getValueCoded();
					countryCode = getCountryCode(conCountry);
				}
				// Check if patient is already on CR and update patient NUPI if found existing
				if(!checkOnCRIfPatientExistsAndUpdate(patient, hasNatID, natID, hasPassNum, passportNum, hasBirthCert, birthCert, countryCode)) {
					try {
						String authToken = getToken();
						GlobalProperty globalPostUrl = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_POST_END_POINT);
						String strPostUrl = globalPostUrl.getPropertyValue();
						Location location = Context.getService(KenyaEmrService.class).getDefaultLocation();

						System.out.println("Using NUPI POST URL: " + strPostUrl);
						URL url = new URL(strPostUrl);

						HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
						con.setRequestMethod("POST");

						con.setRequestProperty("Authorization", "Bearer " + authToken);
						con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
						con.setRequestProperty("Accept", "application/json");
						con.setConnectTimeout(10000); // set timeout to 10 seconds

						String payload = generateNUPIpostPayload(patient, hasNatID, natID, hasPassNum, passportNum, hasBirthCert, birthCert, countryCode);

						con.setDoOutput(true);
						OutputStream os = con.getOutputStream();
						os.write(payload.getBytes());
						os.flush();
						os.close();

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
							System.out.println("Got the Response as: " + stringResponse);
				
							SimpleObject responseObj = processNUPIpostResponse(stringResponse);
							String NUPI = (String) responseObj.get("clientNumber");
							System.out.println("Got the NUPI as: " + NUPI);
							// update the patient NUPI
							wrapper.setNationalUniquePatientNumber(NUPI, location);
							wrapper.setCRVerificationStatus(getAttributeSubstring("Verified"));
							wrapper.setCRVerificationMessage("");
							Context.getPatientService().savePatient(patient);
							System.out.println("Successfully updated patient NUPI: " + patient.getPatientId());
				
						} else {
							String stringResponse = "";
							if(con != null && con.getErrorStream() != null) {
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
							} else {
								System.out.println("Could not get error stream");
							}

							// update the patient with verification error
							wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
							if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
								wrapper.setCRVerificationMessage(getAttributeSubstring("Missing Mandatory Fields: " + responseCode));
							} else {
								wrapper.setCRVerificationMessage(getAttributeSubstring("NETWORK Error: " + responseCode));
							}
							Context.getPatientService().savePatient(patient);

							System.out.println("Error getting NUPI for client: " + responseCode + " : " + stringResponse);
						}
					} catch(Exception ex) {
						try {
							wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
							if(ex.getMessage() == null) {
								wrapper.setCRVerificationMessage(getAttributeSubstring("Missing Mandatory Fields"));
							} else {
								wrapper.setCRVerificationMessage(getAttributeSubstring("Error : " + ex.getMessage()));
							}
							Context.getPatientService().savePatient(patient);
						} catch(Exception e) {}
						System.err.println("Error getting NUPI for client: " + ex.getMessage());
						ex.printStackTrace();
					}
				}
			} catch(Exception ex) {
				System.err.println("General Error getting NUPI for client: " + ex.getMessage());
				ex.printStackTrace();
			}	

			try {
				//Delay for 5 seconds
				Thread.sleep(5000);
			}
			catch (Exception ie) {
				Thread.currentThread().interrupt();
			}
			ret++;
		}
		
		return(ret);
	}

	/**
	 * Generates the payload used to post a patients details to CR
	 * @param patient
	 * @return
	 */
	private Boolean checkOnCRIfPatientExistsAndUpdate(Patient patient, Boolean hasNatID, String natID, Boolean hasPassNum, String passportNum, Boolean hasBirthCert, String birthCert, String countryCode) {
		String authToken = getToken();
		GlobalProperty globalGetUrl = Context.getAdministrationService().getGlobalPropertyObject(CommonMetadata.GP_CLIENT_VERIFICATION_GET_END_POINT);
		String strGetUrl = globalGetUrl.getPropertyValue();
		Location location = Context.getService(KenyaEmrService.class).getDefaultLocation();
		PatientWrapper wrapper = new PatientWrapper(patient);
		// If patient has a national ID
		if(hasNatID == true) {
			try {
				String getUrl = strGetUrl + '/' + countryCode + "/national-id/" + natID;
				System.out.println("Using NUPI GET URL: " + getUrl);
				URL url = new URL(getUrl);

				HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
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
					System.out.println("Got the Response as: " + stringResponse);
		
					SimpleObject responseObj = processNUPIgetResponse(stringResponse);
					Boolean clientExists = (Boolean) responseObj.get("clientExists");
					if(clientExists == true) {
						String NUPI = (String) responseObj.get("clientNumber");
						System.out.println("Got the NUPI as: " + NUPI);
						// update the patient NUPI
						wrapper.setNationalUniquePatientNumber(NUPI, location);
						wrapper.setCRVerificationStatus(getAttributeSubstring("Verified"));
						wrapper.setCRVerificationMessage("");
						Context.getPatientService().savePatient(patient);
						System.out.println("Successfully updated patient NUPI: " + patient.getPatientId());
						// return true to end processing for this client
						return(true);
					} else {
						wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
						wrapper.setCRVerificationMessage(getAttributeSubstring("Client does not exist on CR"));
						Context.getPatientService().savePatient(patient);
						System.out.println("Client does not exist on CR");
					}
		
				} else {
					wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
					wrapper.setCRVerificationMessage(getAttributeSubstring("Error getting NUPI for client: " + responseCode));
					Context.getPatientService().savePatient(patient);
					System.out.println("Error getting NUPI for client: " + responseCode);
				}
			} catch(Exception ex) {
				try {
					wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
					wrapper.setCRVerificationMessage(getAttributeSubstring("Error : " + ex.getMessage()));
					Context.getPatientService().savePatient(patient);
				} catch(Exception e) {}
				System.err.println("Error getting NUPI for client: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		// If patient has a passport number
		if(hasPassNum == true) {
			try {
				String getUrl = strGetUrl + '/' + countryCode + "/passport/" + passportNum;
				System.out.println("Using NUPI GET URL: " + getUrl);
				URL url = new URL(getUrl);

				HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
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
					System.out.println("Got the Response as: " + stringResponse);
		
					SimpleObject responseObj = processNUPIgetResponse(stringResponse);
					Boolean clientExists = (Boolean) responseObj.get("clientExists");
					if(clientExists == true) {
						String NUPI = (String) responseObj.get("clientNumber");
						System.out.println("Got the NUPI as: " + NUPI);
						// update the patient NUPI
						wrapper.setNationalUniquePatientNumber(NUPI, location);
						wrapper.setCRVerificationStatus(getAttributeSubstring("Verified"));
						wrapper.setCRVerificationMessage("");
						Context.getPatientService().savePatient(patient);
						System.out.println("Successfully updated patient NUPI: " + patient.getPatientId());
						// return true to end processing for this client
						return(true);
					} else {
						wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
						wrapper.setCRVerificationMessage(getAttributeSubstring("Client does not exist on CR"));
						Context.getPatientService().savePatient(patient);
						System.out.println("Client does not exist on CR");
					}
		
				} else {
					wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
					wrapper.setCRVerificationMessage(getAttributeSubstring("Error getting NUPI for client: " + responseCode));
					Context.getPatientService().savePatient(patient);
					System.out.println("Error getting NUPI for client: " + responseCode);
				}
			} catch(Exception ex) {
				try {
					wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
					wrapper.setCRVerificationMessage(getAttributeSubstring("Error : " + ex.getMessage()));
					Context.getPatientService().savePatient(patient);
				} catch(Exception e) {}
				System.err.println("Error getting NUPI for client: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		// If patient has a birth certificate number
		if(hasBirthCert == true) {
			try {
				String getUrl = strGetUrl + '/' + countryCode + "/birth-certificate/" + birthCert;
				System.out.println("Using NUPI GET URL: " + getUrl);
				URL url = new URL(getUrl);

				HttpsURLConnection con =(HttpsURLConnection) url.openConnection();
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
					System.out.println("Got the Response as: " + stringResponse);
		
					SimpleObject responseObj = processNUPIgetResponse(stringResponse);
					Boolean clientExists = (Boolean) responseObj.get("clientExists");
					if(clientExists == true) {
						String NUPI = (String) responseObj.get("clientNumber");
						System.out.println("Got the NUPI as: " + NUPI);
						// update the patient NUPI
						wrapper.setNationalUniquePatientNumber(NUPI, location);
						wrapper.setCRVerificationStatus(getAttributeSubstring("Verified"));
						wrapper.setCRVerificationMessage("");
						Context.getPatientService().savePatient(patient);
						System.out.println("Successfully updated patient NUPI: " + patient.getPatientId());
						// return true to end processing for this client
						return(true);
					} else {
						wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
						wrapper.setCRVerificationMessage(getAttributeSubstring("Client does not exist on CR"));
						Context.getPatientService().savePatient(patient);
						System.out.println("Client does not exist on CR");
					}
		
				} else {
					wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
					wrapper.setCRVerificationMessage(getAttributeSubstring("Error getting NUPI for client: " + responseCode));
					Context.getPatientService().savePatient(patient);
					System.out.println("Error getting NUPI for client: " + responseCode);
				}
			} catch(Exception ex) {
				try {
					wrapper.setCRVerificationStatus(getAttributeSubstring("Pending"));
					wrapper.setCRVerificationMessage(getAttributeSubstring("Error : " + ex.getMessage()));
					Context.getPatientService().savePatient(patient);
				} catch(Exception e) {}
				System.err.println("Error getting NUPI for client: " + ex.getMessage());
				ex.printStackTrace();
			}
		}

		return(false);
	}

	/**
	 * Generates the payload used to post a patients details to CR
	 * @param patient
	 * @return
	 */
	private String generateNUPIpostPayload(Patient patient, Boolean hasNatID, String natID, Boolean hasPassNum, String passportNum, Boolean hasBirthCert, String birthCert, String countryCode) {
		String payload = "";
		PatientIdentifierType ccc = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        PatientIdentifierType nupi = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
		
		// Payload
		String cccNum = "";
		Boolean isOnART = false;
		SimpleObject payloadObj = new SimpleObject();
		SimpleObject residence = new SimpleObject();
		SimpleObject contact = new SimpleObject();
		List<SimpleObject> identifications = new ArrayList<SimpleObject>();
		List<String> kins = new ArrayList<String>();
		PersonAddress personAddress = patient.getPersonAddress();
		KenyaEmrService kenyaEmrService = Context.getService(KenyaEmrService.class);
		//marital status
		Obs obsMaritalStatus = getLatestObs(patient, Dictionary.CIVIL_STATUS);
		String maritalStatus = "-";
		if (obsMaritalStatus != null) {
			Concept conMaritalStatus = obsMaritalStatus.getValueCoded();
			maritalStatus = getMaritalStatus(conMaritalStatus);
		}
		//occupation
		Obs obsOccupation = getLatestObs(patient, Dictionary.OCCUPATION);
		String occupation = "-";
		if (obsOccupation != null) {
			Concept conOccupation = obsOccupation.getValueCoded();
			occupation = getOccupation(conOccupation);
		}
		//ccc
		PatientIdentifier piccc = patient.getPatientIdentifier(ccc);
		if(piccc != null) {
			cccNum = piccc.getIdentifier();
			isOnART = true;
			System.err.println("Got the ccc as: " + cccNum);
		}
		//education
		Obs obsEducation = getLatestObs(patient, Dictionary.EDUCATION);
		String education = "-";
		if (obsEducation != null) {
			Concept conEducation = obsEducation.getValueCoded();
			education = getEducation(conEducation);
		}
		//county
		String countyCode = countyCodeList.getCountyCode(personAddress.getCountyDistrict());
		payloadObj.put("firstName", patient.getGivenName() != null ? patient.getGivenName() : "");
		payloadObj.put("middleName", patient.getMiddleName() != null ? patient.getMiddleName() : "");
		payloadObj.put("lastName", patient.getFamilyName() != null ? patient.getFamilyName() : "");
		payloadObj.put("dateOfBirth", formatDate(patient.getBirthdate()));
		payloadObj.put("maritalStatus", maritalStatus);
		payloadObj.put("gender", formatGender(patient.getGender()));
		payloadObj.put("occupation", occupation);
		payloadObj.put("religion", "");
		payloadObj.put("educationLevel", education);
		payloadObj.put("country", countryCode);
		payloadObj.put("countyOfBirth", countyCode);
		payloadObj.put("isAlive", true);
		payloadObj.put("originFacilityKmflCode", kenyaEmrService.getDefaultLocationMflCode());
		payloadObj.put("isOnART", isOnART);
		payloadObj.put("nascopCCCNumber", cccNum);
			residence.put("county", countyCode);
			residence.put("subCounty", formatLocation(personAddress.getStateProvince()));
			residence.put("ward", formatLocation(personAddress.getAddress4()));
			residence.put("village", personAddress.getCityVillage() != null ? personAddress.getCityVillage() : "");
			residence.put("landMark", personAddress.getAddress2() != null ? personAddress.getAddress2() : "");
			residence.put("address", personAddress.getAddress1() != null ? personAddress.getAddress1() : "");
		payloadObj.put("residence", residence);
		if(hasNatID == true) {
			SimpleObject identification = new SimpleObject();
			identification.put("CountryCode", countryCode);
			identification.put("identificationType", "national-id");
			identification.put("identificationNumber", natID);
			identifications.add(identification);
		}
		if(hasPassNum == true) {
			SimpleObject identification = new SimpleObject();
			identification.put("CountryCode", countryCode);
			identification.put("identificationType", "passport");
			identification.put("identificationNumber", passportNum);
			identifications.add(identification);
		}
		if(hasBirthCert == true) {
			SimpleObject identification = new SimpleObject();
			identification.put("CountryCode", countryCode);
			identification.put("identificationType", "birth-certificate");
			identification.put("identificationNumber", birthCert);
			identifications.add(identification);
		}
		payloadObj.put("identifications", identifications.toArray());
		// Primary Telephone
		String primaryPhone = "";
		PersonAttributeType patTel = MetadataUtils.possible(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);
		if(patTel != null) {
			PersonAttribute paTel = patient.getAttribute(patTel);
			if(paTel != null) {
				primaryPhone = paTel.getValue();
			}
		}
		// Secondary Telephone
		String secondaryPhone = "";
		PersonAttributeType patSecTel = MetadataUtils.possible(PersonAttributeType.class, CommonMetadata._PersonAttributeType.ALTERNATE_PHONE_CONTACT);
		if(patSecTel != null) {
			PersonAttribute paSecTel = patient.getAttribute(patSecTel);
			if(paSecTel != null) {
				secondaryPhone = paSecTel.getValue();
			}
		}
		// Email Address
		String emailAddress = "";
		PersonAttributeType patEmail = MetadataUtils.possible(PersonAttributeType.class, CommonMetadata._PersonAttributeType.EMAIL_ADDRESS);
		if(patSecTel != null) {
			PersonAttribute paEmail = patient.getAttribute(patEmail);
			if(paEmail != null) {
				emailAddress = paEmail.getValue();
			}
		}
			contact.put("primaryPhone", primaryPhone);
			contact.put("secondaryPhone", secondaryPhone);
			contact.put("emailAddress", emailAddress);
		payloadObj.put("contact", contact);
		payloadObj.put("nextOfKins", kins.toArray());
		payload = payloadObj.toJson();
		System.out.println("Payload generated: " + payload);
		return(payload);
	}

	/**
	 * Get the latest OBS
	 * @param patient
	 * @param conceptIdentifier
	 * @return
	 */
	public Obs getLatestObs(Patient patient, String conceptIdentifier) {
		Concept concept = Dictionary.getConcept(conceptIdentifier);
		List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
		if (obs.size() > 0) {
			// these are in reverse chronological order
			return obs.get(0);
		}
		return null;
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

	/**
	 * Decode marital status
	 */
	public String getMaritalStatus(Concept status) {
		String ret = "-";
		if (status == Dictionary.getConcept(Dictionary.MARRIED_POLYGAMOUS))
			ret = "Married Polygamous";
		if (status == Dictionary.getConcept(Dictionary.MARRIED_MONOGAMOUS));
			ret = "Married Monogamous";
		if (status == Dictionary.getConcept(Dictionary.DIVORCED));
			ret = "Divorced";
		if (status == Dictionary.getConcept(Dictionary.WIDOWED));
			ret = "Widowed";
		if (status == Dictionary.getConcept(Dictionary.LIVING_WITH_PARTNER));
			ret = "Living With Partner";
		if (status == Dictionary.getConcept(Dictionary.NEVER_MARRIED));
			ret = "Never Married";
		return(ret);
	}

	/**
	 * Decode occupation
	 */
	public String getOccupation(Concept status) {
		String ret = "-";
		if (status == Dictionary.getConcept(Dictionary.FARMER))
			ret = "farmer";
		if (status == Dictionary.getConcept(Dictionary.TRADER))
			ret = "trader";
		if (status == Dictionary.getConcept(Dictionary.EMPLOYEE));
			ret = "employee";
		if (status == Dictionary.getConcept(Dictionary.STUDENT));
			ret = "student";
		if (status == Dictionary.getConcept(Dictionary.DRIVER));
			ret = "driver";
		if (status == Dictionary.getConcept(Dictionary.NONE));
			ret = "node";
		if (status == Dictionary.getConcept(Dictionary.OTHER_NON_CODED));
			ret = "other";
		return(ret);
	}

	/**
	 * Decode education
	 */
	public String getEducation(Concept status) {
		String ret = "-";
		if (status == Dictionary.getConcept(Dictionary.NONE))
			ret = "none";
		if (status == Dictionary.getConcept(Dictionary.PRIMARY_EDUCATION))
			ret = "primary";
		if (status == Dictionary.getConcept(Dictionary.SECONDARY_EDUCATION));
			ret = "secondary";
		if (status == Dictionary.getConcept(Dictionary.COLLEGE_UNIVERSITY_POLYTECHNIC));
			ret = "college-university-polytechnic";
		return(ret);
	}

	/**
	 * Decode country code
	 */
	public String getCountryCode(Concept status) {
		String ret = "-";
		System.out.println("Got country name as: " + status.getDisplayString());
		Integer id = status.getId();
		System.out.println("Got country id as: " + id);
		ret = countryCodeList.getCountryCode(id);
		System.out.println("Got country code as: " + ret);
		return(ret);
	}

	/**
	 * Format a date
	 * @param date
	 * @return
	 */
	private String formatDate(Date date) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return date == null ? "" : dateFormatter.format(date);
	}

	/**
	 * Format gender
	 * @param input
	 * @return
	 */
	private String formatGender(String input) {
		String in = input.trim().toLowerCase();
		if(in.equalsIgnoreCase("m"))
			return("male");
		if(in.equalsIgnoreCase("f"))
			return("female");
		return("male");
	}

	/**
	 * Format location
	 */
	private String formatLocation(String input) {
		String ret = "";
		if(input != null) {
			input = input.trim().toLowerCase();
			input = input.replace(" ", "-");
			input = input.replace("/", "-");
			ret = input;
		}
		return(ret);
	}

	/**
	 * Processes CR GET response for updating NUPI number fetched from CR server
	 * 
	 * @param stringResponse the NUPI payload
	 * @return SimpleObject the processed data
	*/
	public static SimpleObject processNUPIgetResponse(String stringResponse) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = null;
		Boolean clientExists = false;
		String clientNumber = "";
		SimpleObject responseObj = new SimpleObject();

		try {
			jsonNode = mapper.readTree(stringResponse);
			if (jsonNode != null) {
				clientExists = jsonNode.get("clientExists") == null ? false : jsonNode.get("clientExists").getBooleanValue();
				responseObj.put("clientExists", clientExists);
				JsonNode client = jsonNode.get("client");
				clientNumber = client.get("clientNumber") == null ? "" : client.get("clientNumber").getTextValue();
				responseObj.put("clientNumber", clientNumber);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
     	return responseObj;
	}

	/**
	 * Processes CR POST response for updating NUPI number fetched from CR server
	 * 
	 * @param stringResponse the NUPI payload
	 * @return SimpleObject the processed data
	*/
	public static SimpleObject processNUPIpostResponse(String stringResponse) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = null;
		String clientNumber = "";
		SimpleObject responseObj = new SimpleObject();

		try {
			jsonNode = mapper.readTree(stringResponse);
			if (jsonNode != null) {
				clientNumber = jsonNode.get("clientNumber") == null ? "" : jsonNode.get("clientNumber").getTextValue();
				responseObj.put("clientNumber", clientNumber);
			}
		}
		catch (Exception e) {
				e.printStackTrace();
		}
     return responseObj;
	}

}
