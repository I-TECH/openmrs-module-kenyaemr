<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient ])

	def onEncounterClick = { encounter ->
		"""kenyaemr.openEncounterDialog('${ currentApp.id }', ${ encounter.id });"""
	}
%>

<div class="ke-page-content">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td width="30%" valign="top">
					${ ui.includeFragment("kenyaemr", "patient/patientSummary", [ patient: currentPatient ]) }
					${ ui.includeFragment("kenyaemr", "patient/patientRelationships", [ patient: currentPatient ]) }
					${ ui.includeFragment("kenyaemr", "program/programHistories", [ patient: currentPatient, showClinicalData: true ]) }
					%{--${ ui.includeFragment("prep", "program/programHistories", [ patient: currentPatient, showClinicalData: true ]) }--}%
				</td>
				<td width="55%" valign="top" style="padding-left: 5px">
					${ ui.includeFragment("kenyaemr", "visitMenu", [ patient: currentPatient, visit: activeVisit ]) }

					<% if (activeVisit) { %>
					${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: activeVisit ]) }
					${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: activeVisit ]) }
					<% } %>
				</td>
				<td width="15%" valign="top" style="padding-left: 5px">
					${ ui.includeFragment("kenyaemr", "providerAction/providerActions") }
				</td>
			</tr>
		</table>

</div>