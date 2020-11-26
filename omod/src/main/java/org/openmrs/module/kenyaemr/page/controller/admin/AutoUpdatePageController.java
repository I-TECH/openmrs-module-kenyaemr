/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.admin;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.util.ServerInformation;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Manage auto- update page for the admin app
 */
@AppPage(EmrConstants.APP_ADMIN)
public class AutoUpdatePageController {
    public static final String AUTO_UPDATE_RELEASE_URL = "kenyaemr.autoUpdateReleaseUrl";
    private static String latestReleaseUrl = Context.getAdministrationService().getGlobalProperty(AUTO_UPDATE_RELEASE_URL);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Log log = LogFactory.getLog(AutoUpdatePageController.class);
    ObjectNode jsonNode = null;
    ObjectMapper mapper = new ObjectMapper();


    public void controller(PageModel model) {
        String downloadUrl = null;
        String releaseDate = null;
        String releaseVersion = null;
        String releaseNotes = "";
        int latestUploadedVersion = 0;
        int currVersion = 0;
        boolean updatesAvailable = false;
        boolean isOnline = false;
        String currentVersion = ServerInformation.getKenyaemrInformation().get("version").toString();
        model.addAttribute("version", ServerInformation.getKenyaemrInformation().get("version"));

        if(currentVersion.contains("-SNAPSHOT")) {
            currVersion = Integer.parseInt( currentVersion.replaceAll("-SNAPSHOT","").replace(".","").trim());
        } else {
            currVersion = Integer.parseInt(currentVersion.replace(".","").trim());

        }

        if (checkInternetConnectionStatus()) {
            isOnline = true;
            try {
                jsonNode = (ObjectNode) mapper.readTree(getLatestRelease());
                if (jsonNode != null) {
                    releaseVersion = jsonNode.get("name").getTextValue().replaceAll("KenyaEMR","").trim();
                    latestUploadedVersion = Integer.parseInt(jsonNode.get("name").getTextValue().replaceAll("KenyaEMR","").replace(".","").trim());
                    downloadUrl = jsonNode.get("assets").get(0).get("browser_download_url").getTextValue();
                    releaseDate = DATE_FORMAT.format( parseDate(jsonNode.get("assets").get(0).get("created_at").getTextValue()));
                    releaseNotes = jsonNode.get("body") != null ? jsonNode.get("body").getTextValue().replace("\n","<br />"): "";

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (latestUploadedVersion != 0 && latestUploadedVersion > currVersion) {
            updatesAvailable = true;
        }

        model.addAttribute("url", downloadUrl);
        model.addAttribute("releaseDate", releaseDate);
        model.addAttribute("releaseVersion", releaseVersion);
        model.addAttribute("updatesAvailable", updatesAvailable);
        model.addAttribute("isOnline", isOnline);
        model.addAttribute("releaseNotes", releaseNotes);


    }


    private static String getLatestRelease() throws IOException {
        URL obj = new URL(latestReleaseUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        StringBuffer response = new StringBuffer();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } else {
            System.out.println("GET request not worked");
        }
        return response.toString();

    }

    private boolean checkInternetConnectionStatus() {
        boolean isConnected = false;

        try {
            URL url = new URL("https://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            isConnected = true;
        } catch (MalformedURLException e) {
            log.info("Internet is not connected");
        } catch (IOException e) {
            log.error("Internet is not connected");
        }

        return isConnected;
    }

    private Date parseDate(final String dateValue) {
        Date date = null;
        try {
            date = dateFormat.parse(dateValue);
        } catch (ParseException e) {

            log.error("Unable to parse date data from the payload!", e);
        }
        return date;
    }





}