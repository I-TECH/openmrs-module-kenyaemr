<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>
<style type="text/css">
	.report-container {
		float: left;
		margin-right: 5px;
	}
</style>

<div class="panel-frame report-container">
	<div class="panel-heading">Ministry of Health Reports</div>
	<div class="panel-content">

		<% mohReports.each { %>
		<div class="stack-item clickable">
			<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "reportsRunMonthlyIndicatorReport", [ manager: it.manager ]) }" />
			<table>
				<tr>
					<td><img src="${ ui.resourceLink("kenyaemr", "images/reports/moh.png") }" alt="View report" /></td>
					<td><b>${ it.name }</b></td>
				</tr>
			</table>
		</div>
		<% } %>

	</div>
</div>

<div class="panel-frame report-container">
	<div class="panel-heading">Facility Reports</div>
	<div class="panel-content">

		<% patientAlertReports.each { %>
		<div class="stack-item clickable" style="overflow: auto">
			<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "reportsRunPatientAlertListReport", [ manager: it.manager ]) }" />
			<table>
				<tr>
					<td><img src="${ ui.resourceLink("kenyaemr", "images/reports/facility.png") }" alt="View report" /></td>
					<td><b>${ it.name }</b></td>
				</tr>
			</table>
		</div>
		<% } %>

	</div>
</div>

<div class="panel-frame report-container">
	<div class="panel-heading">Patient Specific Reports</div>
	<div class="panel-content">

		<% patientSummaryReports.each { %>
		<div class="stack-item clickable">
			<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "reportsRunPatientSummaryReport", [ manager: it.manager ]) }" />
			<table>
				<tr>
					<td><img src="${ ui.resourceLink("kenyaemr", "images/reports/patient.png") }" alt="View report" /></td>
					<td><b>${ it.name }</b></td>
				</tr>
			</table>
		</div>
		<% } %>

	</div>
</div>