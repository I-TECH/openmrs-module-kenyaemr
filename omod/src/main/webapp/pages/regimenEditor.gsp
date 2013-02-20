<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient, layout: "sidebar" ])

	def allowNew = !history.changes
	def allowChange = history.changes && history.changes.last().started
	def allowRestart = history.changes && !history.changes.last().started
	def allowUndo = history.changes && history.changes.size() > 0
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
		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [ iconProvider: "kenyaemr", icon: "buttons/back.png", label: "Back", href: returnUrl ]) }
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
	${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_start.png", label: "Start", extra: "a new regimen", onClick: "choseAction('start-new-regimen')" ]) }
	<% } %>

	<% if (allowChange) { %>
	${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_change.png", label: "Change", extra: "the current regimen", onClick: "choseAction('change-regimen')" ]) }

	${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_stop.png", label: "Stop", extra: "the current regimen", onClick: "choseAction('stop-regimen')" ]) }
	<% } %>

	<% if (allowRestart) { %>
	${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_restart.png", label: "Restart", extra: "a new regimen", onClick: "choseAction('restart-regimen')" ]) }
	<% } %>

	<% if (allowUndo) { %>
	${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/undo.png", label: "Undo", extra: "the last change", onClick: "undoLastChange()" ]) }
	<% } %>
	</div>

	<% if (allowNew) { %>
	<fieldset id="start-new-regimen" class="regimen-action-form" style="display: none">
		<legend>Start ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "regimenUtil",
			action: "changeRegimen",
			fields: [
					[ hiddenInputName: "patient", value: patient.id ],
					[ hiddenInputName: "category", value: category ],
					[ hiddenInputName: "changeType", value: "start" ],
					[ label: "Start Date", formFieldName: "changeDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
					[ label: "Regimen", formFieldName: "regimen", class: "org.openmrs.module.kenyaemr.regimen.Regimen", fieldFragment: "field/Regimen", category: category ]
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
		<legend>Change ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "regimenUtil",
			action: "changeRegimen",
			fields: [
					[ hiddenInputName: "patient", value: patient.id ],
					[ hiddenInputName: "category", value: category ],
					[ hiddenInputName: "changeType", value: "change" ],
					[ hiddenInputName: "type", value: "CHANGE" ],
					[ label: "Change Date", formFieldName: "changeDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
					[ label: "Regimen", formFieldName: "regimen", class: "org.openmrs.module.kenyaemr.regimen.Regimen", fieldFragment: "field/Regimen", category: category ],
					[ label: "Reason for Change", formFieldName: "changeReason", class: java.lang.String ]
			],
			submitLabel: "Save",
			successCallbacks: [ "ui.reloadPage();" ],
			cancelLabel: "Cancel",
			cancelFunction: "cancelAction"
		]) }
	</fieldset>

	<fieldset id="stop-regimen" class="regimen-action-form" style="display: none">
		<legend>Stop ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "regimenUtil",
			action: "changeRegimen",
			fields: [
					[ hiddenInputName: "patient", value: patient.id ],
					[ hiddenInputName: "category", value: category ],
					[ hiddenInputName: "changeType", value: "stop" ],
					[ label: "Stop Date", formFieldName: "changeDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
					[ label: "Reason for Stop", formFieldName: "changeReason", class: java.lang.String ]
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
		<legend>Restart ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "regimenUtil",
			action: "changeRegimen",
			fields: [
					[ hiddenInputName: "patient", value: patient.id ],
					[ hiddenInputName: "category", value: category ],
					[ hiddenInputName: "changeType", value: "restart" ],
					[ label: "Restart Date", formFieldName: "changeDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
					[ label: "Regimen", formFieldName: "regimen", class: "org.openmrs.module.kenyaemr.regimen.Regimen", fieldFragment: "field/Regimen", category: category ]
			],
			submitLabel: "Save",
			successCallbacks: [ "ui.reloadPage();" ],
			cancelLabel: "Cancel",
			cancelFunction: "cancelAction"
		]) }
	</fieldset>
	<% } %>

</div>