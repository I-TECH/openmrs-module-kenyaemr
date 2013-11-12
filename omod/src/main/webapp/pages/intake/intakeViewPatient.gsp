<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient ])
%>

<div class="ke-page-content">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="40%" valign="top">
				${ ui.includeFragment("kenyaemr", "patient/patientSummary", [ patient: currentPatient ]) }
				${ ui.includeFragment("kenyaemr", "patient/patientRelationships", [ patient: currentPatient ]) }
				${ ui.includeFragment("kenyaemr", "program/programHistories", [ patient: currentPatient, showClinicalData: true ]) }
			</td>
			<td width="60%" valign="top" style="padding-left: 5px">
				${ ui.includeFragment("kenyaemr", "visitMenu", [ patient: currentPatient, visit: activeVisit, allowCheckIn: false, allowCheckOut: false ]) }

				<% if (activeVisit) { %>
				${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: activeVisit ]) }
				${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: activeVisit ]) }
				<% } %>
			</td>
		</tr>
	</table>
</div>

${ ui.includeFragment("kenyaemr", "form/showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
${ ui.includeFragment("kenyaemr", "dialogSupport") }