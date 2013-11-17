<%
	ui.includeCss("kenyaemr", "kenyaemr.css", 50)
	ui.includeJavascript("kenyaemr", "kenyaemr.js", 50)

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
	config.angularApp = "kenyaemr"
	
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