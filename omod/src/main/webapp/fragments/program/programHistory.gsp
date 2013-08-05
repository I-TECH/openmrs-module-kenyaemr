<%
	ui.decorateWith("kenyaui", "panel", [ heading: ui.format(program) ])
%>
<div class="ke-stack-item">
<% if (currentEnrollment) { %>
	${ ui.includeFragment("kenyaui", "widget/button", [
			label: "Discontinue",
			extra: "Exit from program",
			icon: "buttons/program_complete.png",
			iconProvider: "kenyaui",
			href: ui.pageLink("kenyaemr", "enterForm", [ patientId: patient.id, formUuid: defaultCompletionForm.uuid, appId: currentApp.id, returnUrl: ui.thisUrl() ])
	]) }
<% } else if (patientIsEligible) { %>
	${ ui.includeFragment("kenyaui", "widget/button", [
			label: "Enroll",
			extra: "Register on program",
			icon: "buttons/program_enroll.png",
			iconProvider: "kenyaui",
			href: ui.pageLink("kenyaemr", "enterForm", [ patientId: patient.id, formUuid: defaultEnrollmentForm.uuid, appId: currentApp.id, returnUrl: ui.thisUrl() ])
	]) }
<% } %>
</div>
<% enrollments.reverse().each { enrollment -> %>
	<% if (enrollment.dateCompleted) { %>
	<div class="ke-stack-item">
		${ ui.includeFragment("kenyaemr", "program/programCompletion", [ patientProgram: enrollment, showClinicalData: config.showClinicalData ]) }
	</div>
	<% } %>
<div class="ke-stack-item">
	${ ui.includeFragment("kenyaemr", "program/programEnrollment", [ patientProgram: enrollment, showClinicalData: config.showClinicalData ]) }
</div>
<% } %>