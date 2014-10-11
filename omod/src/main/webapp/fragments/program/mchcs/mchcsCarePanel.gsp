<%
	ui.decorateWith("kenyaui", "panel", [heading: "Child Services Care"])

	def dataPoints = []

	dataPoints << [label: "HEI Outcome", value: calculations.heioutcomes]
	dataPoints << [label: "HIV Status", value: calculations.hivStatus]
	dataPoints << [label: "Milestones Attained", value: calculations.milestones]
	dataPoints << [label: "Remarks", value: calculations.remarks]

%>
<div class="ke-stack-item">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>