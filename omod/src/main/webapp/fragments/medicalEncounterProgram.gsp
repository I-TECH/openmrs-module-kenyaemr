<%
	ui.decorateWith("kenyaemr", "panel", [ heading: ui.format(program) ])

	def editButton = { url ->
		return """<div class="edit-button"><a href="${ url }">Edit</a></div>"""
	}

	def helper = { enrollment ->
		def outcomeText
		if (enrollment.dateCompleted) {
			outcomeText = """Completed: <b>${ kenyaEmrUi.formatDateNoTime(enrollment.dateCompleted) }</b><br />
				Outcome: <b>${ ui.format(enrollment.outcome) }</b>"""
		} else {
			outcomeText = ""
			if (exitFormUuid) {
				outcomeText += ui.includeFragment("uilibrary", "widget/button", [
							label: "Discontinue Services",
							href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: exitFormUuid, returnUrl: ui.thisUrl() ])
						])
			}
		}

		def editHtml = ""
		if (registrationFormUuid) {
			editHtml = editButton(ui.pageLink("kenyaemr", "editProgramHtmlForm", [ patientId: patient.id, patientProgramId: enrollment.id, formUuid: registrationFormUuid, returnUrl: ui.thisUrl() ]))
		}

		return """<div class="stack-item">
				${ editHtml }
		        ${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Enrolled", value: enrollment.dateEnrolled, showDateInterval: true ]) }
				${ outcomeText }
			</div>"""
	}
%>

<% if (config.overviewContent) { %>
<div class="stack-item">
	${ config.overviewContent }
</div>
<% } %>

<% if (currentEnrollment) { %>
${ helper(currentEnrollment) }
<% } else { %>
<div class="stack-item">
	${ ui.includeFragment("uilibrary", "widget/button", [
		label: ui.format(program),
		classes: [ "padded "],
		extra: "Enroll",
		iconProvider: "kenyaemr",
		icon: "buttons/program_enroll.png",
		href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: registrationFormUuid, returnUrl: ui.thisUrl() ])
	]) }
</div>
<% } %>

<% pastEnrollments.each { print helper(it) } %>
