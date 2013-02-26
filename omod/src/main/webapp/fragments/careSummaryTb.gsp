<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "TB Care" ])

	def dataPoints = []

	dataPoints << [ label: "Disease classification", value: calculations.tbDiseaseClassification ]
	dataPoints << [ label: "Patient classification", value: calculations.tbPatientClassification ]
%>

<div class="stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaemr", "dataPoint", it) } %>
</div>
<div class="stack-item">
	<% if (config.allowRegimenEdit) { %>
	<div class="edit-button"><a href="${ ui.pageLink("kenyaemr", "regimenEditor", [ patientId: patient.id, category: "TB", returnUrl: ui.thisUrl() ]) }">Edit</a></div>
	<% } %>

	<%
		if (regimenHistory.lastChange) {
			def lastChange = regimenHistory.lastChange
			def regimen = lastChange.started ? kenyaEmrUi.formatRegimenLong(lastChange.started, ui) : ui.message("general.none")
			def dateLabel = lastChange.started ? "Started" : "Stopped"
	%>
	${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: regimen ]) }
	${ ui.includeFragment("kenyaemr", "dataPoint", [ label: dateLabel, value: lastChange.date, showDateInterval: true ]) }
	<% } else { %>
	${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: ui.message("kenyaemr.neverOnTbRegimen") ]) }
	<% } %>
</div>