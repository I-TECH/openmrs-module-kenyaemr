<%
	ui.decorateWith("kenyaui", "panel", [ heading: ui.format(program) ])
%>
<div class="ke-stack-item">
<% if (currentEnrollment) { %>
	${ ui.includeFragment("kenyaui", "widget/button", [
			label: "Discontinue",
			extra: "Exit from program",
			icon: "buttons/program_discontinue.png",
			iconProvider: "kenyaui",
			href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: discontinuationForm.uuid, returnUrl: ui.thisUrl() ])
	]) }
<% } else { %>
	${ ui.includeFragment("kenyaui", "widget/button", [
			label: "Enroll",
			extra: "Register on program",
			icon: "buttons/program_enroll.png",
			iconProvider: "kenyaui",
			href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: enrollmentForm.uuid, returnUrl: ui.thisUrl() ])
	]) }
<% } %>
</div>
<% enrollments.reverse().each { enrollment -> %>
	<% if (enrollment.dateCompleted) { %>
	<div class="ke-stack-item">
		${ ui.includeFragment("kenyaemr", "patientProgramDiscontinuation", [ patientProgram: enrollment, encounterType: discontinuationForm.encounterType, complete: config.complete ]) }
	</div>
	<% } %>
<div class="ke-stack-item">
	${ ui.includeFragment("kenyaemr", "patientProgramEnrollment", [ patientProgram: enrollment, encounterType: enrollmentForm.encounterType, complete: config.complete ]) }
</div>
<% } %>