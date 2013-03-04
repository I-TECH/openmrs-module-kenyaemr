<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = []
	if (supportsExcel) {
		menuItems.add(0, [ iconProvider: "kenyaemr", icon: "buttons/report_download_excel.png", label: "Download as Excel", href: ui.pageLink("kenyaemr", "reportsRunPatientListReport", [ manager: manager.class.name, mode: "excel" ]) ])
	}

	menuItems << [ iconProvider: "kenyaemr", icon: "buttons/back.png", label: "Back to Reports", href: ui.pageLink("kenyaemr", "reportsHome") ]
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [ heading: "Report", items: menuItems ]) }
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "reportOutput", [ definition: definition, data: data ]) }
</div>