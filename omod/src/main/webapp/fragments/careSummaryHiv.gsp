<%
	def dataPoints = []

	if (config.complete) {
		def initialArtStartDate = calculations.initialArtRegimen ? calculations.initialArtRegimen.value.startDate : null
		if (initialArtStartDate) {
			dataPoints << [ label: "ART start date", value: initialArtStartDate, showDateInterval: true ]
			dataPoints << [ label: "Initial ART regimen", value: kenyaEmrUi.formatRegimenLong(calculations.initialArtRegimen.value, ui) ]

			if (calculations.currentArtRegimen) {
				dataPoints << [ label: "Current ART regimen", value: kenyaEmrUi.formatRegimenLong(calculations.currentArtRegimen.value, ui) ]
			}
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

<% dataPoints.each { print ui.includeFragment("kenyaemr", "dataPoint", it) } %>