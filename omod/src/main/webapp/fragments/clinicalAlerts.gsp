<div style="width: 50%; float: left; overflow: auto; text-align: left">
	<div id="patient-alerts-placeholder" style="display: none"></div>
</div>

<script type="text/javascript">
jq(function() {
	ui.getFragmentActionAsJson('kenyaemr', 'clinicalAlerts', 'getAlerts', { patientId: ${ patient.id } }, function(result) {
		if (result) {
			var html = jq.map(result, function(alertText) {
				return '<span class="ke-tag ke-alerttag">' + alertText.singlePatientMessage + '</span>';
			}).join(' ');
			jq('#patient-alerts-placeholder').append(html).show();
		}
	});
});
</script>
