<%
	ui.includeCss("uilibrary", "jquery-ui.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");
	
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("uilibrary", "jquery-ui.js")
	ui.includeJavascript("kenyaemr", "jquery-ui-timepicker-addon-mod.js")
	
	ui.includeJavascript("uilibrary", "uiframework.js")
	ui.includeJavascript("kenyaemr", "kenyaemr.js")

	if (config.patient) {
		config.context = "patientId=${ patient.id }"
	}

	config.beforeContent = ui.includeFragment("kenyaemr", "kenyaHeader", config)
	config.beforeContent += ui.includeFragment("kenyaemr", "kenyaRunningApp", config)

	if (config.patient) {
		config.beforeContent += ui.includeFragment("kenyaemr", "selectedPatientHeader", [ closeChartUrl: config.closeChartUrl ])
	}
	if (config.visit) {
		config.beforeContent += ui.includeFragment("kenyaemr", "selectedVisitHeader", [ visit: config.visit ])
	}
	
	ui.decorateWith("uilibrary", "standardPage", config)
%>

<!-- Override content layout from uilibrary based on the layout config value -->

<style type="text/css">
<% if (config.layout == "sidebar") { %>
	html {
		background: #FFF url('${ ui.resourceLink("kenyaemr", "images/background.png") }') repeat-y;
	}
	#content {
		margin: 0;
		padding: 0;
		position: relative;
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
<% } else { %>
	#content {
		margin: 0;
		padding: 5px;
	}
<% } %>

	.loading-placeholder {
		background-image: url('${ ui.resourceLink("kenyaemr", "images/loading.gif") }');
	}
</style>

<%= config.content %>