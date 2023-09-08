/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.form.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.WebConstants;
import org.apache.http.HttpHeaders;
import org.apache.commons.codec.binary.Base64;

/**
 *
 */
public class HivGreenCardPostSubmissionAction implements CustomFormSubmissionAction {

    /**
     * This is an action that runs immediately after the greencard is saved (submitted)
     */
    @Override
    public void applyAction(FormEntrySession session) {
        // Check if IIT is enabled
        String iitFeatureEnabled = "kenyaemrml.iitml.feature.enabled";
        GlobalProperty gpIITFeatureEnabled = Context.getAdministrationService().getGlobalPropertyObject(iitFeatureEnabled);

        if(gpIITFeatureEnabled != null && gpIITFeatureEnabled.getPropertyValue().trim().equalsIgnoreCase("true")) {
            // Update the patient IIT risk score
            // System.out.println("Updating the patient IIT risk score");
            Patient currentPatient = session.getPatient();
            Integer patientId = currentPatient.getId();
            // System.out.println("IIT risk score: Patient ID: " + patientId);
            SimpleObject constructPayload = new SimpleObject();
            constructPayload.put("patientId", patientId);
            String payload = constructPayload.toJson();

            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try {
                //Connection Params (Build the URL)
                String iitMLbackEndURLGlobal = "kenyaemrml.iitscore.updatepatientscore";
                GlobalProperty globalIITMLbackEndURL = Context.getAdministrationService().getGlobalPropertyObject(iitMLbackEndURLGlobal);
                String strIITMLbackEndURL = globalIITMLbackEndURL.getPropertyValue();
                strIITMLbackEndURL = strIITMLbackEndURL.trim();

                GlobalProperty gpPwd = Context.getAdministrationService().getGlobalPropertyObject("scheduler.password");
                GlobalProperty gpUsername = Context.getAdministrationService().getGlobalPropertyObject("scheduler.username");
                String pwd = gpPwd.getPropertyValue();
                String username = gpUsername.getPropertyValue();

                // System.out.println("Got global IIT update score url part as: " + strIITMLbackEndURL);
                // final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().trim();
                // System.out.println("Got base url part as: " + baseUrl);
                if(!strIITMLbackEndURL.startsWith("http")) {
                    final String baseUrl = "http://127.0.0.1:8080/" + WebConstants.CONTEXT_PATH;
                    strIITMLbackEndURL = baseUrl + strIITMLbackEndURL;
                }
                // System.out.println("Got full IIT update backend url as: " + strIITMLbackEndURL);

                //Call endpoint to get the score
            
                URL url = new URL(strIITMLbackEndURL);
                // System.out.println("Calling IIT update backend using: " + url);

                String auth = username.trim() + ":" + pwd.trim();
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes("UTF-8"));
                String authHeader = "Basic " + new String(encodedAuth);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty(HttpHeaders.AUTHORIZATION, authHeader);
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                byte[] output = payload.getBytes("utf-8");
                outputStream.write(output, 0, output.length);
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                String mlScoreResponse = response.toString();
                // System.out.println("ITT ML - Got IIT update Score JSON as: " + mlScoreResponse);
            }
            catch (Exception e) {
                System.err.println("ITT ML - Error getting IIT update Score: " + e.getMessage());
                e.printStackTrace();
            }
            finally {
                try {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Exception e) {}
                    }
                    connection.disconnect();
                } catch (Exception e) {}
            }
        }
    }

}