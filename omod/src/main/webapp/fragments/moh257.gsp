<%
	def onFormClick = { form ->
		def visitId = visit ? visit.id : null
		def opts = [ appId: currentApp.id, patientId: patient.id, formUuid: form.formUuid, returnUrl: ui.thisUrl() ]
		"""location.href = '${ ui.pageLink('kenyaemr', 'enterHtmlForm', opts) }';"""
	}
%>
<div class="ke-panel-frame">
	<div class="ke-panel-heading">Page 1 (Care Summary)</div>
	<div class="ke-panel-content" style="background-color: #F3F9FF">

		<fieldset>
			<legend>New Forms</legend>

			${ ui.includeFragment("kenyaui", "widget/formStack", [ forms: page1AvailableForms, onFormClick: onFormClick ]) }
		</fieldset>
		<br />
		<fieldset>
			<legend>Previously Completed Forms</legend>
			<%
				if (page1Encounters && page1Encounters.size > 0) {
					page1Encounters.each {
						println ui.includeFragment("kenyaemr", "encounterStackItem", [ encounter: it ])
					}
				} else {
					println "<i>None</i>"
				}
			%>
		</fieldset>
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">Page 2 (Initial and Followup Visits)</div>
	<div class="ke-panel-content" style="background-color: #F3F9FF">
		<%
			if (page2Encounters && page2Encounters.size > 0) {
				page2Encounters.each {
					println ui.includeFragment("kenyaemr", "encounterStackItem", [ encounter: it, showEncounterDate: true ])
				}
			} else {
				println "<i>None</i>"
			}
		%>
		<br />
		<div align="center">
			${ ui.includeFragment("kenyaui", "widget/button", [
					label: "Add Visit Summary",
					extra: "From column",
					iconProvider: "kenyaui",
					icon: "buttons/visit_retrospective.png",
					href: ui.pageLink("kenyaemr", "enterHtmlForm", [ appId: currentApp.id, patientId: patient, formUuid: page2Form.uuid, returnUrl: ui.thisUrl() ])
			]) }
		</div>
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">ARV Regimen History</div>
	<div class="ke-panel-content" style="background-color: #F3F9FF">
		${ ui.includeFragment("kenyaemr", "regimenHistory", [ history: arvHistory ]) }

		<br />
		<div align="center">
			${ ui.includeFragment("kenyaui", "widget/button", [
					label: "Edit History",
					extra: "Go to editor",
					iconProvider: "kenyaui",
					icon: "buttons/regimen.png",
					classes: [ "padded" ],
					href: ui.pageLink("kenyaemr", "regimenEditor", [ appId: currentApp.id, patientId: patient, category: "ARV", returnUrl: ui.thisUrl() ])
			]) }
		</div>
	</div>
</div>