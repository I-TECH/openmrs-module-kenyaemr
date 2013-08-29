<%
	ui.includeCss("kenyaui", "jquery-ui.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");
	
	ui.includeJavascript("kenyaui", "jquery.js")
	ui.includeJavascript("kenyaui", "jquery-ui.js")
	
	ui.includeJavascript("kenyaui", "kenyaui.js")
	ui.includeJavascript("kenyaemr", "kenyaemr.js")

	if (config.patient) {
		config.context = "patientId=${ config.patient.id }"
	}

	config.beforeContent = ui.includeFragment("kenyaemr", "header/pageHeader", config)

	config.beforeContent += ui.includeFragment("kenyaemr", "header/systemHeader", config)

	config.beforeContent += ui.includeFragment("kenyaemr", "header/headerMenu", config)

	if (config.patient) {
		config.beforeContent += ui.includeFragment("kenyaemr", "header/patientHeader", [ patient: config.patient, visit: activeVisit, closeChartUrl: config.closeChartUrl ])
	}
	if (config.visit) {
		config.beforeContent += ui.includeFragment("kenyaemr", "header/visitHeader", [ visit: config.visit ])
	}

	config.pageTitle = "KenyaEMR"
	config.faviconIco = ui.resourceLink("kenyaemr", "images/logos/favicon.ico")
	config.faviconPng = ui.resourceLink("kenyaemr", "images/logos/favicon.png")
	
	ui.decorateWith("kenyaui", "standardPage", config)
%>

<!-- Override content layout from kenyaui based on the layout config value -->

<style type="text/css">

<% if (config.layout == "sidebar") { %>
	html {
		background: #FFF url('${ ui.resourceLink("kenyaui", "images/background.png") }') repeat-y;
	}
<% } %>

</style>

<%= config.content %>