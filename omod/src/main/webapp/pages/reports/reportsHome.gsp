<%
	ui.decorateWith("kenyaemr", "standardPage")

	def onReportClick = { report ->
		def opts = [ appId: currentApp.id, reportUuid: report.definitionUuid, returnUrl: ui.thisUrl() ]
		"""ui.navigate('${ ui.pageLink('kenyaemr', 'report', opts) }');"""
	}

	def programs = reportsByProgram.keySet()

	def programNameToSlug = {
		it.toLowerCase().replace(" ", "")
	}

	def indicatorReports = { it.findAll({ it.isIndicator }) }
	def cohortReports = { it.findAll({ !it.isIndicator }) }
%>

<div class="ke-page-content">

	<div id="program-tabs" class="ke-tabs">
		<div class="ke-tabmenu">
			<% reportsByProgram.keySet().each { programName -> %>
			<div class="ke-tabmenu-item" data-tabid="${ programNameToSlug(programName) }">${ programName }</div>
			<% } %>
		</div>
		<% reportsByProgram.each { programName, reports -> %>
		<div class="ke-tab" data-tabid="${ programNameToSlug(programName) }">
			<table cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td style="width: 50%; vertical-align: top">
						<div class="ke-panel-frame">
							<div class="ke-panel-heading">Indicator</div>
							<div class="ke-panel-content">
								${ ui.includeFragment("kenyaemr", "widget/reportStack", [ reports: indicatorReports(reports), onReportClick: onReportClick ]) }
							</div>
						</div>
					</td>
					<td style="width: 50%; vertical-align: top; padding-left: 5px">
						<div class="ke-panel-frame">
							<div class="ke-panel-heading">Cohort</div>
							<div class="ke-panel-content">
								${ ui.includeFragment("kenyaemr", "widget/reportStack", [ reports: cohortReports(reports), onReportClick: onReportClick ]) }
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<% } %>
	</div>
</div>