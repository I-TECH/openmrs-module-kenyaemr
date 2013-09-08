<%
	ui.decorateWith("kenyaemr", "standardPage")
%>

<div class="ke-page-content">
	<table cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td style="width: 50%; vertical-align: top">
				<div class="ke-panel-frame">
					<div class="ke-panel-heading">General</div>
					<div class="ke-panel-content" style="height: 100%">

						<% commonReports.each { report -> %>
						<div class="ke-stack-item ke-navigable">
							<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "runReport", [ appId: currentApp.id, reportId: report.id, returnUrl: ui.thisUrl() ]) }" />
							<table>
								<tr>
									<td><img src="${ ui.resourceLink("kenyaui", "images/reports/" + (report.isIndicator ? "indicator" : "patient_list") + ".png") }" alt="View report" /></td>
									<td><strong>${ report.name }</strong></td>
								</tr>
							</table>
						</div>
						<% } %>

					</div>
				</div>
			</td>
			<td style="width: 50%; vertical-align: top; padding-left: 5px">
				<% programReports.each { programName, programReports -> %>
				<div class="ke-panel-frame">
					<div class="ke-panel-heading">${ programName }</div>
					<div class="ke-panel-content">

						<% programReports.each { report -> %>
						<div class="ke-stack-item ke-navigable" style="overflow: auto">
							<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", "runReport", [ appId: currentApp.id, reportId: report.id, returnUrl: ui.thisUrl() ]) }" />
							<table>
								<tr>
									<td><img src="${ ui.resourceLink("kenyaui", "images/reports/" + (report.isIndicator ? "indicator" : "patient_list") + ".png") }" alt="View report" /></td>
									<td><strong>${ report.name }</strong></td>
								</tr>
							</table>
						</div>
						<% } %>

					</div>
				</div>
				<% } %>
			</td>
		</tr>
	</table>
</div>