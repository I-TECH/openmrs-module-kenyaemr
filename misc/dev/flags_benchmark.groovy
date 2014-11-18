
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
 * Benchmarks all registered flag calculations
 */

import org.openmrs.api.context.Context
import org.openmrs.api.PatientSetService

/**
 * Fetches a singleton component from the application context
 * @param className the class name
 */
def contextSingleton = { className ->
	return Context.getRegisteredComponents(Context.loadClass(className))[0]
}

def manager = contextSingleton("org.openmrs.module.kenyacore.calculation.CalculationManager")
def flagClazz = Context.loadClass("org.openmrs.module.kenyacore.calculation.PatientFlagCalculation")
def calcSvc = Context.getService(Context.loadClass("org.openmrs.calculation.patient.PatientCalculationService"))
def cohort = Context.patientSetService.getPatientsByCharacteristics(null, null, null) // all patients
def calcTimes = [:]

// Find and sort all flag calculation classes
def calcClasses = manager.allCalculationClasses
		.findAll({ flagClazz.isAssignableFrom(it) })
		.sort({ a, b -> a.name <=> b.name } as Comparator)

// Records a time against a flag calculation class
def recordTime = { calcClazz, time ->
	def currentTotal = calcTimes[calcClazz]
	def newTotal = currentTotal ? (currentTotal + time) : time
	calcTimes[calcClazz] = newTotal
}

// Test all calculations against all patients
cohort.memberIds.each { patientId ->

	calcClasses.each { calcClazz ->
		def calc = calcClazz.newInstance()

		def start = System.currentTimeMillis()
		calcSvc.evaluate(patientId, calc)
		def end = System.currentTimeMillis()

		recordTime calcClazz, (end - start)
	}
}

// Print summary
println "Tested with " + cohort.size() + " patients"
println "Average time for all flags: " + (calcTimes.values().sum() / cohort.size())

calcTimes.each { calcClazz, totalTime ->
	def avgTime = totalTime / cohort.size()
	println calcClazz.name + " &rarr; " + avgTime
}