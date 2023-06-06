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
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
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
     * Reads a json file with weight for length values
     * The file is a json object with length (to 1 decimal point) as the object keys. This is to facilitate O(1) searching
     * @param fileName
     * @return
     */
    public static ObjectNode loadWeightForLengthZScoreFile(String fileName) {
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

    /**
     * Calculates weight for lenght z-score based on a client's weight, height, and sex
     * NOTE: This only works for children under 5 yrs
     * Based on a client's height in cm, the precomputed weights and standard deviations are obtained.
     * The client's weight is then mapped to classify the patient. The result is the weight in the matrix which is the immediate <= the client weight
     * @param height
     * @param weight
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

        if (height < standardMinHeight || height > standardMaxHeight) { // check the extremes. TODO: we should validate this
            return -4;
        }

        ObjectNode sdList = null;
        ObjectNode sdForHeight = null;
        // explicitly load SDs based on sex
        if (sex.equals("M")) {
            sdList = loadWeightForLengthZScoreFile(WEIGHT_FOR_LENGTH_BOYS_FILE_OPTIMIZED);
        } else if (sex.equals("F")) {
            sdList = loadWeightForLengthZScoreFile(WEIGHT_FOR_LENGTH_GIRLS_FILE_OPTIMIZED);
        }

        if (sdList == null) {
            return ZSCORE_NOT_FOUND_DEFAULT_VALUE;// return meaningful error code and description
        }

        String lengthKey = String.valueOf(height);
        sdForHeight = (ObjectNode) sdList.get(lengthKey);

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
            // we want to compare with up to one decimal point.
            // We noted inconsistent results with more than one decimal point. This is also an alignment to the provided paper chart used at site
            double weightToCompareWith = Math.floor(previousKey * 100) / 100;

            if (weightToCompareWith <= weight) {
                if (zScoreValues.containsKey(sdValues.get(previousKey))) {
                    return zScoreValues.get(sdValues.get(previousKey));
                }
            } else {
                continue;
            }
        }
        return ZSCORE_NOT_FOUND_DEFAULT_VALUE;
    }
}
