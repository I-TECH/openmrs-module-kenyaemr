<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, layout: "sidebar" ])

	def allowNew = !regimenFromObs
	def allowChange = regimenFromObs && lastEnc.startDate
	def allowRestart = regimenFromObs && lastEnc.endDate
	def allowUndo = regimenFromObs && regimenFromObs.size() > 0
	def isManager = isManager


	def changeDateField = { label ->
		[ label: label, formFieldName: "changeDate", class: java.util.Date, showTime: true, initialValue: null ]
	}

	def regimenField = {
		[ label: "Regimen", formFieldName: "regimen", class: "org.openmrs.module.kenyaemr.regimen.Regimen", fieldFragment: "field/Regimen", category: category ]
	}

	def reasonFields = { reasonType ->
		ui.includeFragment("kenyaui", "widget/rowOfFields", [
			fields: [
				[ label: "Reason", formFieldName: "changeReason", class: "org.openmrs.Concept", fieldFragment: "field/RegimenChangeReason", category: category, reasonType: reasonType ],
				[ label: "Reason (Other)", formFieldName: "changeReasonNonCoded", class: java.lang.String ]
			]
		]);
	}
%>

<script type="text/javascript">

	function choseAction(formId) {
		// Hide the regimen action buttons
		jq('#regimen-action-buttons').hide();

		ui.confirmBeforeNavigating('#' + formId);

		// Show the relevant regimen action form
		jq('#' + formId).show();
	}

	function cancelAction() {
		ui.cancelConfirmBeforeNavigating('.regimen-action-form');

		// Hide and clear all regimen action forms
		jq('.regimen-action-form').hide();
		jq('.regimen-action-form form').get(0).reset();

		// Redisplay the regimen action buttons
		jq('#regimen-action-buttons').show();
	}

	function undoLastChange() {
		if (confirm('Undo the last regimen change?')) {
			ui.getFragmentActionAsJson('kenyaemr', 'regimenUtil', 'undoLastChange', { patient: ${ currentPatient.patientId }, category: '${ category }' }, function (data) {
				ui.reloadPage();
			});
		}
	}
</script>

<div class="ke-page-sidebar">
	<div class="ke-panel-frame">
		${ ui.includeFragment("kenyaui", "widget/panelMenuItem", [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]) }
	</div>
</div>

<div class="ke-page-content">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">${ category } Regimen History</div>
		<div class="ke-panel-content">
			<% if(!isManager) { %>
				<div class="ke-warning" style="margin-bottom: 5px">Editing of current regimen can be done by a KenyaEMR user with Manager permissions</div>
			<% } %>

			${ ui.includeFragment("kenyaemr", "regimenHistory", [ history: regimenFromObs ]) }

			<br/>

			<div id="regimen-action-buttons" style="text-align: center">
			<% if (allowNew) { %>
			${ ui.includeFragment("kenyaui", "widget/button", [ iconProvider: "kenyaui", icon: "buttons/regimen_start.png", label: "Start", extra: "a new regimen", onClick: "choseAction('start-new-regimen')" ]) }
			<% } %>

			<% if (allowChange) { %>
			${ ui.includeFragment("kenyaui", "widget/button", [ iconProvider: "kenyaui", icon: "buttons/regimen_change.png", label: "Change", extra: "the current regimen", onClick: "choseAction('change-regimen')" ]) }

			${ ui.includeFragment("kenyaui", "widget/button", [ iconProvider: "kenyaui", icon: "buttons/regimen_stop.png", label: "Stop", extra: "the current regimen", onClick: "choseAction('stop-regimen')" ]) }
			<% } %>

			<% if (allowRestart) { %>
			${ ui.includeFragment("kenyaui", "widget/button", [ iconProvider: "kenyaui", icon: "buttons/regimen_restart.png", label: "Restart", extra: "a new regimen", onClick: "choseAction('restart-regimen')" ]) }
			<% } %>

			<% if (allowUndo) { %>
			${ ui.includeFragment("kenyaui", "widget/button", [ iconProvider: "kenyaui", icon: "buttons/undo.png", label: "Undo", extra: "the last change", onClick: "undoLastChange()" ]) }
			<% } %>
			</div>

			<% if (allowNew) { %>
			<fieldset id="start-new-regimen" class="regimen-action-form" style="display: none">
				<legend>Start New Regimen</legend>

				${ ui.includeFragment("kenyaui", "widget/form", [
					fragmentProvider: "kenyaemr",
					fragment: "regimenUtil",
					action: "createRegimenEventEncounter",
					fields: [
							[ hiddenInputName: "patient", value: currentPatient.id ],
							[ hiddenInputName: "changeType", value: "START" ],
							[ hiddenInputName: "category", value: category ],
							changeDateField("Start date"),
							regimenField()
					],
					submitLabel: "Save",
					successCallbacks: [ "ui.reloadPage();" ],
					cancelLabel: "Cancel",
					cancelFunction: "cancelAction"
				]) }
			</fieldset>
			<% } %>

			<% if (allowChange) { %>
			<fieldset id="change-regimen" class="regimen-action-form" style="display: none">
				<legend>Change Regimen</legend>

				${ ui.includeFragment("kenyaui", "widget/form", [
					fragmentProvider: "kenyaemr",
					fragment: "regimenUtil",
					action: "createRegimenEventEncounter",
					fields: [
							[ hiddenInputName: "patient", value: currentPatient.id ],
							[ hiddenInputName: "changeType", value: "CHANGE" ],
							[ hiddenInputName: "category", value: category ],
							changeDateField("Change date"),
							regimenField(),
							[ value: reasonFields("change") ]
					],
					submitLabel: "Save",
					successCallbacks: [ "ui.reloadPage();" ],
					cancelLabel: "Cancel",
					cancelFunction: "cancelAction"
				]) }
			</fieldset>

			<fieldset id="stop-regimen" class="regimen-action-form" style="display: none">
				<legend>Stop Regimen</legend>

				${ ui.includeFragment("kenyaui", "widget/form", [
					fragmentProvider: "kenyaemr",
					fragment: "regimenUtil",
					action: "createRegimenEventEncounter",
					fields: [
							[ hiddenInputName: "patient", value: currentPatient.id ],
							[ hiddenInputName: "changeType", value: "STOP" ],
							[ hiddenInputName: "category", value: category ],
							changeDateField("Stop date"),
							[ value: reasonFields("stop") ]
					],
					submitLabel: "Save",
					successCallbacks: [ "ui.reloadPage();" ],
					cancelLabel: "Cancel",
					cancelFunction: "cancelAction"
				]) }
			</fieldset>
			<% } %>

			<% if (allowRestart) { %>
			<fieldset id="restart-regimen" class="regimen-action-form" style="display: none">
				<legend>Restart Regimen</legend>

				${ ui.includeFragment("kenyaui", "widget/form", [
					fragmentProvider: "kenyaemr",
					fragment: "regimenUtil",
					action: "createRegimenEventEncounter",
					fields: [
							[ hiddenInputName: "patient", value: currentPatient.id ],
							[ hiddenInputName: "changeType", value: "RESTART" ],
							[ hiddenInputName: "category", value: category ],
							changeDateField("Restart date"),
							regimenField()
					],
					submitLabel: "Save",
					successCallbacks: [ "ui.reloadPage();" ],
					cancelLabel: "Cancel",
					cancelFunction: "cancelAction"
				]) }
			</fieldset>
			<% } %>
		</div>
	</div>
</div>