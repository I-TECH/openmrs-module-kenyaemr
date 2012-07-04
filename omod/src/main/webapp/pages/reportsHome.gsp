<%
	ui.decorateWith("standardKenyaEmrPage")
%>

${ ui.includeFragment("widget/button", [
		iconProvider: "uilibrary",
		icon: "chart_32.png",
		label: "MOH 731 Indicator Report",
		href: ui.pageLink("reportsRunMonthlyIndicatorReport", [ manager: "org.openmrs.module.kenyaemr.report.Moh731Report" ])
	]) }