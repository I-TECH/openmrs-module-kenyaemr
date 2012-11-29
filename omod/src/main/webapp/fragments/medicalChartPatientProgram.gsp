<%
    config.require("patientProgram")

	ui.decorateWith("kenyaemr", "panel", [ heading: ui.format(config.patientProgram.program) ])

	def isCompleted = (config.patientProgram.dateCompleted != null)
	def initialArtStartDate = calculations.initialArtRegimen ? calculations.initialArtRegimen.firstResult.value.startDate : null
	def initialArtRegimen = calculations.initialArtRegimen ? kenyaEmrUi.formatRegimen(calculations.initialArtRegimen) : "Never started"
	def currentArtRegimen = calculations.currentArtRegimen ? kenyaEmrUi.formatRegimen(calculations.currentArtRegimen) : "Not on ARVs"
%>

<ul>
	<li>Enrolled: <b>${ kenyaEmrUi.formatDateNoTime(config.patientProgram.dateEnrolled) }</b></li>
	<li>Completed: <b>${ kenyaEmrUi.formatDateNoTime(config.patientProgram.dateCompleted) }</b></li>
	<% if (isCompleted) { %>
		<li>Outcome: <b>${ ui.format(config.patientProgram.outcome) }</b></li>
	<% } %>

	<% if (config.patientProgram.program.uuid == MetadataConstants.HIV_PROGRAM_UUID) { %>

		<li>Date started on ARVs: <b>${ initialArtStartDate ? kenyaEmrUi.formatDateNoTime(initialArtStartDate) : "Never started" }</b></li>
		<% if (initialArtStartDate) { %>
			<li>Initial ART regimen: <b>${ initialArtRegimen }</b></li>
			<li>Current ART regimen: <b>${ currentArtRegimen }</b></li>
		<% } %>

	<% } %>

</ul>