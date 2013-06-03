<%
    config.require("patientProgram")

	def isCompleted = (config.patientProgram.dateCompleted != null)

	def dataPoints = [
		[ label: "Enrolled", value: config.patientProgram.dateEnrolled, showDateInterval: true ]
	]

	if (isCompleted) {
		dataPoints << [ label: "Completed", value: config.patientProgram.dateCompleted ]
		dataPoints << [ label: "Outcome", value: config.patientProgram.outcome ]
	}
%>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">${ ui.format(config.patientProgram.program) }</div>
	<div class="ke-panel-content">
	<% dataPoints.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
	</div>
</div>

<% if (Metadata.hasIdentity(config.patientProgram.program, Metadata.HIV_PROGRAM)) { %>
	${ ui.includeFragment("kenyaemr", "careSummaryHiv", [ patient: patient, complete: true ]) }
<% } else if (Metadata.hasIdentity(config.patientProgram.program, Metadata.TB_PROGRAM)) { %>
	${ ui.includeFragment("kenyaemr", "careSummaryTb", [ patient: patient, complete: true ]) }
<% } %>