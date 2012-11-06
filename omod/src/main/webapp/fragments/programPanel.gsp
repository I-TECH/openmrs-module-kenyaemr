<%
	def helper = { enrollment ->
		def outcomeText
		if (enrollment.dateCompleted) {
			outcomeText = """Outcome: ${ ui.format(enrollment.outcome) } on ${ ui.format(enrollment.dateCompleted) }"""
		} else {
			outcomeText = "Active <br/>"
			if (exitFormUuid) {
				outcomeText += ui.includeFragment("uilibrary", "widget/button", [
							label: "Discontinue Services",
							href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: exitFormUuid, returnUrl: ui.thisUrl() ])
						])
			}
		}
		def editHtml = ""
		if (registrationFormUuid) {
			editHtml = """<div class="edit-button">
							<a href="${ ui.pageLink("kenyaemr", "editProgramHtmlForm", [
									patientId: patient.id,
									patientProgramId: enrollment.id,
									formUuid: registrationFormUuid,
									returnUrl: ui.thisUrl()
								]) }">Edit</a>
						</div>"""
		}
		
		def title = enrollment.dateCompleted ? "<i>[Discontinued]</i> ${ ui.format(program) }" : ui.format(program)
			
		return """
			<fieldset class="editable">
				<legend>${ title }</legend>
				${ editHtml }
		
				Enrolled: ${ ui.format(enrollment.dateEnrolled) } <br/>
				${ outcomeText } <br/>
			</fieldset>
		"""
	}
%>
<% if (currentEnrollment) { %>
	${ helper(currentEnrollment) }

<% } else { %>
	${ ui.includeFragment("uilibrary", "widget/button", [
			label: ui.format(program),
			classes: [ "padded "],
			extra: "Enroll",
			iconProvider: "kenyaemr",
			icon: "buttons/program_enroll.png",
			href: ui.pageLink("kenyaemr", "enterHtmlForm", [ patientId: patient.id, formUuid: registrationFormUuid, returnUrl: ui.thisUrl() ])
		]) }
<% } %>

<% pastEnrollments.each { %>
	${ helper(it) }
<% } %>
