<%
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("kenyaemr.js")
	
	if (config.patient) {
		config.afterAppHeader = ui.includeFragment("selectedPatientHeader")
		config.context = "patientId=${ patient.id }"
	}
	ui.decorateWith("standardAppPage", config)
%>

<%= config.content %>