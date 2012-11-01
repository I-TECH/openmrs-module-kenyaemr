<%
	ui.includeCss("uilibrary", "jquery-ui.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");
	
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("uilibrary", "jquery-ui.js")
	ui.includeJavascript("kenyaemr", "jquery-ui-timepicker-addon-mod.js")
	
	ui.includeJavascript("kenyaemr", "uiframework.js")
	ui.includeJavascript("kenyaemr", "kenyaemr.js")
	if (config.patient) {
		config.context = "patientId=${ patient.id }"
	}

	config.beforeContent = ui.includeFragment("kenyaemr", "kenyaHeader", config)
	config.beforeContent += ui.includeFragment("kenyaemr", "kenyaRunningApp", config)

	if (config.patient) {
		config.beforeContent += ui.includeFragment("kenyaemr", "selectedPatientHeader", [ closeChartUrl: config.closeChartUrl ])
	}
	
	ui.decorateWith("uilibrary", "standardPage", config)
%>

<%= config.content %>