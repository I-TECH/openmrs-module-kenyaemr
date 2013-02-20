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
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.ValidatingCommandObject;
import org.openmrs.module.kenyaemr.regimen.*;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Various actions for regimen related functions
 */
public class RegimenUtilFragmentController {

	protected static final Log log = LogFactory.getLog(RegimenUtilFragmentController.class);

	/**
	 * Gets the patient's complete regimen history
	 * @param patient the patient
	 * @return a list of object with { startDate, endDate, shortDisplay, longDisplay, changeReasons[] }
	 */
	public List<SimpleObject> regimenHistory(@RequestParam("patientId") Patient patient, @RequestParam("category") String category, UiUtils ui) {
		Concept masterSet = RegimenManager.getMasterSetConcept(category);
		RegimenOrderHistory history = RegimenOrderHistory.forPatient(patient, masterSet);

		return KenyaEmrUiUtils.simpleRegimenHistory(history, ui);
	}

	/**
	 * Gets the patient's current regimen
	 * @param patient the patient
	 * @param ui the UI utils
	 * @return the regimen as a simple object
	 */
	public SimpleObject currentRegimen(@RequestParam("patientId") Patient patient, @RequestParam("category") String category, UiUtils ui) {
		Concept masterSet = RegimenManager.getMasterSetConcept(category);
		RegimenOrderHistory history = RegimenOrderHistory.forPatient(patient, masterSet);

		return KenyaEmrUiUtils.simpleRegimen(history.getCurrentRegimen(), ui);
	}

	/**
	 * Changes the patient's current regimen
	 * @param command the command object
	 * @param ui the UI utils
	 * @return the patient's current regimen
	 */
	public SimpleObject changeRegimen(@MethodParam("newRegimenChangeCommandObject") @BindParams RegimenChangeCommandObject command, UiUtils ui) {
		ui.validate(command, command, null);
		command.apply();
		
		return currentRegimen(command.getPatient(), command.getCategory(), ui);
	}

	/**
	 * Undoes the last regimen change for the given patient
	 * @param patient the patient
	 * @param category the regimen category
	 * @param ui the UI utils
	 * @return the patient's current regimen
	 */
	public SimpleObject undoLastChange(@RequestParam("patient") Patient patient, @RequestParam("category") String category, UiUtils ui) {
		Concept masterSet = RegimenManager.getMasterSetConcept(category);
		RegimenOrderHistory history = RegimenOrderHistory.forPatient(patient, masterSet);
		history.undoLastChange();

		return currentRegimen(patient, category, ui);
	}

	/**
	 * Helper method to create a new form object
	 * @return the form object
	 */
	public RegimenChangeCommandObject newRegimenChangeCommandObject() {
		return new RegimenChangeCommandObject();
	}

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

		private String changeType;
		
		private Date changeDate;
		
		private String changeReason;

		private Regimen regimen;

		private RegimenChangeType type;

		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			require(errors, "patient");
			require(errors, "category");
			require(errors, "changeDate");

			log.warn("Type: " + type);

			// Reason is only required for stopping or changing
			if (changeType.equals("stop") || changeType.equals("change")) {
				require(errors, "changeReason");
			}

			if (category != null && changeDate != null) {
				// Get patient regimen history
				Concept masterSet = RegimenManager.getMasterSetConcept(category);
				RegimenOrderHistory history = RegimenOrderHistory.forPatient(patient, masterSet);
				RegimenChange lastChange = history.getLastChange();
				boolean onRegimen = lastChange != null && lastChange.getStarted() != null;

				// Can't start if already started
				if ((changeType.equals("start") || changeType.equals("restart")) && onRegimen) {
					errors.reject("Can't start regimen for patient who is already on a regimen");
				}

				// Changes must be in order
				if (lastChange != null && OpenmrsUtil.compare(changeDate, lastChange.getDate()) <= 0) {
					errors.rejectValue("changeDate", "Change date must be after all other changes");
				}
			}

			// Validate the regimen
			if (!changeType.equals("stop")) {
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
			Concept masterSet = RegimenManager.getMasterSetConcept(category);
			RegimenOrderHistory history = RegimenOrderHistory.forPatient(patient, masterSet);
			RegimenOrder baseline = history.getRegimenOnDate(changeDate);

			if (baseline == null) {
				for (RegimenComponent component : regimen.getComponents()) {
					Concept concept = Context.getConceptService().getConcept(component.getConceptId());
					DrugOrder o = newDrugOrder(patient, changeDate, concept, component.getDose(), component.getUnits(), component.getFrequency());
					Context.getOrderService().saveOrder(o);
				}
			}
			else {
				List<DrugOrder> noChanges = new ArrayList<DrugOrder>();
				List<DrugOrder> toChangeDose = new ArrayList<DrugOrder>();
				List<DrugOrder> toStart = new ArrayList<DrugOrder>();

				if (regimen != null) {
					for (RegimenComponent component : regimen.getComponents()) {
						Concept concept = Context.getConceptService().getConcept(component.getConceptId());
						changeRegimenHelper(baseline, noChanges, toChangeDose, toStart, concept, component.getDose(), component.getUnits(), component.getFrequency());
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
		 * @return the patient
		 */
		public Patient getPatient() {
			return patient;
		}
		
		/**
		 * @param patient the patient to set
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
		 * @param category the regimen category
		 */
		public void setCategory(String category) {
			this.category = category;
		}

		/**
		 * Gets the change type
		 * @return the change type
		 */
		public String getChangeType() {
			return changeType;
		}

		/**
		 * Sets the change type
		 * @param changeType the change type
		 */
		public void setChangeType(String changeType) {
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

		public RegimenChangeType getType() {
			return type;
		}

		public void setType(RegimenChangeType type) {
			this.type = type;
		}
	}

	/**
	 *
	 * @param baseline
	 * @param noChanges
	 * @param toChangeDose
	 * @param toStart
	 * @param generic
	 * @param dosage
	 * @param units
	 * @param frequency
	 */
	private void changeRegimenHelper(RegimenOrder baseline, List<DrugOrder> noChanges, List<DrugOrder> toChangeDose,
									 List<DrugOrder> toStart, Concept generic, Double dosage, String units,
									 String frequency) {
		List<DrugOrder> sameGeneric = baseline.getDrugOrders(generic);
		boolean anyDoseChanges = false;
		for (DrugOrder o : sameGeneric) {
			if (o.getDose().equals(dosage) && o.getUnits().equals(units) && OpenmrsUtil.nullSafeEquals(o.getFrequency(), frequency)) {
				noChanges.add(o);
			} else {
				toChangeDose.add(o);
				anyDoseChanges = true;
			}
		}
		if (anyDoseChanges || sameGeneric.size() == 0) {
			DrugOrder newOrder = new DrugOrder();
			newOrder.setConcept(generic);
			newOrder.setDose(dosage);
			newOrder.setUnits(units);
			newOrder.setFrequency(frequency);
			toStart.add(newOrder);
		}
	}

	/**
	 * Utility method to create a new drug order
	 * @param patient
	 * @param startDate
	 * @param generic
	 * @param dosage
	 * @param doseUnit
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static DrugOrder newDrugOrder(Patient patient, Date startDate, Concept generic, Double dosage,
										  String doseUnit, String frequency) {
		DrugOrder ret = new DrugOrder();
		ret.setOrderType(Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG));
		ret.setPatient(patient);
		ret.setStartDate(startDate);
		ret.setConcept(generic);
		ret.setDose(dosage);
		ret.setUnits(doseUnit);
		ret.setFrequency(frequency);
		return ret;
	}
}