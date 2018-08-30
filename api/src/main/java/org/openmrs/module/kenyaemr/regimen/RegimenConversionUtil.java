package org.openmrs.module.kenyaemr.regimen;

import java.util.HashMap;
import java.util.Map;

public class RegimenConversionUtil {
    /**
     *
     mg=161553
     g=161554
     ml=162263
     tab=1513
     OD=160862
     NOCTE=160863
     qPM=160864
     qAM=160865
     BD=160858
     TDS=160866
     null=162135
     */

    public static Integer getConceptIdFromDoseUnitString(String frequencyString) {
        Map<String, Integer> collection = new HashMap<String, Integer>();
        collection.put("mg", 161553);
        collection.put("g", 161554);
        collection.put("ml", 162263);
        collection.put("tab", 1513);
        return collection.get(frequencyString);

    }

    public static String getDoseUnitStringFromConceptId(Integer frequencyConceptId) {
        Map<Integer, String> collection = new HashMap<Integer, String>();
        collection.put(161553, "mg");
        collection.put(161554, "g");
        collection.put(162263, "ml");
        collection.put(1513, "tab");
        return collection.get(frequencyConceptId);

    }

    public static Integer getConceptIdFromFrequencyString(String frequencyString) {
        Map<String, Integer> collection = new HashMap<String, Integer>();
        collection.put("OD", 160862);
        collection.put("NOCTE", 160863);
        collection.put("qPM", 160864);
        collection.put("qAM", 160865);
        collection.put("BD", 160858);
        collection.put("TDS", 160866);
        return collection.get(frequencyString);

    }

    public static String getFrequencyStringFromConceptId(Integer frequencyConceptId) {
        Map<Integer, String> collection = new HashMap<Integer, String>();
        collection.put(160862, "OD");
        collection.put(160863, "NOCTE");
        collection.put(160864, "qPM");
        collection.put(160865, "qAM");
        collection.put(160858, "BD");
        collection.put(160866, "TDS");
        return collection.get(frequencyConceptId);

    }
}
