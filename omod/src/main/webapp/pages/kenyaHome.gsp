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
	border-radius: 3px;
	background-color: #e0e0e0;
	padding: 7px;
	width: 120px;
	margin: 7px;
	float: left;
	text-align: center;
}

.app-button-label {
	color: #444;
	font-weight: bold;
	text-decoration: none;
	margin-top: 5px;
}
</style>

<div id="homepage-apps">	
	<% apps.eachWithIndex { app, i -> %>
		<div class="clickable app-button" <% if (i % APPS_PER_ROW == 0) { %> style="clear: left;" <% } %> onclick="location.href='/${ contextPath }/${ app.homepageUrl }<% if (patient) { %>?patientId=${ patient.id }<% } %>'">
			<% if (app.iconUrl) { %>
				<img src="/${ contextPath }/${ app.iconUrl }" width="64" height="64" alt="" /><br/>
			<% } %>
			<div class="app-button-label">
				${ app.label }
			</div>
		</div>
	<% } %>
</div>