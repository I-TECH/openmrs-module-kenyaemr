<%
	ui.decorateWith("kenyaui", "panel", [ heading: "HIV Care" ])
	def currentRegimen = lastEnc && lastEnc.regimenShortDisplay ? lastEnc.regimenShortDisplay : ui.message("general.none")
	def dateLabel = lastEnc && lastEnc.startDate && !lastEnc.endDate ? "Started" : "Stopped"

	def dataPoints = []

	if (config.complete) {

		def regimen = firstEnc && firstEnc.regimenShortDisplay ? firstEnc.regimenShortDisplay : ui.message("general.none")
		if (firstEnc && firstEnc.startDate) {

			dataPoints << [ label: "ART start date", value: firstEnc.startDate, showDateInterval: true ]
			dataPoints << [ label: "Initial ART regimen", value: regimen ]
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
		dataPoints << [ label: "Last CD4 count", value: ui.format(calculations.lastCD4Count.value) + "", extra: calculations.lastCD4Count.value.obsDatetime ]
	} else {
		dataPoints << [ label: "Last CD4 count", value: "None" ]
	}

	if (calculations.lastCD4Percent) {
		dataPoints << [ label: "Last CD4 percentage", value: ui.format(calculations.lastCD4Percent.value) + " %", extra: calculations.lastCD4Percent.value.obsDatetime ]
	}
	else {
		dataPoints << [ label: "Last CD4 percentage", value: "None" ]
	}
	if (calculations.lastViralLoad) {
		dataPoints << [ label: "Last Viral Load", value: value, extra: date ]
	}
%>

<% if (config.complete) { %>
<div class="ke-stack-item">
	<table width="100%" border="0">
		<tr>
			<td width="50%" valign="top">
				${ ui.includeFragment("kenyaui", "widget/obsHistoryTable", [ id: "tblhistory", patient: currentPatient, concepts: graphingConcepts ]) }
			</td>
			<td width="50%" valign="top">
				${ ui.includeFragment("kenyaui", "widget/obsHistoryGraph", [ id: "cd4graph", patient: currentPatient, concepts: graphingConcepts, showUnits: true, style: "height: 300px" ]) }
			</td>
		</tr>
		<tr>
			<td colspan="2"><strong>Note:*</strong> LDL default value:  ${ldl_default_value}</td>
		</tr>
	</table>
</div>
<% } %>
<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>
<div class="ke-stack-item">
	<% if (activeVisit) { %>
	<button type="button" class="ke-compact" onclick="ui.navigate('${ ui.pageLink("kenyaemr", "regimenEditor", [ patientId: currentPatient.id, category: "ARV", appId: currentApp.id, returnUrl: ui.thisUrl() ]) }')">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/edit.png") }" />
	</button>
	<% } %>

	<%
		if (lastEnc && !lastEnc.endDate) {
			def lastChange = lastEnc
	%>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Regimen", value: currentRegimen ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: dateLabel, value: lastEnc.startDate, showDateInterval: true ]) }
	<% } else if(lastEnc && lastEnc.endDate) { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Regimen", value: currentRegimen ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: dateLabel, value: lastEnc.endDate, showDateInterval: true ]) }

	<% } else { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Regimen", value: ui.message("kenyaemr.neverOnARVs") ]) }

	<% } %>
</div>