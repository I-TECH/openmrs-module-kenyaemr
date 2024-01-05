/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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