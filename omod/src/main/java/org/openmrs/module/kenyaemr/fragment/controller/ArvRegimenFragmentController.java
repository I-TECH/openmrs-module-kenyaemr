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

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.ValidatingCommandObject;
import org.openmrs.module.kenyaemr.regimen.Regimen;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenHistory;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
public class ArvRegimenFragmentController {
	
	public SimpleObject currentRegimen(@RequestParam("patientId") Patient patient, UiUtils ui) {
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		RegimenHistory history = RegimenHistory.forPatient(patient, arvs);
		Regimen reg = history.getCurrentRegimen();
		return reg == null ? SimpleObject.create("shortDisplay", "None", "longDisplay", "None") : SimpleObject.create(
		    "shortDisplay", reg.getShortDisplay(ui), "longDisplay", reg.getLongDisplay(ui));
	}
	
	/**
	 * @param patient
	 * @return a list of object with { startDate, endDate, shortDisplay, longDisplay,
	 *         changeReasons[] }
	 */
	public List<SimpleObject> regimenHistory(@RequestParam("patientId") Patient patient, UiUtils ui) {
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		RegimenHistory history = RegimenHistory.forPatient(patient, arvs);
		return history.asSimpleRegimenHistory(ui);
	}
	
	public SimpleObject startRegimen(UiUtils ui, @RequestParam("patient") Patient patient,
	                                 @MethodParam("newArvRegimenCommandObject") @BindParams ArvRegimenCommandObject command) {
		
		ui.validate(command, command, null);
		
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		command.applyNewRegimen(arvs);
		
		return currentRegimen(patient, ui);
	}
	
	public SimpleObject changeRegimen(UiUtils ui, @RequestParam("patient") Patient patient,
	                                  @MethodParam("newArvRegimenCommandObject") @BindParams ArvRegimenCommandObject command) {
		
		ui.validate(command, command, null);
		
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		command.applyRegimenChange(arvs);
		
		return currentRegimen(patient, ui);
	}
	
	public SimpleObject stopRegimen(UiUtils ui, @RequestParam("patient") Patient patient,
	                                @RequestParam("stopDate") Date stopDate, @RequestParam("stopReason") String stopReason) {
		
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		
		ArvRegimenCommandObject command = new ArvRegimenCommandObject();
		command.setPatient(patient);
		command.setStartDate(stopDate);
		command.setChangeReason(stopReason);
		command.applyRegimenChange(arvs);
		
		return currentRegimen(patient, ui);
	}
	
	public ArvRegimenCommandObject newArvRegimenCommandObject() {
		return new ArvRegimenCommandObject();
	}
	
	public class ArvRegimenCommandObject extends ValidatingCommandObject {
		
		private Patient patient;
		
		private Date startDate;
		
		private String changeReason;
		
		private Concept arv1;
		
		private Concept arv2;
		
		private Concept arv3;
		
		private Double dosage1;
		
		private Double dosage2;
		
		private Double dosage3;
		
		private String units1;
		
		private String units2;
		
		private String units3;
		
		private String frequency1;
		
		private String frequency2;
		
		private String frequency3;
		
		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			require(errors, "patient");
			require(errors, "startDate");
			require(errors, "arv1");
			/**
			 * First ARV is mandatory, while the other two are optional.
			 */
			/*require(errors, "arv2");
			require(errors, "arv3");*/
			require(errors, "dosage1");
			/*require(errors, "dosage2");
			require(errors, "dosage3");*/
			require(errors, "units1");
			/*require(errors, "units2");
			require(errors, "units3");*/
			require(errors, "frequency1");
			/*require(errors, "frequency2");
			require(errors, "frequency3");*/
		}
		
		/**
		 * @param medSet
		 */
		@SuppressWarnings("deprecation")
		public void applyNewRegimen(Concept medSet) {
			RegimenHistory history = RegimenHistory.forPatient(patient, medSet);
			List<RegimenChange> changes = history.getChanges();
			if (changes.size() > 0 && changes.get(changes.size() - 1).getStarted().getDrugOrders().size() > 0) {
				throw new RuntimeException("Can't Start/Restart a regimen for a patient who already has one");
			}
			
			DrugOrder o1 = newDrugOrder(patient, startDate, arv1, dosage1, units1, frequency1);
			Context.getOrderService().saveOrder(o1);
			
			if (arv2 != null) {
				DrugOrder o2 = newDrugOrder(patient, startDate, arv2, dosage2, units2, frequency2);
				Context.getOrderService().saveOrder(o2);
			}
			if (arv3 != null) {
				DrugOrder o3 = newDrugOrder(patient, startDate, arv3, dosage3, units3, frequency3);
				Context.getOrderService().saveOrder(o3);
			}
					
		}
		
		/**
		 * @param medSet
		 */
		@SuppressWarnings("deprecation")
		public void applyRegimenChange(Concept medSet) {
			RegimenHistory history = RegimenHistory.forPatient(patient, medSet);
			Date lastChange = history.getChanges().get(history.getChanges().size() - 1).getDate();
			if (OpenmrsUtil.compare(startDate, lastChange) <= 0) {
				throw new RuntimeException("Trying to change a regimen on " + startDate + " but there is a later change on "
				        + lastChange);
			}
			
			Regimen baseline = history.getRegimenOnDate(startDate);
			
			List<DrugOrder> noChanges = new ArrayList<DrugOrder>();
			List<DrugOrder> toChangeDose = new ArrayList<DrugOrder>();
			List<DrugOrder> toStart = new ArrayList<DrugOrder>();
			if (arv1 != null) {
				changeRegimenHelper(baseline, noChanges, toChangeDose, toStart, arv1, dosage1, units1, frequency1);
			}
			if (arv2 != null) {
				changeRegimenHelper(baseline, noChanges, toChangeDose, toStart, arv2, dosage2, units2, frequency2);
			}
			if (arv3 != null) {
				changeRegimenHelper(baseline, noChanges, toChangeDose, toStart, arv3, dosage3, units3, frequency3);
			}
			
			List<DrugOrder> toStop = new ArrayList<DrugOrder>(baseline.getDrugOrders());
			// for now "toChangeDose" is handled the same as toStop
			// toStop.removeAll(toChangeDose);
			toStop.removeAll(noChanges);
			
			OrderService os = Context.getOrderService();
			
			for (DrugOrder o : toStop) {
				o.setDiscontinued(true);
				o.setDiscontinuedDate(startDate);
				o.setDiscontinuedBy(Context.getAuthenticatedUser());
				o.setDiscontinuedReasonNonCoded(changeReason);
				os.saveOrder(o);
			}
			
			for (DrugOrder o : toStart) {
				o.setPatient(patient);
				o.setStartDate(startDate);
				o.setOrderType(os.getOrderType(OpenmrsConstants.ORDERTYPE_DRUG));
				os.saveOrder(o);
			}
		}
		
		private void changeRegimenHelper(Regimen baseline, List<DrugOrder> noChanges, List<DrugOrder> toChangeDose,
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
		 * @param patient
		 * @param startDate
		 * @param generic
		 * @param dosage
		 * @param doseUnit
		 * @return
		 */
		@SuppressWarnings("deprecation")
		private DrugOrder newDrugOrder(Patient patient, Date startDate, Concept generic, Double dosage, String doseUnit,
		                               String frequency) {
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
		 * @return the startDate
		 */
		public Date getStartDate() {
			return startDate;
		}
		
		/**
		 * @param startDate the startDate to set
		 */
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		
		/**
		 * @return the arv1
		 */
		public Concept getArv1() {
			return arv1;
		}
		
		/**
		 * @param arv1 the arv1 to set
		 */
		public void setArv1(Concept arv1) {
			this.arv1 = arv1;
		}
		
		/**
		 * @return the arv2
		 */
		public Concept getArv2() {
			return arv2;
		}
		
		/**
		 * @param arv2 the arv2 to set
		 */
		public void setArv2(Concept arv2) {
			this.arv2 = arv2;
		}
		
		/**
		 * @return the arv3
		 */
		public Concept getArv3() {
			return arv3;
		}
		
		/**
		 * @param arv3 the arv3 to set
		 */
		public void setArv3(Concept arv3) {
			this.arv3 = arv3;
		}
		
		/**
		 * @return the dosage1
		 */
		public Double getDosage1() {
			return dosage1;
		}
		
		/**
		 * @param dosage1 the dosage1 to set
		 */
		public void setDosage1(Double dosage1) {
			this.dosage1 = dosage1;
		}
		
		/**
		 * @return the dosage2
		 */
		public Double getDosage2() {
			return dosage2;
		}
		
		/**
		 * @param dosage2 the dosage2 to set
		 */
		public void setDosage2(Double dosage2) {
			this.dosage2 = dosage2;
		}
		
		/**
		 * @return the dosage3
		 */
		public Double getDosage3() {
			return dosage3;
		}
		
		/**
		 * @param dosage3 the dosage3 to set
		 */
		public void setDosage3(Double dosage3) {
			this.dosage3 = dosage3;
		}
		
		/**
		 * @return the units1
		 */
		public String getUnits1() {
			return units1;
		}
		
		/**
		 * @param units1 the units1 to set
		 */
		public void setUnits1(String units1) {
			this.units1 = units1;
		}
		
		/**
		 * @return the units2
		 */
		public String getUnits2() {
			return units2;
		}
		
		/**
		 * @param units2 the units2 to set
		 */
		public void setUnits2(String units2) {
			this.units2 = units2;
		}
		
		/**
		 * @return the units3
		 */
		public String getUnits3() {
			return units3;
		}
		
		/**
		 * @param units3 the units3 to set
		 */
		public void setUnits3(String units3) {
			this.units3 = units3;
		}
		
		/**
		 * @return the frequency1
		 */
		public String getFrequency1() {
			return frequency1;
		}
		
		/**
		 * @param frequency1 the frequency1 to set
		 */
		public void setFrequency1(String frequency1) {
			this.frequency1 = frequency1;
		}
		
		/**
		 * @return the frequency2
		 */
		public String getFrequency2() {
			return frequency2;
		}
		
		/**
		 * @param frequency2 the frequency2 to set
		 */
		public void setFrequency2(String frequency2) {
			this.frequency2 = frequency2;
		}
		
		/**
		 * @return the frequency3
		 */
		public String getFrequency3() {
			return frequency3;
		}
		
		/**
		 * @param frequency3 the frequency3 to set
		 */
		public void setFrequency3(String frequency3) {
			this.frequency3 = frequency3;
		}
		
		/**
		 * @return the changeReason
		 */
		public String getChangeReason() {
			return changeReason;
		}
		
		/**
		 * @param changeReason the changeReason to set
		 */
		public void setChangeReason(String changeReason) {
			this.changeReason = changeReason;
		}
		
	}
	
}
