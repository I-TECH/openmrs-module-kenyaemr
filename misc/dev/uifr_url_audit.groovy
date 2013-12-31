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
 * Audits all UIFR managed page controllers and fragment actions
 */

import org.openmrs.api.context.Context
import org.openmrs.module.ModuleFactory
import org.openmrs.util.OpenmrsClassLoader
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.RegexPatternTypeFilter
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.util.regex.Pattern

/**
 * Fetches a singleton component from the application context
 * @param className the class name
 */
def contextComponents = { className ->
	return Context.getRegisteredComponents(Context.loadClass(className))
}

/**
 * Creates a class path scanner
 * @param regex the class name regex
 */
def scanner = { regex ->
	def scanner = new ClassPathScanningCandidateComponentProvider(false)
	scanner.setResourceLoader(new PathMatchingResourcePatternResolver(OpenmrsClassLoader.instance))
	scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(regex)))
	scanner
}

/**
 * Uncapitalizes a string (e.g. HelloWorld -> helloWorld)
 */
def uncapitalize = {
	it[0].toLowerCase() + it.substring(1)
}

/**
 * Re-constructs a page URL
 */
def pageUrl = { provider, basePkg, clazz ->
	def relPkg = (clazz.package.name - basePkg).replace(".", "/")
	def page = clazz.simpleName.substring(0, clazz.simpleName.length() - 14)
	provider + relPkg + "/" + uncapitalize(page) + ".page"
}

/**
 * Re-constructs an action URL
 */
def actionUrl = { provider, basePkg, clazz, method ->
	def relPkg = (clazz.package.name - basePkg).replace(".", "/")
	def frag = clazz.simpleName.substring(0, clazz.simpleName.length() - 18)
	provider + relPkg + "/" + uncapitalize(frag) + "/" + method.name + ".action"
}

/**
 * Formats annotations from a page or action
 */
def formatAnnotations = { annotations ->
	def ret = annotations.collect {
		def annoClazz = it.annotationType().simpleName;

		if (annoClazz == "AppAction" || annoClazz == "AppPage") {
			return it.value()
		} else if (annoClazz == "SharedAction" || annoClazz == "SharedPage") {
			return it.value() ? it.value().join(", ") : "<ANY-APP>"
		} else if (annoClazz == "PublicAction" || annoClazz == "PublicPage") {
			return "<PUBLIC>"
		}
	}.join(", ")

	ret ?: "<AUTHENTICATED>"
}

/**
 * Audits the given page controller
 */
def auditPageController = { provider, basePkg, clazz ->
	def url = pageUrl(provider, basePkg, clazz)

	[ url, formatAnnotations(clazz.annotations) ]
}

/**
 * Audits the given fragment controller
 */
def auditFragmentController = { provider, basePkg, clazz ->
	def rows = []

	clazz.methods.each { action ->
		// See UIFR-134 and UIFR-135 - these methods should not be accessible
		if (! ["wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll", "controller"].contains(action.name)) {
			def url = actionUrl(provider, basePkg, clazz, action)

			rows << [ url, formatAnnotations(action.annotations) ]
		}
	}

	rows
}

/**
 * Writes an array of values as a CSV row
 */
def writeCsvRow = { writer, vals ->
	writer.write(vals.collect { it -> "\"" + it.replace("\"", "\\\"") + "\"" }.join(",") + "\n")
}

// Load all registered UIFR configurations
def uiconfigs = contextComponents "org.openmrs.ui.framework.StandardModuleUiConfiguration"

// Create output files
def pagesLog = File.createTempFile("pages", ".csv")
def pagesWriter = new FileWriter(pagesLog)

def fragsLog = File.createTempFile("fragments", ".csv")
def fragsWriter = new FileWriter(fragsLog)

writeCsvRow pagesWriter, [ "URL", "Access" ]
writeCsvRow fragsWriter, [ "URL", "Access" ]

uiconfigs.each { uiconfig ->
	def pages = 0, actions = 0

	def pagesPkg = "org.openmrs.module." + uiconfig.moduleId + ".page.controller"
	def fragsPkg = "org.openmrs.module." + uiconfig.moduleId + ".fragment.controller"

	scanner("[\\w.]+PageController").findCandidateComponents(pagesPkg).each {
		def row = auditPageController(uiconfig.moduleId, pagesPkg, Context.loadClass(it.beanClassName))
		writeCsvRow(pagesWriter, row)
		pages++
	}

	scanner("[\\w.]+FragmentController").findCandidateComponents(fragsPkg).each {
		def rows = auditFragmentController(uiconfig.moduleId, fragsPkg, Context.loadClass(it.beanClassName))
		rows.each { row ->
			writeCsvRow(fragsWriter, row)
			actions++
		}
	}

	println "Scanned module " + uiconfig.moduleId + " (pages: " + pages + ", actions: " + actions + ")"
}

pagesWriter.close()
fragsWriter.close()

println "Pages audit log written to " + pagesLog.absolutePath
println "Fragments audit log written to " + fragsLog.absolutePath