<style type="text/css">
#selected-patient-alerts {
	width: 50%;
	float: left;
	overflow: auto;
	text-align: left;
}
</style>

<div id="selected-patient-alerts" style="display: none"></div>

<script type="text/javascript">
jq(function() {
	ui.getFragmentActionAsJson('kenyaemr', 'clinicalAlerts', 'getAlerts', { patientId: ${ patient.id } }, function(result) {
		if (result) {
			var html = jq.map(result, function(alertText) {
				return '<span class="patient-alert">' + alertText.singlePatientMessage + '</span>';
			}).join('');
			jq('#selected-patient-alerts').append(html).show();
		}
	});
});
</script>
