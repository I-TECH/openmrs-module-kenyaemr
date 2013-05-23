<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient, closeChartUrl: ui.pageLink("kenyaemr", "home") ])
%>
<div>
	<% apps.eachWithIndex { app, i -> %>
	<div class="ke-control ke-button ke-app-button" onclick="location.href='/${ contextPath }/${ app.homepageUrl }<% if (patient) { %>?patientId=${ patient.id }<% } %>'">
		<% if (app.iconUrl) { %>
			<img src="/${ contextPath }/${ app.iconUrl }" alt="${ app.label }" /><br/>
		<% } %>
		<div class="ke-label" style="margin-top: 5px;">
			${ app.label }
		</div>
	</div>
	<% } %>
</div>