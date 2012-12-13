<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])

	def menuItems = [
		[ iconProvider: "kenyaemr", icon: "buttons/back.png", label: "Go Back to Reports", href: ui.pageLink("kenyaemr", "reportsHome") ]
	]

	if (supportsExcel) {
		menuItems.add(0, [ iconProvider: "kenyaemr", icon: "buttons/report_download_excel.png", label: "Download as Excel", href: ui.pageLink("kenyaemr", "reportsRunPatientAlertListReport", [ manager: manager.class.name, mode: "excel" ]) ])
	}
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [ heading: "Tasks", items: menuItems ]) }
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "showReportOutput", [ definition: definition, data: data ]) }
</div>