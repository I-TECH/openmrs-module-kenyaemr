package org.openmrs.module.kenyaemr.regimen;

import java.util.HashMap;
import java.util.Map;

public class DrugOrderToRegimenComponentMapper {

    public static Integer getFrequencyConcept(String conceptName) {
        return 1;
    }

    public static String getFrequencyString(Integer conceptId) {
        return frequencyMap().get(conceptId);
    }

    public static Integer getDosageUnitConcept(String unitName) {
        return 1;
    }

    public static String getDosageUnitString(Integer unitConceptId) {
        return dosageUnitMap().get(unitConceptId);
    }

    private static Map<Integer, String> frequencyMap() {
        Map<Integer, String> frequencies = new HashMap<Integer, String>();
        frequencies.put(160865, "qAm");// once daily, in the morning
        frequencies.put(160864, "qPm");// once daily, in the evening
        frequencies.put(160858, "BD");// twice daily
        frequencies.put(160863, "NOCTE"); //once a day, at bedtime
        frequencies.put(160862, "OD");// once daily
        frequencies.put(160866, "TDS");// thrice daily
        return frequencies;
    }

    private static Map<Integer, String> dosageUnitMap() {
        Map<Integer, String> units = new HashMap<Integer, String>();
        units.put(161554, "g");
        units.put(161553, "mg");
        units.put(1513, "tab");
        units.put(162263, "ml");
        return units;
    }
}