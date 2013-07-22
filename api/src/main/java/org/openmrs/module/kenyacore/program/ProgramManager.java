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

package org.openmrs.module.kenyacore.program;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.module.kenyacore.ContentManager;
import org.openmrs.module.kenyacore.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Program manager
 */
@Component
public class ProgramManager implements ContentManager {

	private Map<String, ProgramDescriptor> programs = new LinkedHashMap<String, ProgramDescriptor>();

	/**
	 * @see org.openmrs.module.kenyacore.ContentManager#refresh()
	 */
	@Override
	public synchronized void refresh() {
		programs.clear();

		List<ProgramDescriptor> descriptors = Context.getRegisteredComponents(ProgramDescriptor.class);

		// Sort by identifier descriptor order
		Collections.sort(descriptors);

		for (ProgramDescriptor descriptor : descriptors) {
			programs.put(descriptor.getTargetUuid(), descriptor);
		}
	}

	/**
	 * Gets all of the programs
	 * @return the programs
	 */
	public Collection<ProgramDescriptor> getAllPrograms() {
		return programs.values();
	}

	/**
	 * Gets all programs for which the given patient is eligible
	 * @param patient the patient
	 * @return the programs
	 */
	public List<ProgramDescriptor> getPatientEligiblePrograms(Patient patient) {
		List<ProgramDescriptor> eligibleFor = new ArrayList<ProgramDescriptor>();

		for (ProgramDescriptor programDescriptor : programs.values()) {
			try {
				Class<? extends BaseEmrCalculation> clazz = (Class<? extends BaseEmrCalculation>) Context.loadClass(programDescriptor.getEligibilityCalculation());

				CalculationResult result = CalculationUtils.evaluateForPatient(clazz, null, patient);
				if (ResultUtil.isTrue(result)) {
					eligibleFor.add(programDescriptor);
				}
			}
			catch (ClassNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		}

		return eligibleFor;
	}
}