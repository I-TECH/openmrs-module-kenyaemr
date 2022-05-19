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

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifierType;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;


public class UpiUtilsDataExchange {

	private Log log = LogFactory.getLog(UpiUtilsDataExchange.class);

	private List<PatientIdentifierType> allPatientIdentifierTypes;

	//OAuth variables
	private static final Pattern pat = Pattern.compile(".*\"access_token\"\\s*:\\s*\"([^\"]+)\".*");
	
	private String strClientId = ""; // clientId
	
	private String strClientSecret = ""; // client secret
	
	private String strScope = ""; // scope
	
	private String strTokenUrl = ""; // Token URL

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
				System.out.println("Client Number==>"+clientNumber);
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

}
