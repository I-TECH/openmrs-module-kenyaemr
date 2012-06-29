<%
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("kenyaemr.js")

	if (config.patient) {
		config.context = "patientId=${ patient.id }"
	}

	config.beforeContent = ui.includeFragment("kenyaHeader", config)
	config.beforeContent += ui.includeFragment("kenyaRunningApp", config)

	if (config.patient) {
		config.beforeContent += ui.includeFragment("selectedPatientHeader")
	}
	
	ui.decorateWith("standardPage", config)
%>

<%= config.content %>