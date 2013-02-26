<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "HIV Care" ])

	def dataPoints = []

	if (config.complete) {
		def initialArtStartDate = calculations.initialArtRegimen ? calculations.initialArtRegimen.value.startDate : null
		if (initialArtStartDate) {
			dataPoints << [ label: "ART start date", value: initialArtStartDate, showDateInterval: true ]
			dataPoints << [ label: "Initial ART regimen", value: kenyaEmrUi.formatRegimenLong(calculations.initialArtRegimen.value, ui) ]
		} else {
			dataPoints << [ label: "ART start date", value: "Never" ]
		}
	}

	if (calculations.lastWHOStage) {
		dataPoints << [ label: "Last WHO stage", value: ui.format(calculations.lastWHOStage.value.valueCoded), extra: calculations.lastWHOStage.value.obsDatetime ]
	} else {
		dataPoints << [ label: "Last WHO stage", value: "None" ]
	}

	if (calculations.lastCD4Count) {
		dataPoints << [ label: "Last CD4 count", value: ui.format(calculations.lastCD4Count.value) + " cells/&micro;L", extra: calculations.lastCD4Count.value.obsDatetime ]
	} else {
		dataPoints << [ label: "Last CD4 count", value: "None" ]
	}

	if (calculations.lastCD4Percent) {
		dataPoints << [ label: "Last CD4 percentage", value: ui.format(calculations.lastCD4Percent.value) + " %", extra: calculations.lastCD4Percent.value.obsDatetime ]
	}
	else {
		dataPoints << [ label: "Last CD4 percentage", value: "None" ]
	}
%>

<div class="stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaemr", "dataPoint", it) } %>
</div>
<div class="stack-item">
<% if (config.allowRegimenEdit) { %>
	<div class="edit-button"><a href="${ ui.pageLink("kenyaemr", "regimenEditor", [ patientId: patient.id, category: "ARV", returnUrl: ui.thisUrl() ]) }">Edit</a></div>
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
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Regimen", value: ui.message("kenyaemr.neverOnARVs") ]) }
<% } %>
</div>