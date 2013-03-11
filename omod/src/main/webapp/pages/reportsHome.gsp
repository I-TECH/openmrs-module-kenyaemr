<%
	ui.decorateWith("kenyaemr", "standardPage")
%>

<table cellspacing="0" cellpadding="5" width="100%">
	<tr>
		<td width="50%" valign="top">
			<div class="panel-frame">
				<div class="panel-heading">Ministry of Health Reports</div>
				<div class="panel-content" style="height: 100%">

					<% mohReports.each { %>
					<div class="stack-item ke-clickable">
						<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "reportsRunMonthlyIndicatorReport", [ builder: it.builder ]) }" />
						<table>
							<tr>
								<td><img src="${ ui.resourceLink("kenyaui", "images/reports/moh.png") }" alt="View report" /></td>
								<td><b>${ it.name }</b></td>
							</tr>
						</table>
					</div>
					<% } %>

				</div>
			</div>
		</td>
		<td width="50%" valign="top">
			<div class="panel-frame">
				<div class="panel-heading">Facility Reports</div>
				<div class="panel-content">

					<% facilityReports.each { %>
					<div class="stack-item ke-clickable" style="overflow: auto">
						<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "reportsRunPatientListReport", [ builder: it.builder ]) }" />
						<table>
							<tr>
								<td><img src="${ ui.resourceLink("kenyaui", "images/reports/facility.png") }" alt="View report" /></td>
								<td><b>${ it.name }</b></td>
							</tr>
						</table>
					</div>
					<% } %>

				</div>
			</div>
		</td>
	</tr>
</table>