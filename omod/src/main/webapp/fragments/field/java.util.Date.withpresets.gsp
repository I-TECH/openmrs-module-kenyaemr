<%
	config.require("presets")

	def presetOptions = config.presets.collect({
		"""<option value="${ kenyaEmrUi.formatDate(it.value) }">${ it.label }</option>"""
	}).join()
%>
${ ui.includeFragment("uilibrary", "field/java.util.Date", config) }

<script type="text/javascript">
	jq(function() {
		jq('#${ config.id }-presets').change(function() {
			var parsedDate = jq.datepicker.parseDate('dd-M-yy', jq(this).val());
			jq('#${ config.id }').datepicker('setDate', parsedDate);
			jq(this).val('');
		});
	});
</script>

<i>Use preset: </i>
<select id="${ config.id }-presets">
	<option value="">Select...</option>
	${ presetOptions }
</select>