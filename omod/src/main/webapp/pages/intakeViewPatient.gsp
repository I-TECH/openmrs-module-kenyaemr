<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient ])
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
			${ ui.includeFragment("kenyaemr", "visitSummary", [ visit: visit ]) }
			${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: visit ]) }
			${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: visit ]) }
		<% } else { %>
			<div class="panel-frame" style="text-align: right">
				${ ui.includeFragment("uilibrary", "widget/button", [
					iconProvider: "kenyaemr",
					icon: "buttons/registration.png",
					label: "Go to Registration",
					classes: [ "padded" ],
					extra: "to Check In",
					href: ui.pageLink("kenyaemr", "registrationViewPatient", [ patientId: patient.id ])
				]) }
			</div>
		<% } %>
		</td>
	</tr>
</table>

<% if (visit) { %>
${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
${ ui.includeFragment("kenyaemr", "dialogSupport") }
<% } %>