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

/**
 * Correct all errors brought about by the moh257 therapy form
 */
import org.openmrs.api.context.Context;

def concept = Context.getConceptService().getConceptByUuid("159599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
def encounterTherapyForm = Context.formService.getFormByUuid("8f5b3ba5-1677-450f-8445-33b9a38107ae");
def encounterTypeTherapy = Context.encounterService.getEncounterTypeByUuid("de78a6be-bfc5-4634-adc3-5f1a280455cc");

def encounters = Context.encounterService.getEncounters(null, null, null, null, Arrays.asList(encounterTherapyForm), Arrays.asList(encounterTypeTherapy), null, null, null, false);


// list of all obs
def obsList = []
int count = 0;
for (def encounter : encounters) {
  obsList = encounter.getAllObs(false)
  for(def obs : obsList){
	if(obs.getConcept().equals(concept)){
		obs.setVoided(true);
		obs.setVoidedBy(Context.getAuthenticatedUser());
	  	obs.setVoidReason("Wrong Concept mapping corrected")
		obs.setDateVoided(new Date());
	  
	  count++
	}
  }
}

println "=================== Summary ======================"
println "Observations found: " + obsList.size()+" Matching records"
println "Corrected "+count+" Observation entries"

