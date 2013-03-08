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

package org.openmrs.module.kenyaemr.fragment.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.ValidatingCommandObject;
import org.openmrs.module.kenyaemr.regimen.*;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * Various actions for regimen related functions
 */
public class RegimenUtilFragmentController {

	protected static final Log log = LogFactory.getLog(RegimenUtilFragmentController.class);

	/**
	 * Changes the patient's current regimen
	 * @param command the command object
	 * @param ui the UI utils
	 * @return the patient's current regimen
	 */
	public void changeRegimen(@MethodParam("newRegimenChangeCommandObject") @BindParams RegimenChangeCommandObject command, UiUtils ui) {
		ui.validate(command, command, null);
		command.apply();
	}

	/**
	 * Undoes the last regimen change for the given patient
	 * @param patient the patient
	 * @return the patient's current regimen
	 */
	public void undoLastChange(@RequestParam("patient") Patient patient, HttpSession session, @RequestParam("category") String category, @SpringBean KenyaEmr emr) {
		Concept masterSet = emr.getRegimenManager().getMasterSetConcept(category);
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, masterSet);
		history.undoLastChange();

		KenyaEmrUiUtils.notifySuccess(session, "Removed last regimen change");
	}

	/**
	 * Helper method to create a new form object
	 * @return the form object
	 */
	public RegimenChangeCommandObject newRegimenChangeCommandObject() {
		return new RegimenChangeCommandObject();
	}

	/**
	 * Change types
	 */
	public enum RegimenChangeType {
		START,
		CHANGE,
		STOP,
		RESTART
	}

	/**
	 * Command object for regimen changes
	 */
	public class RegimenChangeCommandObject extends ValidatingCommandObject {

		private Patient patient;

		private String category;

		private RegimenChangeType changeType;
		
		private Date changeDate;
		
		private String changeReason;

		private Regimen regimen;

		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			require(errors, "patient");
			require(errors, "category");
			require(errors, "changeType");
			require(errors, "changeDate");

			// Reason is only required for stopping or changing
			if (changeType == RegimenChangeType.STOP || changeType == RegimenChangeType.CHANGE) {
				require(errors, "changeReason");
			}

			if (category != null && changeDate != null) {
				// Get patient regimen history
				Concept masterSet = KenyaEmr.getInstance().getRegimenManager().getMasterSetConcept(category);
				RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, masterSet);
				RegimenChange lastChange = history.getLastChange();
				boolean onRegimen = lastChange != null && lastChange.getStarted() != null;

				// Can't start if already started
				if ((changeType == RegimenChangeType.START || changeType == RegimenChangeType.RESTART) && onRegimen) {
					errors.reject("Can't start regimen for patient who is already on a regimen");
				}

				// Changes must be in order
				if (lastChange != null && OpenmrsUtil.compare(changeDate, lastChange.getDate()) <= 0) {
					errors.rejectValue("changeDate", "Change date must be after all other changes");
				}

				// Only TB allows future dates
				if (!category.equals("TB") && OpenmrsUtil.compare(changeDate, OpenmrsUtil.firstSecondOfDay(new Date())) > 0) {
					errors.rejectValue("changeDate", "Change date can't be in the future");
				}
			}

			// Validate the regimen
			if (changeType != RegimenChangeType.STOP) {
				try {
					errors.pushNestedPath("regimen");
					ValidationUtils.invokeValidator(new RegimenValidator(), regimen, errors);
				} finally {
					errors.popNestedPath();
				}
			}
		}
		
		/**
		 * Applies this regimen change
		 */
		public void apply() {
			Concept masterSet = KenyaEmr.getInstance().getRegimenManager().getMasterSetConcept(category);
			RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, masterSet);
			RegimenChange lastChange = history.getLastChange();
			RegimenOrder baseline = lastChange != null ? lastChange.getStarted() : null;

			if (baseline == null) {
				for (RegimenComponent component : regimen.getComponents()) {
					Concept concept = component.getDrugRef().getConcept();
					DrugOrder o = component.toDrugOrder(patient, changeDate);
					Context.getOrderService().saveOrder(o);
				}
			}
			else {
				List<DrugOrder> noChanges = new ArrayList<DrugOrder>();
				List<DrugOrder> toChangeDose = new ArrayList<DrugOrder>();
				List<DrugOrder> toStart = new ArrayList<DrugOrder>();

				if (regimen != null) {
					for (RegimenComponent component : regimen.getComponents()) {
						changeRegimenHelper(baseline, component, noChanges, toChangeDose, toStart);
					}
				}

				List<DrugOrder> toStop = new ArrayList<DrugOrder>(baseline.getDrugOrders());
				// for now "toChangeDose" is handled the same as toStop
				// toStop.removeAll(toChangeDose);
				toStop.removeAll(noChanges);

				OrderService os = Context.getOrderService();

				for (DrugOrder o : toStop) {
					o.setDiscontinued(true);
					o.setDiscontinuedDate(changeDate);
					o.setDiscontinuedBy(Context.getAuthenticatedUser());
					o.setDiscontinuedReasonNonCoded(changeReason);
					os.saveOrder(o);
				}

				for (DrugOrder o : toStart) {
					o.setPatient(patient);
					o.setStartDate(changeDate);
					o.setOrderType(os.getOrderType(OpenmrsConstants.ORDERTYPE_DRUG));
					os.saveOrder(o);
				}
			}
		}
		
		/**
		 * Gets the patient
		 * @return the patient
		 */
		public Patient getPatient() {
			return patient;
		}
		
		/**
		 * Sets the patient
		 * @param patient the patient
		 */
		public void setPatient(Patient patient) {
			this.patient = patient;
		}

		/**
		 * Gets the regimen category
		 * @return the regimen category
		 */
		public String getCategory() {
			return category;
		}

		/**
		 * Sets the regimen category
		 * @param category the category
		 */
		public void setCategory(String category) {
			this.category = category;
		}

		/**
		 * Gets the change type
		 * @return the change type
		 */
		public RegimenChangeType getChangeType() {
			return changeType;
		}

		/**
		 * Sets the change type
		 * @param changeType the change type
		 */
		public void setChangeType(RegimenChangeType changeType) {
			this.changeType = changeType;
		}

		/**
		 * Gets the change date
		 * @return the change date
		 */
		public Date getChangeDate() {
			return changeDate;
		}
		
		/**
		 * Set the change date
		 * @param changeDate the change date
		 */
		public void setChangeDate(Date changeDate) {
			this.changeDate = changeDate;
		}
		
		/**
		 * Gets the change reason
		 * @return the change reason
		 */
		public String getChangeReason() {
			return changeReason;
		}
		
		/**
		 * Sets the change reason
		 * @param changeReason the change reason
		 */
		public void setChangeReason(String changeReason) {
			this.changeReason = changeReason;
		}

		/**
		 * Gets the regimen
		 * @return the regimen
		 */
		public Regimen getRegimen() {
			return regimen;
		}

		/**
		 * Sets the regimen
		 * @param regimen the regimen
		 */
		public void setRegimen(Regimen regimen) {
			this.regimen = regimen;
		}
	}

	/**
	 * Analyzes the current regimen order and the new regimen component to decide which orders must be changed
	 * @param baseline the current regimen order
	 * @param component the new regimen component
	 * @param noChanges
	 * @param toChangeDose
	 * @param toStart
	 */
	private void changeRegimenHelper(RegimenOrder baseline, RegimenComponent component, List<DrugOrder> noChanges, List<DrugOrder> toChangeDose,
									 List<DrugOrder> toStart) {

		List<DrugOrder> sameGeneric = baseline.getDrugOrders(component.getDrugRef());

		boolean anyDoseChanges = false;
		for (DrugOrder o : sameGeneric) {
			if (o.getDose().equals(component.getDose()) && o.getUnits().equals(component.getUnits()) && OpenmrsUtil.nullSafeEquals(o.getFrequency(), component.getFrequency())) {
				noChanges.add(o);
			} else {
				toChangeDose.add(o);
				anyDoseChanges = true;
			}
		}
		if (anyDoseChanges || sameGeneric.size() == 0) {
			toStart.add(component.toDrugOrder(null, null));
		}
	}
}