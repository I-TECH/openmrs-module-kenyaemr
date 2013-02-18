<%
	config.require("editable")

	def editUrl = config.editable ? ui.pageLink("kenyaemr", "regimenEditor", [ patientId: patient.id, returnUrl: ui.thisUrl() ]) : null

	ui.decorateWith("kenyaemr", "panel", [ heading: "ARV Summary", editUrl: editUrl ])

	if (lastChange) {
		def regimen = lastChange.started ? kenyaEmrUi.formatRegimenLong(lastChange.started, ui) : ui.message("general.none")
		def dateLabel = lastChange.started ? "Started" : "Stopped"
%>
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: regimen ]) }
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: dateLabel, value: lastChange.date, showDateInterval: true ]) }
<% } else { %>
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: ui.message("kenyaemr.neverOnARVs") ]) }
<% } %>