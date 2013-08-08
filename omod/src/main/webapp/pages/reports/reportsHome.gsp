<%
	ui.decorateWith("kenyaemr", "standardPage")
%>

<div class="ke-page-content">
	<table cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td style="width: 50%; vertical-align: top">
				<div class="ke-panel-frame">
					<div class="ke-panel-heading">Ministry of Health Reports</div>
					<div class="ke-panel-content" style="height: 100%">

						<% mohReports.each { %>
						<div class="ke-stack-item ke-navigable">
							<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "reports/runMonthlyIndicatorReport", [ builder: it.builder ]) }" />
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
			<td style="width: 50%; vertical-align: top; padding-left: 5px">
				<div class="ke-panel-frame">
					<div class="ke-panel-heading">Facility Reports</div>
					<div class="ke-panel-content">

						<% facilityReports.each { %>
						<div class="ke-stack-item ke-navigable" style="overflow: auto">
							<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "reports/runPatientListReport", [ builder: it.builder ]) }" />
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
</div>