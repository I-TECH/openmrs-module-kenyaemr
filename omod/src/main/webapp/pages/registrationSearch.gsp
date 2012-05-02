<%
	ui.decorateWith("standardAppPage")
%>
<style>
	#search {
		float: left;
	}
	
	#results {
		float: left;
	}

	.panel {
		background-color: #e0e0e0;
		margin-bottom: 0.5em;
	}	
	.panel .name {
		font-weight: bold;
		font-size: 1.5em;
	}
	.panel .demographics {
	}
	.panel .identifiers {
		float: right;
		padding-left: 2em;
	}
</style>

<fieldset id="search">
	<legend>
		Find a Patient
	</legend>
	
	${ ui.includeFragment("widget/form", [
			id: "patientSearch",
			fields: [
				[ label: "ID or Name", formFieldName: "q", class: java.lang.String ],
				[ label: "Age", formFieldName: "age", class: java.lang.Integer ]
			],
			fragment: "patientSearch",
			action: "search",
			submitOnEvent: "patientSearch/changed",
			successEvent: "patientSearch/results"
		] )}
</fieldset>

<div id="results">
</div>

<script>
	subscribe("patientSearch/results", function(event, data) {
		jq('#results').html('');
		for (var i = 0; i < data.length; ++i) {
			var pt = data[i]
			var html = '<div class="panel">';
			html += '<span class="demographics">';
			html += '<span class="name">' + pt.personName + '</span><br/>';
			html += '<span class="age">' + pt.age + ' year old </span>';
			html += '<span class="gender">' + pt.gender + '</span>';
			html += '</span>';
			html += '<span class="identifiers">' + pt.patientIdentifier + '</span>';
			html += '</div>';
			jq(html).appendTo(jq('#results'));
		}
	});
</script>