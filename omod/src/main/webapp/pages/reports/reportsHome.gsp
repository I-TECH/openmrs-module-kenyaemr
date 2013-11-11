<%
	ui.decorateWith("kenyaemr", "standardPage")

	def onReportClick = { report ->
		def opts = [ appId: currentApp.id, reportUuid: report.definitionUuid, returnUrl: ui.thisUrl() ]
		"""ui.navigate('${ ui.pageLink('kenyaemr', 'report', opts) }');"""
	}
%>

<div class="ke-page-content">
	<table cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td style="width: 50%; vertical-align: top">
				<div class="ke-panel-frame">
					<div class="ke-panel-heading">General</div>
					<div class="ke-panel-content">
						${ ui.includeFragment("kenyaemr", "widget/reportStack", [ reports: commonReports, onReportClick: onReportClick ]) }
					</div>
				</div>
			</td>
			<td style="width: 50%; vertical-align: top; padding-left: 5px">
				<% programReports.each { programName, programReports -> %>
				<div class="ke-panel-frame">
					<div class="ke-panel-heading">${ programName }</div>
					<div class="ke-panel-content">
						${ ui.includeFragment("kenyaemr", "widget/reportStack", [ reports: programReports, onReportClick: onReportClick ]) }
					</div>
				</div>
				<% } %>
			</td>
		</tr>
	</table>
</div>