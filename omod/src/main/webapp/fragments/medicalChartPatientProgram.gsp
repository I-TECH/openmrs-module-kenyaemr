<%
    config.require("patientProgram")

	ui.decorateWith("kenyaemr", "panel", [ heading: ui.format(config.patientProgram.program) ])

	def isCompleted = (config.patientProgram.dateCompleted != null)

	def dataPoints = [
		[ label: "Enrolled", value: config.patientProgram.dateEnrolled, showDateInterval: true ]
	]

	if (isCompleted) {
		dataPoints << [ label: "Completed", value: config.patientProgram.dateCompleted ]
		dataPoints << [ label: "Outcome", value: config.patientProgram.outcome ]
	}
%>

<div class="stack-item">
<% dataPoints.each { print ui.includeFragment("kenyaemr", "dataPoint", it) } %>
</div>
<div class="stack-item">
<% if (config.patientProgram.program.uuid == MetadataConstants.HIV_PROGRAM_UUID) { %>
	${ ui.includeFragment("kenyaemr", "careSummaryHiv", [ patient: patient, complete: true ]) }
<% } else if (config.patientProgram.program.uuid == MetadataConstants.TB_PROGRAM_UUID) { %>
	${ ui.includeFragment("kenyaemr", "careSummaryTb", [ patient: patient, complete: true ]) }
<% } %>
</div>
