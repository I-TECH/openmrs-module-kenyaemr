<%
	def onFormClick = { form ->
		def opts = [ appId: currentApp.id, patientId: currentPatient.id, formUuid: form.formUuid, returnUrl: ui.thisUrl() ]
		"""ui.navigate('${ ui.pageLink('kenyaemr', 'enterForm', opts) }');"""
	}

	def onEncounterClick = { encounter ->
		"""kenyaemr.openEncounterDialog('${ currentApp.id }', ${ encounter.id });"""
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
			${ ui.includeFragment("kenyaemr", "widget/encounterStack", [ encounters: page1Encounters, onEncounterClick: onEncounterClick ]) }
		</fieldset>
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">Page 2 (Initial and Followup Visits)</div>
	<div class="ke-panel-content" style="background-color: #F3F9FF">
		${ ui.includeFragment("kenyaemr", "widget/encounterStack", [ encounters: page2Encounters, onEncounterClick: onEncounterClick ]) }
		<br />
		<% if (inHivProgram) { %>
			<div align="center">
				${ ui.includeFragment("kenyaui", "widget/button", [
						label: "Add Visit Summary",
						extra: "From column",
						iconProvider: "kenyaui",
						icon: "buttons/visit_retrospective.png",
						href: ui.pageLink("kenyaemr", "enterForm", [ appId: currentApp.id, patientId: currentPatient, formUuid: page2Form.uuid, returnUrl: ui.thisUrl() ])
				]) }
			</div>
		<%}%>
		<% if (!(inHivProgram)) { %>
			<div class="ke-warning" align="center">
				You need to enroll the patient into HIV program before completing visit summary and regimen
			</div>
		<%}%>
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">ARV Regimen History</div>
	<div class="ke-panel-content" style="background-color: #F3F9FF">
		${ ui.includeFragment("kenyaemr", "regimenHistory", [ history: arvHistory ]) }
		<br />
		<% if (inHivProgram) { %>
			<div align="center">
				${ ui.includeFragment("kenyaui", "widget/button", [
						label: "Edit History",
						extra: "Go to editor",
						iconProvider: "kenyaui",
						icon: "buttons/regimen.png",
						classes: [ "padded" ],
						href: ui.pageLink("kenyaemr", "regimenEditor", [ appId: currentApp.id, patientId: currentPatient, category: "ARV", returnUrl: ui.thisUrl() ])
				]) }
			</div>
		<%}%>
	</div>

</div>