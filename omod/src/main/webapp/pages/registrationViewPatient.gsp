<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient ])

	ui.includeCss("kenyaemr", "kenyaemr.css");
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="40%" valign="top">
			${ ui.includeFragment("kenyaemr", "patientSummary", [ patient: patient ]) }

			${ ui.includeFragment("kenyaemr", "medicalEncounterProgram", [
				patient: patient,
				program: hivProgram,
				registrationFormUuid: MetadataConstants.HIV_PROGRAM_ENROLLMENT_FORM_UUID,
				exitFormUuid: MetadataConstants.HIV_PROGRAM_DISCONTINUATION_FORM_UUID
			]) }

			${ ui.includeFragment("kenyaemr", "medicalEncounterProgram", [
				patient: patient,
				program: tbProgram,
				registrationFormUuid: MetadataConstants.TB_ENROLLMENT_FORM_UUID,
				exitFormUuid: MetadataConstants.TB_COMPLETION_FORM_UUID
			]) }
		</td>

		<td width="60%" valign="top" style="padding-left: 5px">
		<% if (visit) { %>
			${ ui.includeFragment("kenyaemr", "visitSummary", [ visit: visit, showEndVisitButton: !visit.stopDatetime ]) }
			${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: visit ]) }
			${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: visit ]) }
		<% } else {
			// do this here to avoid annoying template engine issue
			def jsSuccess = "location.href = ui.pageLink('kenyaemr', 'registrationViewPatient', " + "{" + "patientId: ${ patient.id }, visitId: data.visitId" + "});"
		%>

			<div class="panel-frame" style="text-align: right">
				<%= ui.includeFragment("uilibrary", "widget/popupForm", [
					id: "check-in-form",
					buttonConfig: [
						iconProvider: "kenyaemr",
						icon: "buttons/registration.png",
						label: "Check In For Visit",
						classes: [ "padded" ],
						extra: "Patient is Here"
					],
					popupTitle: "Check In For Visit",
					prefix: "visit",
					commandObject: newCurrentVisit,
					hiddenProperties: [ "patient" ],
					properties: [ "visitType", "startDatetime" ],
					fieldConfig: [
						"visitType": [ label: "Visit Type" ]
					],
					propConfig: [
						"visitType": [ type: "radio" ],
					],
					fieldConfig: [
						"startDatetime": [ fieldFragment: "field/java.util.Date.datetime" ]
					],
					fragment: "registrationUtil",
					fragmentProvider: "kenyaemr",
					action: "startVisit",
					successCallbacks: [ jsSuccess ],
					submitLabel: ui.message("general.submit"),
					cancelLabel: ui.message("general.cancel"),
					submitLoadingMessage: "Checking In"
					]) %>
			</div>
		<% } %>

		</td>
	</tr>
</table>

<% if (visit) { %>
	${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	${ ui.includeFragment("kenyaemr", "dialogSupport") }
<% } %>