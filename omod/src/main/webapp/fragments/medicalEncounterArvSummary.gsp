<%
	config.require("editable")

	def editUrl = config.editable ? ui.pageLink("kenyaemr", "medicalEncounterArvRegimen", [ patientId: patient.id ]) : null

	ui.decorateWith("kenyaemr", "panel", [ heading: "ARV Summary", editUrl: editUrl ])
%>

<% if (lastChange) { %>
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: kenyaEmrUi.formatRegimenLong(lastChange.started, ui) ]) }
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Started", value: lastChange.date, showDateInterval: true ]) }
<% } else { %>
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: "None" ]) }
<% } %>