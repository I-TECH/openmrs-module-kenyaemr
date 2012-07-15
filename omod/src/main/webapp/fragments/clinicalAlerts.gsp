<style>
	#alerts {
		margin: 0.5em;
	}
	.alert {
		color: black;
		background-color: yellow;
		border: 1px black solid;
		margin-right: 0.5em;
		padding: 0.2em;
	}
</style>

<div id="alerts" style="display: none"></div>

<script>
jq(function() {
	ui.getFragmentActionAsJson('clinicalAlerts', 'getAlerts', { patientId: ${ patient.id } }, function(result) {
		if (result) {
			var html = jq.map(result, function(alertText) {
				return '<span class="alert">' + alertText.shortMessage + '</span>';
			}).join('');
			jq('#alerts').append(html).show();
		}
	});
});
</script>
