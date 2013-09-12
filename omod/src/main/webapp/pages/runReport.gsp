<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems =  []

	if (isIndicator) {
		menuItems << [
				iconProvider: "kenyaui",
				icon: "buttons/report_generate.png",
				label: "Run Report",
				extra: "View online without formatting",
				href: "javascript:viewReportOnline()"
		]
	}

	if (excelRenderable) {
		menuItems << [
				iconProvider: "kenyaui",
				icon: "buttons/report_download_excel.png",
				label: "Download as Excel",
				href: "javascript:downloadAsExcel()"
		]
	}

	menuItems << [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]
%>
<div class="ke-page-sidebar">

	<div class="ke-panel-frame" id="end-of-day">
		<div class="ke-panel-heading">Report</div>

		<% if (isIndicator) { %>
			<script type="text/javascript">
				function viewReportOnline() {
					jq('#report-parameters').submit();
				}
				function downloadAsExcel() {
					var startDate = jq('#report-startdate').val();
					location.href = ui.pageLink('kenyaemr', 'downloadReportAsExcel', { appId: '${ currentApp.id }', reportId: '${ report.id }', startDate: startDate });
				}
			</script>
			<div class="ke-panel-content" style="text-align: center">
				<form method="post" id="report-parameters">
					Period:
					${ ui.includeFragment("kenyaui", "widget/selectList", [
							id: "report-startdate",
							formFieldName: "startDate",
							selected: startDateSelected,
							options: startDateOptions,
							optionsValueField: "key",
							optionsDisplayField: "value"
					]) }
				</form>
			</div>
		<% } %>

		<% menuItems.each { item -> %>
			${ ui.includeFragment("kenyaui", "widget/panelMenuItem", item) }
		<% } %>
	</div>
</div>

<div class="ke-page-content">
<% if (isIndicator) { %>
	${ ui.includeFragment("kenyaemr", "report/indicatorReport", [ report: report ]) }
<% } else { %>
	${ ui.includeFragment("kenyaemr", "report/patientListReport", [ report: report ]) }
<% } %>
</div>