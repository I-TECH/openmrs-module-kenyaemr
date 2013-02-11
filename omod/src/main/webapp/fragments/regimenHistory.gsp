<%
	config.require("patient")
%>
<script type="text/javascript">

	function showRegimenHistory(tbody, data) {
		if (!data || data.length === 0) {
			tbody.append('<tr><td colspan="4">None</td></tr>');
			return;
		}
		for (var i = 0; i < data.length; ++i) {
			var str = '<tr><td>' + data[i].startDate + '</td>';
			str += '<td>' + data[i].endDate + '</td>';
			str += '<td style="text-align: left">' + data[i].regimen.shortDisplay + '<br/><small>' + data[i].regimen.longDisplay + '</small></td>';
			str += '<td style="text-align: left">';
			if (data[i].changeReasons) {
				str += data[i].changeReasons.join(', ');
			}
			str += '</td></tr>';
			tbody.append(str);
		}
	}
	
	function refreshRegimenHistory(patientId) {
		jq.getJSON(ui.fragmentActionLink('kenyaemr', 'arvRegimen', 'regimenHistory', { patientId: patientId }), function(data) {
			showRegimenHistory(jq('table#regimen-history > tbody'), data);
		});
	}

	jq(function() {
		refreshRegimenHistory(${ patient.id });
	});
</script>
<table id="regimen-history" class="table-decorated table-vertical">
	<thead>
		<tr>
			<th>Start</th>
			<th>End</th>
			<th>Regimen</th>
			<th>Change Reason</th>
		</tr>
	</thead>
	<tbody></tbody>
</table>