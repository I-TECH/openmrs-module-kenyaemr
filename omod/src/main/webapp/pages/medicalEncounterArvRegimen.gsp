<%
	ui.decorateWith("standardKenyaEmrPage", [ patient: patient ])

	def allowNew = !history.changes
	def allowChange = history.changes
	
	def arvOptions = arvs.collect{
			"""<option value="${ it.conceptId }">${ it.getPreferredName(Locale.ENGLISH) }</option>"""
		}.join()
	def arvSelect = { """<select name="arv${ it }"><option value="">${ arvOptions }</select>""" }
	def arvFields = ui.decorate("labeled", [label: "ARVs"], """
			${ arvSelect(1) }, dosage: <input type="text" size="5" name="dosage1"/><select name="units1"><option value="mg">mg</option></select><br/>
			${ arvSelect(2) }, dosage: <input type="text" size="5" name="dosage2"/><select name="units2"><option value="mg">mg</option></select> <br/>
			${ arvSelect(3) }, dosage: <input type="text" size="5" name="dosage3"/><select name="units3"><option value="mg">mg</option></select>
		""")
%>

<style>
	.start-new-regimen, .change-regimen {
		float: left;
	}
</style>

<script>
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
		jq.getJSON(ui.fragmentActionLink('arvRegimen', 'regimenHistory', { patientId: patientId }), function(data) {
			showRegimenHistory(tbody, data);
		});
	}
	
	jq(function() {
		showRegimenHistory(jq('#regimen-history > tbody'), ${ regimenHistoryJson });
	});
</script>

<a href="${ ui.pageLink("medicalEncounterViewPatient", [ patientId: patient.id ]) }">Back to Visit</a>

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

<% if (allowNew) { %>
	<fieldset class="start-new-regimen">
		<legend>Start ARVs</legend>
		${ ui.includeFragment("widget/form", [
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
<% } %>

<% if (allowChange) { %>
	<fieldset class="change-regimen">
		<legend>Change ARVs</legend>
		${ ui.includeFragment("widget/form", [
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
<% } %>