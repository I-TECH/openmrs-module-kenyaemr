<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient, layout: "sidebar" ])

	def allowNew = !history.changes
	def allowChange = history.changes && history.changes.last().started.drugOrders
	def allowRestart = history.changes && !history.changes.last().started.drugOrders

	// Create HTML options for each ARV drug
	def drugOptions = arvs.collect {
			"""<option value="${ it.conceptId }">${ it.getPreferredName(Locale.ENGLISH) }</option>"""
	}.join()

	// Create HTML options for each adult ARV regimen
	def refDefIndex = 0;
	def regimenAdultOptions = regimenDefinitions.findAll({ it.pediatric == false }).collect { reg ->
		"""<option value="${ refDefIndex++ }">${ reg.name }</option>"""
	}.join()

	// Create HTML options for each child ARV regimen
	def regimenChildOptions = regimenDefinitions.findAll({ it.pediatric == true }).collect { reg ->
		"""<option value="${ refDefIndex++ }">${ reg.name }</option>"""
	}.join()

	// Create regimen form controls
	def arvStdRegSelect = { """<select class="standard-regimen-select"><option label="Select..." value="" /><optgroup label="Adult">${ regimenAdultOptions }</optgroup><optgroup label="Child">${ regimenChildOptions }</optgroup></select>""" }
	def arvDrugSelect = { """<select name="arv${ it }"><option value="" />${ drugOptions }</select>""" }
	def arvDoseInput = { """<input name="dosage${ it }" type="text" size="5" />""" }
	def arvUnitsSelect = { """<select name="units${ it }"><option value="mg">mg</option></select>""" }
	def arvFreqSelect = { """<select name="frequency${ it }"><option value="BD">BD</option><option value="OD">OD</option></select>""" }

	def arvFields = ui.decorate("uilibrary", "labeled", [ label: "Regimen" ], """
	    <div style="text-align: right">
			<i>Use standard:</i> ${ arvStdRegSelect() }
		</div>
		<br />
		Drug: ${ arvDrugSelect(1) } Dosage: ${ arvDoseInput(1) }${ arvUnitsSelect(1) } Frequency: ${ arvFreqSelect(1) }<br/>
		Drug: ${ arvDrugSelect(2) } Dosage: ${ arvDoseInput(2) }${ arvUnitsSelect(2) } Frequency: ${ arvFreqSelect(2) }<br/>
		Drug: ${ arvDrugSelect(3) } Dosage: ${ arvDoseInput(3) }${ arvUnitsSelect(3) } Frequency: ${ arvFreqSelect(3) }
	""")
%>

<style type="text/css">
	.start-new-regimen, .change-regimen {
		float: left;
	}
	
	.cancel-action-button {
		cursor: pointer;
		margin-left: 2em;
	}
</style>

<script type="text/javascript">

	var standardRegimens = [
		<% regimenDefinitions.each { regDef -> %>
		{ name: "${ regDef.name }", components: [ <% regDef.components.each { regComp -> %>{ conceptId: ${ regComp.conceptId }, dose: ${ regComp.dose } }, <% } %> ] },
		<% } %>
	];

	function showRegimenHistory(tbody, data) {
		if (!data || data.length === 0) {
			tbody.append('<tr><td colspan="4">None</td></tr>');
			return;
		}
		for (var i = 0; i < data.length; ++i) {
			var str = '<tr><td>' + data[i].startDate + '</td>';
			str += '<td>' + data[i].endDate + '</td>';
			str += '<td style="text-align: left">' + data[i].shortDisplay + '<br/><small>' + data[i].longDisplay + '</small></td>';
			str += '<td style="text-align: left">';
			if (data[i].changeReasons) {
				str += data[i].changeReasons.join(', ');
			}
			str += '</td></tr>';
			tbody.append(str);
		}
	}
	
	function refreshRegimenHistory(tbody, patientId) {
		jq.getJSON(ui.fragmentActionLink('kenyaemr', 'arvRegimen', 'regimenHistory', { patientId: patientId }), function(data) {
			showRegimenHistory(tbody, data);
		});
	}
	
	function choseAction(classChosen) {
		jq('#regimen-actions').hide();
		jq('.' + classChosen).show();
	}

	function cancelAction() {
		jq('fieldset').hide();
		jq('#regimen-actions').show();
	}
	
	jq(function() {
		showRegimenHistory(jq('#regimen-history > tbody'), ${ regimenHistoryJson });

		jq('.standard-regimen-select').change(function () {
			// Get selected regimen definition
			var stdRegIndex = parseInt(jq(this).val());
			var stdReg = standardRegimens[stdRegIndex];

			for (var c = 0; c < stdReg.components.length; c++) {
				var component = stdReg.components[c];

				jq(this).parent().parent().find('select[name=arv' + c + ']').val(component.conceptId);
				jq(this).parent().parent().find('input[name=dosage' + c + ']').val(component.dose);
			}

			// Reset select box back to 'Select...'
			jq(this).val('');
		});
	});
</script>

<div id="content-side">
	<div class="panel-frame">
		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
			iconProvider: "kenyaemr",
			icon: "buttons/back.png",
			label: "Back to Visit",
			href: ui.pageLink("kenyaemr", "medicalEncounterViewPatient", [ patientId: patient.id ])
		]) }
	</div>
</div>

<div id="content-main">

<div class="panel-frame">
	<div class="panel-heading">ARV Regimen History</div>
	<div class="panel-content">

	<table id="regimen-history" class="table-decorated table-vertical">
		<thead>
			<tr>
				<th>Start</th>
				<th>End</th>
				<th>Regimen</th>
				<th>Change Reason</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<br/>

	<div id="regimen-actions">
	<% if (allowNew) { %>
		${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_start.png", label: "Start", onClick: "choseAction('start-new-regimen')" ]) }
	<% } %>

	<% if (allowChange) { %>
		${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_change.png", label: "Change", onClick: "choseAction('change-regimen')" ]) }

		${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_stop.png", label: "Stop", onClick: "choseAction('stop-regimen')" ]) }
	<% } %>

	<% if (allowRestart) { %>
		${ ui.includeFragment("uilibrary", "widget/button", [ iconProvider: "kenyaemr", icon: "buttons/regimen_restart.png", label: "Restart", onClick: "choseAction('restart-regimen')" ]) }
	<% } %>
	</div>

	<% if (allowNew) { %>
	<fieldset class="start-new-regimen" style="display: none">
		<legend>Start ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "arvRegimen",
			action: "startRegimen",
			fields: [
				[ hiddenInputName: "patient", value: patient.id ],
				[ label: "Start Date", formFieldName: "startDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
				[ value: arvFields ]
			],
			submitLabel: "Save",
			successCallbacks: [ "ui.reloadPage();" ],
			cancelLabel: "Cancel",
			cancelFunction: "cancelAction"
		]) }
	</fieldset>

	<script type="text/javascript">
		jq(function() {
			ui.confirmBeforeNavigating('.start-new-regimen');
		});
	</script>
	<% } %>

	<% if (allowChange) { %>
	<fieldset class="change-regimen" style="display: none">
		<legend>Change ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "arvRegimen",
			action: "changeRegimen",
			fields: [
				[ hiddenInputName: "patient", value: patient.id ],
				[ label: "Change Date", formFieldName: "startDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
				[ value: arvFields ],
				[ label: "Reason for Change", formFieldName: "changeReason", class: java.lang.String ]
			],
			submitLabel: "Save",
			successCallbacks: [ "ui.reloadPage();" ],
			cancelLabel: "Cancel",
			cancelFunction: "cancelAction"
		]) }
	</fieldset>

	<fieldset class="stop-regimen" style="display: none">
		<legend>Stop ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "arvRegimen",
			action: "stopRegimen",
			fields: [
				[ hiddenInputName: "patient", value: patient.id ],
				[ label: "Stop Date", formFieldName: "stopDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
				[ label: "Reason for Stop", formFieldName: "stopReason", class: java.lang.String ]
			],
			submitLabel: "Save",
			successCallbacks: [ "ui.reloadPage();" ],
			cancelLabel: "Cancel",
			cancelFunction: "cancelAction"
		]) }
	</fieldset>

	<script type="text/javascript">
		jq(function() {
			ui.confirmBeforeNavigating('.change-regimen');
			ui.confirmBeforeNavigating('.stop-regimen');
		});
	</script>
	<% } %>

	<% if (allowRestart) { %>
	<fieldset class="restart-regimen" style="display: none">
		<legend>Restart ARVs</legend>

		${ ui.includeFragment("uilibrary", "widget/form", [
			fragmentProvider: "kenyaemr",
			fragment: "arvRegimen",
			action: "startRegimen",
			fields: [
				[ hiddenInputName: "patient", value: patient.id ],
				[ label: "Restart Date", formFieldName: "startDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
				[ value: arvFields ]
			],
			submitLabel: "Save",
			successCallbacks: [ "ui.reloadPage();" ],
			cancelLabel: "Cancel",
			cancelFunction: "cancelAction"
		]) }
	</fieldset>

	<script type="text/javascript">
		jq(function() {
			ui.confirmBeforeNavigating('.restart-regimen');
		});
	</script>
	<% } %>

</div>