<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient ])

	def onEncounterClick = { encounter ->
		"""kenyaemr.openEncounterDialog('${ currentApp.id }', ${ encounter.id });"""
	}
%>

<div class="ke-page-content">

	${ /*ui.includeFragment("kenyaui", "widget/tabMenu", [ items: [
			[ label: "Overview", tabid: "overview" ],
			[ label: "Lab Tests", tabid: "labtests" ],
			[ label: "Prescriptions", tabid: "prescriptions" ]
	] ])*/ "" }

	<!--<div class="ke-tab" data-tabid="overview">-->
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td width="30%" valign="top">
					${ ui.includeFragment("kenyaemr", "patient/patientSummary", [ patient: currentPatient ]) }
					${ ui.includeFragment("kenyaemr", "patient/patientRelationships", [ patient: currentPatient ]) }
				</td>
				<td width="70%" valign="top" style="padding-left: 5px">
					<div class="ke-panel-frame">
						<div class="ke-panel-heading">Tracing History</div>
						<div class="ke-panel-content" style="background-color: #F3F9FF">

							<div align="center">
								${ ui.includeFragment("kenyaui", "widget/button", [
										label: "Add Tracing information",
										extra: "",
										iconProvider: "kenyaui",
										icon: "buttons/visit_retrospective.png",
										href: ui.pageLink("kenyaemr", "enterForm", [ appId: currentApp.id, patientId: currentPatient, formUuid: formUuid, returnUrl: ui.thisUrl() ])
								]) }
							</div>
							<br />
							${ ui.includeFragment("kenyaemr", "widget/encounterStack", [ encounters: pastEncounters, onEncounterClick: onEncounterClick ]) }




						</div>
					</div>
				</td>
			</tr>
		</table>
	<!--</div>-->

</div>