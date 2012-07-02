<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<% if (data) { %>

	${ ui.includeFragment("showReportOutput", [ definition: definition, data: data ]) }

<% } else { %>

	<h2>${ definition.name }</h2>

	<form method="POST">
		Period:
		${ ui.includeFragment("widget/selectList", [
				formFieldName: "startDate",
				options: startDateOptions,
				optionsValueField: "key",
				optionsDisplayField: "value"
			]) }
			
		<br/>
		<br/>
		<input type="submit" class="button" value="Generate Report"/>
	
	</form>
	
	<script>
	jq(function() {
		jq('.button').button();
	});
	</script>

<% } %>