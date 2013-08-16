<div id="patient-flags-placeholder" style="display: none"></div>

<script type="text/javascript">
jq(function() {
	ui.getFragmentActionAsJson('kenyaemr', 'patient/patientUtils', 'flags', { patientId: ${ config.patient.id } }, function(result) {
		if (result) {
			var html = jq.map(result, function(alert) {
				return '<span class="ke-flagtag">' + alert.message + '</span>';
			}).join(' ');
			jq('#patient-flags-placeholder').append(html).show();
		}
	});
});
</script>