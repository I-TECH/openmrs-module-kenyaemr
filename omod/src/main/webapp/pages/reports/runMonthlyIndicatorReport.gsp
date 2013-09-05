<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems =  []
	if (data) {
		menuItems << [ iconProvider: "kenyaui", icon: "buttons/report_configure.png", label: "Change Parameters", href: ui.pageLink("kenyaemr", "reports/runMonthlyIndicatorReport", [ builder: builder.class ]) ]
	}
	menuItems << [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to Reports", href: ui.pageLink("kenyaemr", "reports/reportsHome") ]

	def renderOptions = [ [ value: "view", label: "View online" ] ]

	if (builder.excelRenderable) {
		renderOptions << [ value: "excel", label: "Download as Excel" ]
	}
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Report", items: menuItems ]) }
</div>

<div class="ke-page-content">

<% if (data) { %>
	${ ui.includeFragment("kenyaemr", "report/indicatorReport", [ definition: definition, data: data ]) }
<% } else { %>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">${ definition.name }</div>
		<div class="ke-panel-content">
			<form method="post" id="generate-report">
				Period:
				${ ui.includeFragment("kenyaui", "widget/selectList", [
						formFieldName: "startDate",
						options: startDateOptions,
						optionsValueField: "key",
						optionsDisplayField: "value"
				]) }
				<br/>
				<br/>
				${ ui.includeFragment("kenyaui", "widget/radioButtons", [
						formFieldName: "mode",
						options: renderOptions,
						selected: "view"
				]) }
				<br/>
				<br/>
				${ ui.includeFragment("kenyaui", "widget/button", [
						iconProvider: "kenyaui",
						icon: "buttons/report_generate.png",
						label: "Generate Report",
						onClick:"jq('#generate-report').submit()"
				]) }
			</form>
		</div>
	</div>

<% } %>

</div>