<%
	config.require("editable")

	def editUrl = config.editable ? ui.pageLink("kenyaemr", "medicalEncounterArvRegimen", [ patientId: patient.id ]) : null

	ui.decorateWith("kenyaemr", "panel", [ heading: "ARV Summary", editUrl: editUrl ])
%>

<%
if (lastChange) {
	def regimen = lastChange.started ? kenyaEmrUi.formatRegimenLong(lastChange.started, ui) : "None"
	def dateLabel = lastChange.started ? "Started" : "Stopped"
%>
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: regimen ]) }
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: dateLabel, value: lastChange.date, showDateInterval: true ]) }
<% } else { %>
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: "Never on ARVs" ]) }
<% } %>