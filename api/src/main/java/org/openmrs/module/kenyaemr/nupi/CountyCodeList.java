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

import java.util.HashMap;

public class CountyCodeList {
    private HashMap<String, String> countyCodes = new HashMap<String, String>();

    public CountyCodeList() {
        //init list
        countyCodes.put("Nairobi", "047");
        countyCodes.put("Mombasa", "001");
        countyCodes.put("Kwale", "002");
        countyCodes.put("Kilifi", "003");
        countyCodes.put("Tana River", "004");
        countyCodes.put("Lamu", "005");
        countyCodes.put("Taita Taveta", "006");
        countyCodes.put("Garissa", "007");
        countyCodes.put("Wajir", "008");
        countyCodes.put("Mandera", "009");
        countyCodes.put("Marsabit", "010");
        countyCodes.put("Isiolo", "011");
        countyCodes.put("Meru", "012");
        countyCodes.put("Tharaka Nithi", "013");
        countyCodes.put("Embu", "014");
        countyCodes.put("Kitui", "015");
        countyCodes.put("Machakos", "016");
        countyCodes.put("Makueni", "017");
        countyCodes.put("Nyandarua", "018");
        countyCodes.put("Nyeri", "019");
        countyCodes.put("Kirinyaga", "020");
        countyCodes.put("Muranga", "021");
        countyCodes.put("Kiambu", "022");
        countyCodes.put("Turkana", "023");
        countyCodes.put("West Pokot", "024");
        countyCodes.put("Samburu", "025");
        countyCodes.put("Trans Nzoia", "026");
        countyCodes.put("Uasin Gishu", "027");
        countyCodes.put("Elgeyo Marakwet", "028");
        countyCodes.put("Nandi", "029");
        countyCodes.put("Baringo", "030");
        countyCodes.put("Laikipia", "031");
        countyCodes.put("Nakuru", "032");
        countyCodes.put("Narok", "033");
        countyCodes.put("Kajiado", "034");
        countyCodes.put("Kericho", "035");
        countyCodes.put("Bomet", "036");
        countyCodes.put("Kakamega", "037");
        countyCodes.put("Vihiga", "038");
        countyCodes.put("Bungoma", "039");
        countyCodes.put("Busia", "040");
        countyCodes.put("Siaya", "041");
        countyCodes.put("Kisumu", "042");
        countyCodes.put("Homa Bay", "043");
        countyCodes.put("Migori", "044");
        countyCodes.put("Kisii", "045");
        countyCodes.put("Nyamira", "046");
    }

    public String getCountyCode(String key) {
        String ret = "047";
        System.out.println("Getting county code for: " + key);
        if(countyCodes != null && countyCodes.size() > 0) {
            // Case insensitive search
            for (HashMap.Entry<String, String> set : countyCodes.entrySet()) {
                String setkey = set.getKey();
                if(setkey != null) {
                    if(setkey.trim().toLowerCase().equalsIgnoreCase(key)) {
                        ret = set.getValue();
                        System.out.println("Got county code as: " + ret);
                        break;
                    }
                }
            }
        }
        return(ret);
    }
}
