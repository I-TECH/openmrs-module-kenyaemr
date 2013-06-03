<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient ])
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="40%" valign="top">
			${ ui.includeFragment("kenyaemr", "patientSummary", [ patient: patient ]) }

			${ ui.includeFragment("kenyaemr", "programHistory", [
					patient: patient,
					program: hivProgram,
					enrollmentForm: Metadata.getForm(Metadata.HIV_PROGRAM_ENROLLMENT_FORM),
					discontinuationForm: Metadata.getForm(Metadata.HIV_PROGRAM_DISCONTINUATION_FORM),
					showClinicalData: true
			]) }

			${ ui.includeFragment("kenyaemr", "programHistory", [
					patient: patient,
					program: tbProgram,
					enrollmentForm: Metadata.getForm(Metadata.TB_ENROLLMENT_FORM),
					discontinuationForm: Metadata.getForm(Metadata.TB_COMPLETION_FORM),
					showClinicalData: true
			]) }
		</td>
		<td width="60%" valign="top" style="padding-left: 5px">
			${ ui.includeFragment("kenyaemr", "visitMenu", [ patient: patient, visit: visit, allowCheckIn: false, allowCheckOut: false ]) }

			<% if (visit) { %>
			${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: visit ]) }
			${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: visit ]) }
			<% } %>
		</td>
	</tr>
</table>

<% if (visit) { %>
${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
${ ui.includeFragment("kenyaemr", "dialogSupport") }
<% } %>