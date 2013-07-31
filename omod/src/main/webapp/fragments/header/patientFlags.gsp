<div style="width: 50%; float: left; overflow: auto; text-align: left">
	<div id="patient-flags-placeholder" style="display: none"></div>
</div>

<script type="text/javascript">
jq(function() {
	ui.getFragmentActionAsJson('kenyaemr', 'patient/patientUtils', 'flags', { patientId: ${ patient.id } }, function(result) {
		if (result) {
			var html = jq.map(result, function(alert) {
				return '<span class="ke-tag ke-flagtag">' + alert.message + '</span>';
			}).join(' ');
			jq('#patient-flags-placeholder').append(html).show();
		}
	});
});
</script>