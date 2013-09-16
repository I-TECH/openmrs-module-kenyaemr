<%
	ui.decorateWith("kenyaui", "panel", [heading: "MCH Care"])

	def dataPoints = []

	dataPoints << [label: "Gestation (weeks)", value: calculations.gestation]
	dataPoints << [label: "HIV Status", value: calculations.hivStatus]
	dataPoints << [label: "On Prophylaxis", value: calculations.onProhylaxis]
	dataPoints << [label: "On HAART", value: calculations.onHaart]

%>

<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>