<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient, layout: "sidebar" ])

	def allowNew = !history.changes
	def allowChange = history.changes && history.changes.last().started
	def allowRestart = history.changes && !history.changes.last().started
	def allowUndo = history.changes && history.changes.size() > 0

	def changeDateField = { label ->
		[ label: label, formFieldName: "changeDate", class: java.util.Date, initialValue: initialDate, fieldFragment: "field/java.util.Date.withpresets", presets: datePresets ]
	}

	def regimenField = {
		[ label: "Regimen", formFieldName: "regimen", class: "org.openmrs.module.kenyaemr.regimen.Regimen", fieldFragment: "field/Regimen", category: category ]
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
			ui.getFragmentActionAsJson('kenyaemr', 'regimenUtil', 'undoLastChange', { patient: ${ patient.patientId }, category: '${ category }' }, function (data) {
				ui.reloadPage();
			});
		}
	}
</script>

<div id="content-side">
	<div class="panel-frame">
		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]) }
	</div>
</div>

<div id="content-main">
	<div class="panel-frame">
		<div class="panel-heading">${ category } Regimen History</div>
		<div class="panel-content">

			${ ui.includeFragment("kenyaemr", "regimenHistory", [ history: history ]) }

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
					action: "changeRegimen",
					fields: [
							[ hiddenInputName: "patient", value: patient.id ],
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
					action: "changeRegimen",
					fields: [
							[ hiddenInputName: "patient", value: patient.id ],
							[ hiddenInputName: "changeType", value: "CHANGE" ],
							[ hiddenInputName: "category", value: category ],
							changeDateField("Change date"),
							regimenField(),
							[ label: "Reason for change", formFieldName: "changeReason", class: java.lang.String ]
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
					action: "changeRegimen",
					fields: [
							[ hiddenInputName: "patient", value: patient.id ],
							[ hiddenInputName: "changeType", value: "STOP" ],
							[ hiddenInputName: "category", value: category ],
							changeDateField("Stop date"),
							[ label: "Reason for stop", formFieldName: "changeReason", class: java.lang.String ]
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
					action: "changeRegimen",
					fields: [
							[ hiddenInputName: "patient", value: patient.id ],
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