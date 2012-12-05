<%
    config.require("patientProgram")

	ui.decorateWith("kenyaemr", "panel", [ heading: ui.format(config.patientProgram.program) ])

	def isCompleted = (config.patientProgram.dateCompleted != null)

	def dataPoints = [
		[ label: "Enrolled", value: kenyaEmrUi.formatDateNoTime(config.patientProgram.dateEnrolled) ]
	]

	if (isCompleted) {
		dataPoints << [ label: "Completed", value: kenyaEmrUi.formatDateNoTime(config.patientProgram.dateCompleted) ]
		dataPoints << [ label: "Outcome", value: ui.format(config.patientProgram.outcome) ]
	}

	if (config.patientProgram.program.uuid == MetadataConstants.HIV_PROGRAM_UUID) {
		def initialArtStartDate = calculations.initialArtRegimen ? calculations.initialArtRegimen.firstResult.value.startDate : null
		if (initialArtStartDate) {
			dataPoints << [ label: "ART start date", value: kenyaEmrUi.formatDateNoTime(initialArtStartDate) ]
			dataPoints << [ label: "Initial ART regimen", value: kenyaEmrUi.formatRegimen(calculations.initialArtRegimen) ]

			if (calculations.currentArtRegimen) {
				dataPoints << [ label: "Current ART regimen", value: kenyaEmrUi.formatRegimen(calculations.currentArtRegimen) ]
			}
		} else {
			dataPoints << [ label: "ART start date", value: "Never" ]
		}

		if (calculations.lastWHOStage) {
			dataPoints << [ label: "Last WHO stage", value: ui.format(calculations.lastWHOStage.value.valueCoded), date: calculations.lastWHOStage.value.obsDatetime ]
		} else {
			dataPoints << [ label: "Last WHO stage", value: "None" ]
		}

		if (calculations.lastCD4Count) {
			dataPoints << [ label: "Last CD4 count", value: ui.format(calculations.lastCD4Count.value) + " cells/&micro;L", date: calculations.lastCD4Count.value.obsDatetime ]
		} else {
			dataPoints << [ label: "Last CD4 count", value: "None" ]
		}

		if (calculations.lastCD4Percent) {
			dataPoints << [ label: "Last CD4 percentage", value: ui.format(calculations.lastCD4Percent.value) + " %", date: calculations.lastCD4Percent.value.obsDatetime ]
		}
		else {
			dataPoints << [ label: "Last CD4 percentage", value: "None" ]
		}
	}
%>

<ul>
	<% dataPoints.each { %>
	<li>${ it.label }: <b>${ it.value }</b> <% if (it.date) { %><small>(${ kenyaEmrUi.formatDateNoTime(it.date) })</small><% } %></li>
	<% } %>
</ul>