<%
	config.require("presets")

	def presetOptions = config.presets.collect({
		"""<option value="${ kenyaui.formatDate(it.value) }">${ it.label }</option>"""
	}).join()
%>
${ ui.includeFragment("kenyaui", "field/java.util.Date", config) }

<script type="text/javascript">
	jq(function() {
		jq('#${ config.id }-presets').change(function() {
			var parsedDate = jq.datepicker.parseDate('dd-M-yy', jq(this).val());
			jq('#${ config.id }').datepicker('setDate', parsedDate);
			jq(this).val('');
		});
	});
</script>

<% if (config.presets) { %>
<i>Use preset: </i>
<select id="${ config.id }-presets">
	<option value="">Select...</option>
	${ presetOptions }
</select>
<% } %>