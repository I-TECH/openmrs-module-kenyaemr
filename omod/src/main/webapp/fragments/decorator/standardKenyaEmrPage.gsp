<%
	ui.includeCss("uilibrary", "jquery-ui.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");
	
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("uilibrary", "jquery-ui.js")
	ui.includeJavascript("kenyaemr", "jquery-ui-timepicker-addon-mod.js")
	
	ui.includeJavascript("uiframework.js")
	ui.includeJavascript("kenyaemr.js")
	ui.includeJavascript("highcharts.js")
	
	if (config.patient) {
		config.context = "patientId=${ patient.id }"
	}

	config.beforeContent = ui.includeFragment("kenyaHeader", config)
	config.beforeContent += ui.includeFragment("kenyaRunningApp", config)

	if (config.patient) {
		config.beforeContent += ui.includeFragment("selectedPatientHeader", [ closeChartUrl: config.closeChartUrl ])
	}
	
	ui.decorateWith("standardPage", config)
%>

<%= config.content %>