<%
	ui.decorateWith("kenyaemr", "standardPage", [ doNotShowApp: true, patient: patient, closeChartUrl: ui.pageLink("kenyaemr", "home") ])
	def APPS_PER_ROW = 3;
%>

<style type="text/css">
.app-button {
	padding: 7px;
	width: 120px;
	margin: 7px;
	float: left;
}
</style>

<div>
	<% apps.eachWithIndex { app, i -> %>
		<div class="ke-control ke-button app-button" <% if (i % APPS_PER_ROW == 0) { %> style="clear: left;" <% } %> onclick="location.href='/${ contextPath }/${ app.homepageUrl }<% if (patient) { %>?patientId=${ patient.id }<% } %>'">
			<% if (app.iconUrl) { %>
				<img src="/${ contextPath }/${ app.iconUrl }" width="64" height="64" alt="" /><br/>
			<% } %>
			<div class="ke-label" style="margin-top: 5px;">
				${ app.label }
			</div>
		</div>
	<% } %>
</div>