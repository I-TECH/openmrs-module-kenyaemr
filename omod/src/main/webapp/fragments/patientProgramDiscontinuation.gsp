<% if (encounter) { %>
${ ui.includeFragment("kenyaui", "widget/editButton", [
		href: ui.pageLink("kenyaemr", "editHtmlForm", [ encounterId: encounter.id, returnUrl: ui.thisUrl() ])
]) }
<% } %>
<% dataPoints.each { dataPoint -> %>
${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: dataPoint.key, value: dataPoint.value, showDateInterval: true ]) }
<% } %>