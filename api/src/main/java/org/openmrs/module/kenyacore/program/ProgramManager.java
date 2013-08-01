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
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.module.kenyacore.ContentManager;
import org.openmrs.module.kenyacore.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.form.FormConfiguration;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
			if (programs.containsKey(descriptor.getTargetUuid())) {
				throw new RuntimeException("Program " + descriptor.getTargetUuid() + " already registered");
			}

			programs.put(descriptor.getTargetUuid(), descriptor);
		}
	}

	/**
	 * Gets all program descriptors
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getAllProgramDescriptors() {
		return programs.values();
	}

	/**
	 * Gets the program descriptor for the given program
	 * @param program the program
	 * @return the program descriptor
	 */
	public ProgramDescriptor getProgramDescriptor(Program program) {
		return programs.get(program.getUuid());
	}

	/**
	 * Gets program descriptors for all programs which the given patient has ever been enrolled in
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientPrograms(Patient patient) {
		Collection<ProgramDescriptor> everIn = new LinkedHashSet<ProgramDescriptor>();

		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
			ProgramDescriptor descriptor = getProgramDescriptor(pp.getProgram());
			if (descriptor != null) {
				everIn.add(descriptor);
			}
		}

		return everIn;
	}

	/**
	 * Checks whether patient is eligible to enroll in the given program
	 * @param patient the patient
	 * @param program the program
	 * @return true if patient can enroll
	 */
	public boolean isPatientEligibleFor(Patient patient, Program program) {
		ProgramDescriptor descriptor = getProgramDescriptor(program);

		Class<? extends BaseEmrCalculation> clazz = descriptor.getEligibilityCalculation();

		CalculationResult result = CalculationUtils.evaluateForPatient(clazz, null, patient);
		return ResultUtil.isTrue(result);
	}

	/**
	 * Gets program descriptors for all programs which the given patient is eligible for
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientEligiblePrograms(Patient patient) {
		List<ProgramDescriptor> eligibleFor = new ArrayList<ProgramDescriptor>();

		for (ProgramDescriptor descriptor : programs.values()) {
			if (isPatientEligibleFor(patient, descriptor.getTarget())) {
				eligibleFor.add(descriptor);
			}
		}

		return eligibleFor;
	}

	/**
	 * Gets program descriptors for all programs which the given patient is currently enrolled in
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientActivePrograms(Patient patient) {
		return getPatientActivePrograms(patient, new Date());
	}

	/**
	 * Gets program descriptors for all programs which the given patient was enrolled in on given date
	 * @param patient the patient
	 * @param onDate the date
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientActivePrograms(Patient patient, Date onDate) {
		List<ProgramDescriptor> activeIn = new ArrayList<ProgramDescriptor>();

		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
			if (pp.getActive(onDate)) {
				ProgramDescriptor descriptor = getProgramDescriptor(pp.getProgram());
				if (descriptor != null) {
					activeIn.add(descriptor);
				}
			}
		}

		return activeIn;
	}

	/**
	 * Gets all enrollments for the given patient in the given program, in chronological order
	 * @param patient the patient
	 * @param program the program
	 * @return the enrollments
	 */
	public List<PatientProgram> getPatientEnrollments(Patient patient, Program program) {
		List<PatientProgram> enrollments = Context.getProgramWorkflowService().getPatientPrograms(patient, program, null, null, null, null, false);

		Collections.sort(enrollments, new Comparator<PatientProgram>() {
			@Override
			public int compare(PatientProgram pp1, PatientProgram pp2) {
				return OpenmrsUtil.compare(pp1.getDateEnrolled(), pp2.getDateEnrolled());
			}
		});

		return enrollments;
	}
}