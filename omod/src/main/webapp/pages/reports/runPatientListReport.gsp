<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to Reports", href: ui.pageLink("kenyaemr", "reports/reportsHome") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Report", items: menuItems ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "report/patientListReport", [ builder: builder ]) }
</div>