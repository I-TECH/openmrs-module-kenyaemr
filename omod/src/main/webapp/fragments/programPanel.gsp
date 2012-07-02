<%
	def helper = { enrollment ->
		def outcomeText
		if (enrollment.dateCompleted) {
			outcomeText = """Outcome: ${ ui.format(enrollment.outcome) } on ${ ui.format(enrollment.dateCompleted) }"""
		} else {
			outcomeText = "Active <br/>"
			if (exitFormUuid) {
				outcomeText += ui.includeFragment("widget/button", [
							label: "Discontinue Services",
							href: ui.pageLink("enterHtmlForm", [ patientId: patient.id, formUuid: exitFormUuid, returnUrl: ui.thisUrl() ])
						])
			}
		}
		def editHtml = ""
		if (registrationFormUuid) {
			editHtml = """<div class="edit-button">
							<a href="${ ui.pageLink("editProgramHtmlForm", [
									patientId: patient.id,
									patientProgramId: enrollment.id,
									formUuid: registrationFormUuid,
									returnUrl: ui.thisUrl()
								]) }">Edit</a>
						</div>"""
		}
			
		return """
			<fieldset class="editable">
				<legend>${ ui.format(program) }</legend>
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
	${ ui.includeFragment("widget/button", [
			label: ui.format(program),
			classes: [ "padded "],
			extra: "Enroll",
			iconProvider: "uilibrary",
			icon: "window_app_list_add_32.png",
			href: ui.pageLink("enterHtmlForm", [ patientId: patient.id, formUuid: registrationFormUuid, returnUrl: ui.thisUrl() ])
		]) }
<% } %>

<% pastEnrollments.each { %>
	${ helper(it) }
<% } %>
