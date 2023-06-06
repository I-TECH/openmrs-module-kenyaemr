/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ZScoreUtil {

    public static String WEIGHT_FOR_LENGTH_BOYS_FILE = "zscore/wfl_boys_below5.json";
    public static String WEIGHT_FOR_LENGTH_BOYS_FILE_OPTIMIZED = "zscore/wfl_boys_below5_object.json";
    public static String WEIGHT_FOR_LENGTH_GIRLS_FILE = "zscore/wfl_girls_below5.json";
    public static String WEIGHT_FOR_LENGTH_GIRLS_FILE_OPTIMIZED = "zscore/wfl_girls_below5_object.json";
    public static Integer ZSCORE_NOT_FOUND_DEFAULT_VALUE = -10; // arbitrarily picked number. can be refined to something meaningful
    public static Integer ZSCORE_INSUFFICIENT_INFORMATION_DEFAULT_VALUE = -20; // arbitrarily picked number. can be refined to something meaningful

    /**
     * Generates a client's z-score from height and sex
     * @param height
     * @param sex
     * @return
     */
    public static Integer calculateZScore(Double height, Double weight, String sex) {

        String sD4negKey =  "SD4neg";
        String sD3negKey =  "SD3neg";
        String sD2negKey =  "SD2neg";
        String sD1negKey =  "SD1neg";
        String sD0Key =  "SD0";
        String sD1Key =  "SD1";
        String sD2Key =  "SD2";
        String sD3Key =  "SD3";
        String sD4Key =  "SD4";

        Map<Double,String> sdValues = new TreeMap<Double,String>(Collections.reverseOrder());

       // map sdKeys to values. We can check if this is a better approach than if statements
        Map<String, Integer> zScoreValues = new HashMap<String, Integer>();
        zScoreValues.put(sD4negKey, -4);
        zScoreValues.put(sD3negKey, -3);
        zScoreValues.put(sD2negKey, -2);
        zScoreValues.put(sD1negKey, -1);
        zScoreValues.put(sD0Key, 0);
        zScoreValues.put(sD1Key, 1);
        zScoreValues.put(sD2Key, 2);
        zScoreValues.put(sD2Key, 3);
        zScoreValues.put(sD2Key, 4);

        if (height == null || height == 0 || sex == null || sex == "") {
            return ZSCORE_INSUFFICIENT_INFORMATION_DEFAULT_VALUE;
        }
        int standardMinHeight = 45; // in cm
        int standardMaxHeight = 110; // in cm

        if (height < standardMinHeight || height > standardMaxHeight) { // check the extremes
            return -4;
        }

        ArrayNode sdList = null;
        ObjectNode sdForHeight = null;
        // explicitly load SDs based on sex
        if (sex.equals("M")) {
            sdList = readZScoreFile(WEIGHT_FOR_LENGTH_BOYS_FILE);
        } else if (sex.equals("F")) {
            sdList = readZScoreFile(WEIGHT_FOR_LENGTH_GIRLS_FILE);
        }

        if (sdList == null) {
            return ZSCORE_NOT_FOUND_DEFAULT_VALUE;// return meaningful error code and description
        }

        for (int i = 0; i < sdList.size(); i++) {
            ObjectNode objNode = (ObjectNode) sdList.get(i);
            if (objNode.get("Length").asDouble() == height) {
                sdForHeight = objNode;
                break;
            }
        }

        if (sdForHeight != null) {
            sdValues.put(sdForHeight.get(sD4negKey).asDouble(),sD4negKey);
            sdValues.put(sdForHeight.get(sD3negKey).asDouble(),sD3negKey);
            sdValues.put(sdForHeight.get(sD2negKey).asDouble(),sD2negKey);
            sdValues.put(sdForHeight.get(sD1negKey).asDouble(),sD1negKey);
            sdValues.put(sdForHeight.get(sD0Key).asDouble(),sD0Key);
            sdValues.put(sdForHeight.get(sD1Key).asDouble(),sD1Key);
            sdValues.put(sdForHeight.get(sD2Key).asDouble(),sD2Key);
            sdValues.put(sdForHeight.get(sD3Key).asDouble(),sD3Key);
            sdValues.put(sdForHeight.get(sD4Key).asDouble(),sD4Key);
        }


        double previousKey = 0;
        for (Map.Entry<Double,String> entry : sdValues.entrySet()) {
            if (weight <= entry.getKey()) {
                System.out.println("Key: " + entry.getKey() + ", val: " + entry.getValue());
                previousKey = entry.getKey();
                continue;
            } else {
                String sdValStringRepresentation = sdValues.get(previousKey);
                System.out.println("Got the SD: " + previousKey + ", and value of " + sdValues.get(previousKey));
                if (zScoreValues.containsKey(sdValStringRepresentation)) {
                    return zScoreValues.get(sdValStringRepresentation);
                }
            }
        }
        return ZSCORE_NOT_FOUND_DEFAULT_VALUE;
    }

    public static ArrayNode readZScoreFile(String fileName) {
        InputStream stream = ZScoreUtil.class.getClassLoader().getResourceAsStream(fileName);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode result = mapper.readValue(stream, ArrayNode.class);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObjectNode readZScoreFileObject(String fileName) {
        InputStream stream = ZScoreUtil.class.getClassLoader().getResourceAsStream(fileName);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode result = mapper.readValue(stream, ObjectNode.class);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer calculateZScoreOptimized(Double height, Double weight, String sex) {
        String sD4negKey =  "SD4neg";
        String sD3negKey =  "SD3neg";
        String sD2negKey =  "SD2neg";
        String sD1negKey =  "SD1neg";
        String sD0Key =  "SD0";
        String sD1Key =  "SD1";
        String sD2Key =  "SD2";
        String sD3Key =  "SD3";
        String sD4Key =  "SD4";

        Map<Double,String> sdValues = new TreeMap<Double,String>(Collections.reverseOrder());

        // map sdKeys to values. We can check if this is a better approach than if statements
        Map<String, Integer> zScoreValues = new HashMap<String, Integer>();
        zScoreValues.put(sD4negKey, -4);
        zScoreValues.put(sD3negKey, -3);
        zScoreValues.put(sD2negKey, -2);
        zScoreValues.put(sD1negKey, -1);
        zScoreValues.put(sD0Key, 0);
        zScoreValues.put(sD1Key, 1);
        zScoreValues.put(sD2Key, 2);
        zScoreValues.put(sD2Key, 3);
        zScoreValues.put(sD2Key, 4);

        if (height == null || height == 0 || sex == null || sex == "") {
            return ZSCORE_INSUFFICIENT_INFORMATION_DEFAULT_VALUE;
        }
        int standardMinHeight = 45; // in cm
        int standardMaxHeight = 110; // in cm

        if (height < standardMinHeight || height > standardMaxHeight) { // check the extremes
            return -4;
        }

        ObjectNode sdList = null;
        ObjectNode sdForHeight = null;
        // explicitly load SDs based on sex
        if (sex.equals("M")) {
            sdList = readZScoreFileObject(WEIGHT_FOR_LENGTH_BOYS_FILE_OPTIMIZED);
        } else if (sex.equals("F")) {
            sdList = readZScoreFileObject(WEIGHT_FOR_LENGTH_GIRLS_FILE_OPTIMIZED);
        }

        if (sdList == null) {
            return ZSCORE_NOT_FOUND_DEFAULT_VALUE;// return meaningful error code and description
        }

        String lengthKey = String.valueOf(height);
        sdForHeight = (ObjectNode) sdList.get(lengthKey);

/*        for (int i = 0; i < sdList.size(); i++) {
            ObjectNode objNode = (ObjectNode) sdList.get(i);
            if (objNode.get("Length").asDouble() == height) {
                sdForHeight = objNode;
                break;
            }
        }*/

        if (sdForHeight != null) {
            sdValues.put(sdForHeight.get(sD4negKey).asDouble(),sD4negKey);
            sdValues.put(sdForHeight.get(sD3negKey).asDouble(),sD3negKey);
            sdValues.put(sdForHeight.get(sD2negKey).asDouble(),sD2negKey);
            sdValues.put(sdForHeight.get(sD1negKey).asDouble(),sD1negKey);
            sdValues.put(sdForHeight.get(sD0Key).asDouble(),sD0Key);
            sdValues.put(sdForHeight.get(sD1Key).asDouble(),sD1Key);
            sdValues.put(sdForHeight.get(sD2Key).asDouble(),sD2Key);
            sdValues.put(sdForHeight.get(sD3Key).asDouble(),sD3Key);
            sdValues.put(sdForHeight.get(sD4Key).asDouble(),sD4Key);
        }


        double previousKey = 0;
        for (Map.Entry<Double,String> entry : sdValues.entrySet()) {
            previousKey = entry.getKey();
            DecimalFormat df = new DecimalFormat("#.#");
            double formatedVal = Double.parseDouble(df.format(entry.getKey()));
            if (formatedVal <= weight) {
                String sdValStringRepresentation = sdValues.get(previousKey);
                System.out.println("Got the SD: " + previousKey + ", and value of " + sdValues.get(previousKey));
                if (zScoreValues.containsKey(sdValStringRepresentation)) {
                    return zScoreValues.get(sdValStringRepresentation);
                }
            } else {
                System.out.println("Key: " + entry.getKey() + ", val: " + entry.getValue());
                continue;
            }
        }
        return ZSCORE_NOT_FOUND_DEFAULT_VALUE;
    }
}
