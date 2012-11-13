<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient ])

	def allowNew = !history.changes
	def allowChange = history.changes && history.changes.last().started.drugOrders
	def allowRestart = history.changes && !history.changes.last().started.drugOrders
	
	def arvOptions = arvs.collect{
			"""<option value="${ it.conceptId }">${ it.getPreferredName(Locale.ENGLISH) }</option>"""
		}.join()
	def arvSelect = { """<select name="arv${ it }"><option value="">${ arvOptions }</select>""" }
	def arvFields = ui.decorate("uilibrary", "labeled", [label: "ARVs"], """
			${ arvSelect(1) }, dosage: <input type="text" size="5" name="dosage1"/><select name="units1"><option value="mg">mg</option></select> <select name="frequency1"><option value="BD">BD</option><option value="OD">OD</option></select> <br/>
			${ arvSelect(2) }, dosage: <input type="text" size="5" name="dosage2"/><select name="units2"><option value="mg">mg</option></select> <select name="frequency2"><option value="BD">BD</option><option value="OD">OD</option></select> <br/>
			${ arvSelect(3) }, dosage: <input type="text" size="5" name="dosage3"/><select name="units3"><option value="mg">mg</option></select> <select name="frequency3"><option value="BD">BD</option><option value="OD">OD</option></select>
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
	function showRegimenHistory(tbody, data) {
		if (!data || data.length === 0) {
			tbody.append('<tr><td colspan="4">None</td></tr>');
			return;
		}
		for (var i = 0; i < data.length; ++i) {
			var str = "<tr><td>" + data[i].startDate + "</td><td>" + data[i].endDate + "</td><td>" + data[i].shortDisplay + "<br/><span style=\"font-size: 0.7em\">" + data[i].longDisplay + "</span></td><td>";
			if (data[i].changeReasons) {
				str += data[i].changeReasons.join(", ");
			}
			str += "</td></tr>";
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
	
	jq(function() {
		showRegimenHistory(jq('#regimen-history > tbody'), ${ regimenHistoryJson });
		jq('.cancel-action-button').click(function() {
			jq(this).parents('fieldset').hide();
			jq('#regimen-actions').show();
		});
	});
</script>

<a href="${ ui.pageLink("kenyaemr", "medicalEncounterViewPatient", [ patientId: patient.id ]) }">Back to Visit</a>

<h3>ARV Regimen History</h3>

<table id="regimen-history" class="bordered">
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
		${ ui.includeFragment("uilibrary", "widget/button", [
				iconProvider: "uilibrary",
				icon: "add1-32.png",
				label: "Start",
				onClick: "choseAction('start-new-regimen')"
			]) }
	<% } %>
	
	<% if (allowChange) { %>
		${ ui.includeFragment("uilibrary", "widget/button", [
				iconProvider: "uilibrary",
				icon: "arrow_right_32.png",
				label: "Change",
				onClick: "choseAction('change-regimen')"
			]) }
			
		${ ui.includeFragment("uilibrary", "widget/button", [
				iconProvider: "uilibrary",
				icon: "close_32.png",
				label: "Stop",
				onClick: "choseAction('stop-regimen')"
			]) }
	<% } %>
	
	<% if (allowRestart) { %>
		${ ui.includeFragment("uilibrary", "widget/button", [
				iconProvider: "uilibrary",
				icon: "blue_arrow_right_32.png",
				label: "Restart",
				onClick: "choseAction('restart-regimen')"
			]) }
	<% } %>
</div>

<% if (allowNew) { %>
	<fieldset class="start-new-regimen" style="display: none">
		<legend>
			Start ARVs
			<img class="cancel-action-button" src="${ ui.resourceLink("uilibrary", "images/close_16.png") }" title="Cancel"/>
		</legend>
		${ ui.includeFragment("uilibrary", "widget/form", [
				fragmentProvider: "kenyaemr",
				fragment: "arvRegimen",
				action: "startRegimen",
				fields: [
					[ hiddenInputName: "patient", value: patient.id ],
					[ label: "Start Date", formFieldName: "startDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
					[ value: arvFields ]
				],
				submitLabel: "Start ARVs",
				successCallbacks: [ "ui.reloadPage();" ]
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
		<legend>
			Change ARVs
			<img class="cancel-action-button" src="${ ui.resourceLink("uilibrary", "images/close_16.png") }" title="Cancel"/>
		</legend>
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
				submitLabel: "Change ARV Regimen",
				successCallbacks: [ "ui.reloadPage();" ]
			]) }
	</fieldset>
	
	<fieldset class="stop-regimen" style="display: none">
		<legend>
			Stop ARVs
			<img class="cancel-action-button" src="${ ui.resourceLink("uilibrary", "images/close_16.png") }" title="Cancel"/>
		</legend>
		${ ui.includeFragment("uilibrary", "widget/form", [
				fragmentProvider: "kenyaemr",
				fragment: "arvRegimen",
				action: "stopRegimen",
				fields: [
					[ hiddenInputName: "patient", value: patient.id ],
					[ label: "Stop Date", formFieldName: "stopDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
					[ label: "Reason for Stop", formFieldName: "stopReason", class: java.lang.String ]
				],
				submitLabel: "Stop ARV Regimen",
				successCallbacks: [ "ui.reloadPage();" ]
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
		<legend>
			Restart ARVs
			<img class="cancel-action-button" src="${ ui.resourceLink("uilibrary", "images/close_16.png") }" title="Cancel"/>
		</legend>
		${ ui.includeFragment("uilibrary", "widget/form", [
				fragmentProvider: "kenyaemr",
				fragment: "arvRegimen",
				action: "startRegimen",
				fields: [
					[ hiddenInputName: "patient", value: patient.id ],
					[ label: "Restart Date", formFieldName: "startDate", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ],
					[ value: arvFields ]
				],
				submitLabel: "Restart ARVs",
				successCallbacks: [ "ui.reloadPage();" ]
			]) }
	</fieldset>
	
	<script type="text/javascript">
		jq(function() {
			ui.confirmBeforeNavigating('.restart-regimen');
		});
	</script>
<% } %>
