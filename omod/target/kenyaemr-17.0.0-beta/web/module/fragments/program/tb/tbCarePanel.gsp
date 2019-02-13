<%
	ui.decorateWith("kenyaui", "panel", [ heading: "TB Care" ])

	def dataPoints = []

	dataPoints << [ label: "Treatment Number", value: calculations.tbTreatmentNumber ]
	dataPoints << [ label: "Disease classification", value: calculations.tbDiseaseClassification ]
	dataPoints << [ label: "Patient classification", value: result ]
%>

<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>
<div class="ke-stack-item">
	<% if (activeVisit) { %>
	<button type="button" class="ke-compact" onclick="ui.navigate('${ ui.pageLink("kenyaemr", "regimenEditor", [ patientId: currentPatient.id, category: "TB", appId: currentApp.id, returnUrl: ui.thisUrl() ]) }')">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/edit.png") }" />
	</button>
	<% } %>

	<%
		if (lastEnc) {
			def lastChange = lastEnc
			def regimen = firstEnc && lastEnc.regimenShortDisplay ? lastEnc.regimenShortDisplay : ui.message("general.none")
			def dateLabel = firstEnc && lastEnc.startDate ? "Started" : "Stopped"
	%>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Regimen", value: regimen ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: dateLabel, value: lastEnc.startDate, showDateInterval: true ]) }
	<% } else { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Regimen", value: ui.message("kenyaemr.neverOnTbRegimen") ]) }
	<% } %>
</div>