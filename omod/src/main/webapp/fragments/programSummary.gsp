<%
	ui.decorateWith("kenyaemr", "panel", [ heading: ui.format(program) ])

	def editButton = { url ->
		return """<div class="edit-button"><a href="${ url }">Edit</a></div>"""
	}

	def discontinueHtml = "<br />" + ui.includeFragment("kenyaui", "widget/button", [
			label: "Discontinue Services",
			href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: exitFormUuid, returnUrl: ui.thisUrl() ])
	])

	def helper = { enrollment ->
		def editHtml = ""
		if (registrationFormUuid) {
			editHtml = editButton(ui.pageLink("kenyaemr", "editProgramHtmlForm", [ patientId: patient.id, patientProgramId: enrollment.id, formUuid: registrationFormUuid, returnUrl: ui.thisUrl() ]))
		}

		def completedHtml = ""
		if (enrollment.dateCompleted) {
			completedHtml += ui.includeFragment("kenyaemr", "dataPoint", [ label: "Completed", value: enrollment.dateCompleted ])
			completedHtml += ui.includeFragment("kenyaemr", "dataPoint", [ label: "Outcome", value: enrollment.outcome ])
		}

		def enrollmentExtraHtml = config.enrollmentExtra ? (config.enrollmentExtra instanceof Closure ? config.enrollmentExtra(enrollment) : config.enrollmentExtra) : ""

		return """<div class="stack-item">
			${ editHtml }
		    ${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Enrolled", value: enrollment.dateEnrolled, showDateInterval: true ]) }
			${ enrollmentExtraHtml }
			${ completedHtml }
			${ (!enrollment.dateCompleted && exitFormUuid) ? discontinueHtml : "" }
		</div>"""
	}
%>

<% if (config.overviewContent) { %>
	${ config.overviewContent }
<% } %>

<% if (currentEnrollment) { %>
${ helper(currentEnrollment) }
<% } else { %>
<div class="stack-item">
	${ ui.includeFragment("kenyaui", "widget/button", [
		label: ui.format(program),
		classes: [ "padded "],
		extra: "Enroll",
		iconProvider: "kenyaui",
		icon: "buttons/program_enroll.png",
		href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: registrationFormUuid, returnUrl: ui.thisUrl() ])
	]) }
</div>
<% } %>

<% pastEnrollments.reverse().each { print helper(it) } %>
