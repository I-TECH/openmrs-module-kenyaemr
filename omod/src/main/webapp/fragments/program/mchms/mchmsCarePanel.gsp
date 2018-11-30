<%
	ui.decorateWith("kenyaui", "panel", [heading: "MCH Care"])

	def dataPoints = []

	dataPoints << [label: "HIV Status", value: calculations.hivStatus]
//	dataPoints << [label: "On Prophylaxis", value: calculations.onProhylaxis]
//	dataPoints << [label: "On HAART", value: calculations.onHaart]

%>


<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
	<%
		if (regimenHistory.lastChange) {
			def lastChange = regimenHistory.lastChangeBeforeNow
			def regimen = lastChange.started ? kenyaEmrUi.formatRegimenLong(lastChange.started, ui) : ui.message("general.none")
			def dateLabel = lastChange.started ? "Started" : "Stopped"
	%>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "On HAART", value: "YES" ]) }
	<% } else { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "On HAART", value: "NO" ]) }
	<% } %>

</div>