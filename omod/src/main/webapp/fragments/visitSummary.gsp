<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Visit Summary" ])

%>
${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Type", value: visit.visitType ]) }
${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Location", value: visit.location ]) }
${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "When", value: kenyaUi.formatVisitDates(visit) ]) }

<div style="clear: both"></div>
