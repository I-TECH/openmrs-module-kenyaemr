<%
	ui.includeCss("kenyaui", "jquery-ui.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");
	
	ui.includeJavascript("kenyaui", "jquery.js")
	ui.includeJavascript("kenyaui", "jquery-ui.js")
	
	ui.includeJavascript("kenyaui", "kenyaui.js")
	ui.includeJavascript("kenyaemr", "kenyaemr.js")

	if (config.patient) {
		config.context = "patientId=${ patient.id }"
	}

	config.beforeContent = ui.includeFragment("kenyaemr", "pageHeader", config)
	config.beforeContent += ui.includeFragment("kenyaemr", "pageAppHeader", config)

	if (config.patient) {
		config.beforeContent += ui.includeFragment("kenyaemr", "selectedPatientHeader", [ closeChartUrl: config.closeChartUrl ])
	}
	if (config.visit) {
		config.beforeContent += ui.includeFragment("kenyaemr", "selectedVisitHeader", [ visit: config.visit ])
	}
	
	ui.decorateWith("kenyaui", "standardPage", config)
%>

<!-- Override content layout from uilibrary based on the layout config value -->

<style type="text/css">

<% if (config.layout == "sidebar") { %>
	html {
		background: #FFF url('${ ui.resourceLink("kenyaui", "images/background.png") }') repeat-y;
	}
	#content {
		padding: 0;
	}
	#content-side {
		width: 320px;
		position: absolute;
		padding: 5px;
		overflow: auto;
	}
	#content-main {
		margin-left: 330px;
		padding: 5px;
		overflow: auto;
	}
<% } %>
	/**
	 * Override styles for toasts
	 */
	.toast-item {
		background-color: #464640;
		border-radius: 3px;
		border: 0;
	}
</style>

<%= config.content %>