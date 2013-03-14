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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

/**
 * Manager for calculations used by the Kenya EMR
 */
@Component
public class CalculationManager implements CalculationProvider {
	
	protected static final Log log = LogFactory.getLog(CalculationManager.class);
	
	private Map<String, Class<? extends BaseEmrCalculation>> calculationClasses = new HashMap<String, Class<? extends BaseEmrCalculation>>();

	/**
	 * Refreshes registered calculation classes
	 */
	public synchronized void refreshCalculationClasses() {
		clear();

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(BaseEmrCalculation.class));

		for (BeanDefinition bd : scanner.findCandidateComponents(CalculationManager.class.getPackage().getName())) {
			try {
				Class clazz = Class.forName(bd.getBeanClassName());
				calculationClasses.put(bd.getBeanClassName(), clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			log.info("Found calculation class :" + bd.getBeanClassName());
		}
	}

	/**
	 * Clears all registered calculation classes
	 */
	public synchronized void clear() {
		calculationClasses.clear();
	}

	/**
	 * Gets new instances of all alert calculations in this module
	 * @return list of alert calculation instances
	 */
	public List<BaseAlertCalculation> getAlertCalculations() {
		List<BaseAlertCalculation> ret = new ArrayList<BaseAlertCalculation>();

		for (Class<? extends BaseEmrCalculation> calculationClass : calculationClasses.values()) {
			if (BaseAlertCalculation.class.isAssignableFrom(calculationClass)) {
				ret.add((BaseAlertCalculation)instantiateCalculation(calculationClass, null));
			}
		}

		return ret;
	}

	/**
	 * Gets new instances of all calculations in this module with the given tag
	 * @return list of calculation instances
	 */
	public List<BaseEmrCalculation> getCalculations(String tag) {
		List<BaseEmrCalculation> ret = new ArrayList<BaseEmrCalculation>();
		for (String calcName : calculationClasses.keySet()) {
			try {
				BaseEmrCalculation calc = (BaseEmrCalculation) getCalculation(calcName, null);

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
		Class<? extends BaseEmrCalculation> clazz = calculationClasses.get(calculationName);
		if (clazz == null)
			throw new InvalidCalculationException("Not Found: " + calculationName + " (valid values are: " + calculationClasses.keySet() + ")");

		return instantiateCalculation(clazz, configuration);
	}

	/**
	 * Instantiates and configures a calculation
	 * @param clazz the calculation class
	 * @param configuration the configuration
	 * @return the calculation instance
	 */
	public static BaseEmrCalculation instantiateCalculation(Class<? extends BaseEmrCalculation> clazz, String configuration) {
		try {
			BaseEmrCalculation calc = clazz.newInstance();

			if (configuration != null && clazz.isAssignableFrom(ConfigurableCalculation.class)) {
				((ConfigurableCalculation) calc).setConfiguration(configuration);
			}

			return calc;
		}
		catch (Exception ex) {
			return null;
		}
	}
}