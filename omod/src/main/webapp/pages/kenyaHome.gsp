<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ doNotShowApp: true, patient: patient, closeChartUrl: ui.pageLink("kenyaemr", "kenyaHome") ])
	def APPS_PER_ROW = 3;
%>

<style type="text/css">
#homepage-apps {
	text-align: center;
}
.app-button {
	border-bottom: 1px #BBB solid;
	border-right: 1px #BBB solid;
	background-color: #e0e0e0;
	padding: 0.3em;
	margin: 0.5em;
	float: left;
	text-align: center;
	width: 7.5em;
	height: 5.5em;
	border-radius: 3px;
}

.app-button-label {
	color: #444;
	font-weight: bold;
	text-decoration: none;
}
</style>

<div id="homepage-apps">	
	<% apps.eachWithIndex { app, i -> %>
		<div class="clickable app-button" <% if (i % APPS_PER_ROW == 0) { %> style="clear: left;" <% } %> onclick="location.href='/${ contextPath }/${ app.homepageUrl }<% if (patient) { %>?patientId=${ patient.id }<% } %>'">
			<% if (app.iconUrl) { %>
				<img src="/${ contextPath }/${ app.iconUrl }" height="64"/><br/>
			<% } %>
			<span class="app-button-label">
				${ app.label }
			</span>
		</div>
	<% } %>
</div>