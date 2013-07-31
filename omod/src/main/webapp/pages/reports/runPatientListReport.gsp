<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = []
	if (builder.excelRenderable) {
		menuItems.add(0, [ iconProvider: "kenyaui", icon: "buttons/report_download_excel.png", label: "Download as Excel", href: ui.pageLink("kenyaemr", "reports/runPatientListReport", [ builder: builder.class, mode: "excel" ]) ])
	}

	menuItems << [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to Reports", href: ui.pageLink("kenyaemr", "reports/reportsHome") ]
%>

<div id="content-side">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Report", items: menuItems ]) }
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "reportOutput", [ definition: definition, data: data ]) }
</div>