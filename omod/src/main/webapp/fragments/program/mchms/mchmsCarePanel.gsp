<%
	ui.decorateWith("kenyaui", "panel", [heading: "MCH Care"])

	def dataPoints = []

	dataPoints << [label: "HIV Status", value: calculations.hivStatus]
	dataPoints << [label: "On ART", value: calculations.onHaart]

%>

<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>