<%
	def startFieldName = config.startFieldName ?: "param[startDate]";
	def endFieldName = config.endFieldName ?: "param[endDate]";
%>
<script type="text/javascript">
	jQuery(function() {

	});
</script>

<div>
	<b>Start Date: </b> ${ ui.includeFragment("kenyaui", "field/java.util.Date", [ id: "startDate", formFieldName: startFieldName]) }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b>End Date: </b> ${ ui.includeFragment("kenyaui", "field/java.util.Date", [ id: "endDate", formFieldName: endFieldName]) }
</div>

<span id="${ config.id }-error" class="error" style="display: none"></span>

<input type="hidden" id="date_value" />
