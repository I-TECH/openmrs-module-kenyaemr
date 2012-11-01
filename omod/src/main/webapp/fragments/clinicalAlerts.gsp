<style type="text/css">
#selected-patient-alerts {
	width: 50%;
	float: left;
	overflow: auto;
	text-align: left;
}

#selected-patient-alerts > .alert {
	color: #332;
	background-color: yellow;
	margin-right: 3px;
	padding: 2px;
	border-radius: 2px;
	display: inline-block;
}
</style>

<div id="selected-patient-alerts" style="display: none"></div>

<script type="text/javascript">
jq(function() {
	ui.getFragmentActionAsJson('kenyaemr', 'clinicalAlerts', 'getAlerts', { patientId: ${ patient.id } }, function(result) {
		if (result) {
			var html = jq.map(result, function(alertText) {
				return '<span class="alert">' + alertText.shortMessage + '</span>';
			}).join('');
			jq('#selected-patient-alerts').append(html).show();
		}
	});
});
</script>
