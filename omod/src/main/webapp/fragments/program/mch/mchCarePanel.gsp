<%
	ui.decorateWith("kenyaui", "panel", [heading: "MCH Care"])

	def dataPoints = []

	dataPoints << [label: "Gestation", value: calculations.gestation]
	dataPoints << [label: "On PMTCT", value: calculations.onPmtct]
	dataPoints << [label: "On ARV", value: calculations.onArv]

%>

<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>

<div class="ke-stack-item">
	<% if (activeVisit) { %>
	${ui.includeFragment("kenyaui", "widget/editButton", [href: ui.pageLink("kenyaemr", "regimenEditor", [patientId: currentPatient.id, category: "TB", appId: currentApp.id, returnUrl: ui.thisUrl()])])}
	<% } %>

	<%
		if (regimenHistory.lastChange) {
			def lastChange = regimenHistory.lastChangeBeforeNow
			def regimen = lastChange.started ? kenyaEmrUi.formatRegimenLong(lastChange.started, ui) : ui.message("general.none")
			def dateLabel = lastChange.started ? "Started" : "Stopped"
	%>
	${ui.includeFragment("kenyaui", "widget/dataPoint", [label: "Regimen", value: regimen])}
	${ui.includeFragment("kenyaui", "widget/dataPoint", [label: dateLabel, value: lastChange.date, showDateInterval: true])}
	<% } else { %>
	${ui.includeFragment("kenyaui", "widget/dataPoint", [label: "Regimen", value: ui.message("kenyaemr.neverOnTbRegimen")])}
	<% } %>
</div>