/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.calculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.module.kenyaemr.calculation.art.EligibleForArtCalculation;
import org.springframework.stereotype.Component;


/**
 *
 */
@Component("org.openmrs.module.kenyaemr.calculation.KenyaEmrCalculationProvider")
public class KenyaEmrCalculationProvider implements CalculationProvider {
	
	private final Log log = LogFactory.getLog(getClass());
	
	Map<String, Class<? extends BaseKenyaEmrCalculation>> map = new HashMap<String, Class<? extends BaseKenyaEmrCalculation>>();
	
	public KenyaEmrCalculationProvider() {
		map.put("needsCd4", NeedsCD4Calculation.class);
		map.put("eligibleForArt", EligibleForArtCalculation.class);
		map.put("decliningCd4", DecliningCD4Calculation.class);
        map.put("missedAppointmentsOrDefaulted", MissedAppointmentsOrDefaultedCalculation.class);
		map.put("lostToFollowUp", LostToFollowUpCalculation.class);
		// TODO add others (onArt, scheduledVisitOnDay), but make sure they don't run on the patient page by default.
	}
	
	public List<BaseKenyaEmrCalculation> getAllCalculations() {
		List<BaseKenyaEmrCalculation> ret = new ArrayList<BaseKenyaEmrCalculation>();
		for (String calcName : map.keySet()) {
			try {
	            ret.add((BaseKenyaEmrCalculation) getCalculation(calcName, null));
            }
            catch (InvalidCalculationException ex) {
	            log.warn("Invalid calculation defined", ex);
            }
		}
		return ret;
	}
	
	/**
	 * @see org.openmrs.calculation.CalculationProvider#getCalculation(java.lang.String, java.lang.String)
	 */
	@Override
	public Calculation getCalculation(String calculationName, String configuration) throws InvalidCalculationException {
		Class<? extends BaseKenyaEmrCalculation> clazz = map.get(calculationName);
		if (clazz == null)
			throw new InvalidCalculationException("Not Found: " + clazz + " (valid values are: " + map.keySet() + ")");
		try {
	        return clazz.newInstance();
        }
        catch (Exception ex) {
	        throw new InvalidCalculationException("Failed to instantiate " + clazz, ex);
        }
	}
	
}
