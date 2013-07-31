<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient ])
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="40%" valign="top">
			${ ui.includeFragment("kenyaemr", "patientSummary", [ patient: patient ]) }
			${ ui.includeFragment("kenyaemr", "program/programHistories", [ patient: patient, showClinicalData: false ]) }
		</td>

		<td width="60%" valign="top" style="padding-left: 5px">
			${ ui.includeFragment("kenyaemr", "visitMenu", [ patient: patient, visit: visit, allowCheckIn: true, allowCheckOut: true ]) }

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