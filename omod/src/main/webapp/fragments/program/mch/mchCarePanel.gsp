<%
	ui.decorateWith("kenyaui", "panel", [heading: "MCH Care"])

	def dataPoints = []

	dataPoints << [label: "Gestation", value: calculations.gestation]
	dataPoints << [label: "On PMTCT", value: calculations.onPmtct]
	dataPoints << [label: "On ARV", value: calculations.onArv]

%>

<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>