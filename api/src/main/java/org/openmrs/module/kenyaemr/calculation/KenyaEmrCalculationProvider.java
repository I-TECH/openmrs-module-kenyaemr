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

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.ConfigurableCalculation;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.module.kenyaemr.calculation.art.*;
import org.openmrs.module.kenyaemr.calculation.cd4.DecliningCD4Calculation;
import org.openmrs.module.kenyaemr.calculation.cd4.LastCD4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.cd4.LastCD4PercentageCalculation;
import org.openmrs.module.kenyaemr.calculation.cd4.NeedsCD4Calculation;
import org.springframework.stereotype.Component;

/**
 * Provides new instances of calculations in this module
 */
@Component("org.openmrs.module.kenyaemr.calculation.KenyaEmrCalculationProvider")
public class KenyaEmrCalculationProvider implements CalculationProvider {
	
	private final Log log = LogFactory.getLog(getClass());
	
	private Map<String, Class<? extends BaseKenyaEmrCalculation>> map = new HashMap<String, Class<? extends BaseKenyaEmrCalculation>>();
	
	public KenyaEmrCalculationProvider() {

		// General
		map.put("inTBProgram", InTBProgramCalculation.class);
		map.put("lastWHOStage", LastWHOStageCalculation.class);
		map.put("lostToFollowUp", LostToFollowUpCalculation.class);
        map.put("missedAppointmentsOrDefaulted", MissedAppointmentsOrDefaultedCalculation.class);
		map.put("neverScreenedForTB", NeverScreenedForTBCalculation.class);

		// ART
		map.put("currentArtRegimen", CurrentArtRegimenCalculation.class);
		map.put("eligibleForArt", EligibleForArtCalculation.class);
		map.put("initialArtRegimen", InitialArtRegimenCalculation.class);
		map.put("initialArtStartDate", InitialArtStartDateCalculation.class);
		map.put("onArt", OnArtCalculation.class);
		map.put("onSecondLineArt", OnSecondLineArtCalculation.class);

		// CD4
		map.put("decliningCD4", DecliningCD4Calculation.class);
		map.put("needsCD4", NeedsCD4Calculation.class);
		map.put("lastCD4Count", LastCD4CountCalculation.class);
		map.put("lastCD4Percent", LastCD4PercentageCalculation.class);
	}

	/**
	 * Gets new instances of all calculations in this module
	 * @return list of calculation instances
	 */
	public List<BaseKenyaEmrCalculation> getAllCalculations() {
		return getCalculations(null);
	}

	/**
	 * Gets new instances of all calculations in this module with the given tag
	 * @return list of calculation instances
	 */
	public List<BaseKenyaEmrCalculation> getCalculations(String tag) {
		List<BaseKenyaEmrCalculation> ret = new ArrayList<BaseKenyaEmrCalculation>();
		for (String calcName : map.keySet()) {
			try {
				BaseKenyaEmrCalculation calc = (BaseKenyaEmrCalculation) getCalculation(calcName, null);

				// Check against the calculations tags if a tag was specified
				if (tag == null || (calc.getTags().length > 0 && Arrays.asList(calc.getTags()).contains(tag))) {
					ret.add(calc);
				}
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
			throw new InvalidCalculationException("Not Found: " + calculationName + " (valid values are: " + map.keySet() + ")");
		try {
			BaseKenyaEmrCalculation calc = clazz.newInstance();

			if (configuration != null && clazz.isAssignableFrom(ConfigurableCalculation.class)) {
				((ConfigurableCalculation) calc).setConfiguration(configuration);
			}

			return calc;
        }
        catch (Exception ex) {
	        throw new InvalidCalculationException("Failed to instantiate " + clazz, ex);
        }
	}
}