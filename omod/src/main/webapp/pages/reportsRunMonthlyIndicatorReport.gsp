<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems =  []
	if (data) {
		menuItems << [ iconProvider: "kenyaemr", icon: "buttons/report_configure.png", label: "Change Parameters", href: ui.pageLink("kenyaemr", "reportsRunMonthlyIndicatorReport", [ builder: builder.class ]) ]
	}
	menuItems << [ iconProvider: "kenyaemr", icon: "buttons/back.png", label: "Back to Reports", href: ui.pageLink("kenyaemr", "reportsHome") ]
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [ heading: "Report", items: menuItems ]) }
</div>

<div id="content-main">

<% if (data) { %>
	${ ui.includeFragment("kenyaemr", "reportOutput", [ definition: definition, data: data ]) }
<% } else { %>

	<div class="panel-frame">
		<div class="panel-heading">${ definition.name }</div>
		<div class="panel-content">
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
						options: [
							[ value: "view", label: "View online" ],
							[ value: "excel", label: "Download as Excel" ]
						],
						selected: "view"
				]) }
				<br/>
				<br/>
				${ ui.includeFragment("kenyaui", "widget/button", [
						iconProvider: "kenyaemr",
						icon: "buttons/report_generate.png",
						label: "Generate Report",
						onClick:"jq('#generate-report').submit()"
				]) }
			</form>
		</div>
	</div>

<% } %>

</div>