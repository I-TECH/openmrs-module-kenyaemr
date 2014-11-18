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
		config.beforeContent += ui.includeFragment("kenyaemr", "header/patientHeader", [ patient: config.patient, closeChartUrl: config.closeChartUrl ])
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

<!-- Required for the kenyaemr.ensureUserAuthenticated(...) method -->
<div id="authdialog" title="Login Required" style="display: none">
	<div class="ke-panel-content">
		<table border="0" align="center">
			<tr>
				<td colspan="2" style="text-align: center; padding-bottom: 12px">
					Your session has expired so please authenticate

					<div class="error" style="display: none;">Invalid username or password. Please try again.</div>
				</td>
			</tr>
			<tr>
				<td align="right"><b>Username:</b></td>
				<td><input type="text" id="authdialog-username"/></td>
			</tr>
			<tr>
				<td align="right"><b>Password:</b></td>
				<td><input type="password" id="authdialog-password"/></td>
			</tr>
		</table>
	</div>
	<div class="ke-panel-controls">
		<button type="button"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/login.png") }" /> Login</button>
	</div>
</div>