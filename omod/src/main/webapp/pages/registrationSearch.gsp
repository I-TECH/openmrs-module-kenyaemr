<%
	ui.decorateWith("standardAppPage")
%>
<style>
	#create-patient {
		position: fixed;
		bottom: 0;
		left: 40%;
	}
	
	#search {
		float: left;
	}
	
	#results {
	}

	.panel {
		cursor: pointer;
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
			resetOnSubmit: false,
			successEvent: "patientSearch/results"
		] )}
</fieldset>

<fieldset>
	<legend>Results</legend>
	<div id="results">
	</div>
</fieldset>

${ ui.includeFragment("widget/button", [
	id: "create-patient",
	iconProvider: "uilibrary",
	icon: "add1-32.png",
	label: "Create New Patient Record",
	extra: "Patient does not exist yet",
	href: ui.pageLink("registrationCreatePatient") ]) }


<script>
	subscribe("patientSearch/results", function(event, data) {
		jq('#results').html('');
		for (var i = 0; i < data.length; ++i) {
			var pt = data[i]
			var html = '<div class="panel">';
			html += '<input type="hidden" class="patientId" value="' + pt.patientId + '"/>';
			html += '<span class="demographics">';
			html += '<span class="name">' + pt.personName + '</span><br/>';
			html += '<span class="age">' + pt.age + ' year old </span>';
			html += '<span class="gender">' + pt.gender + '</span>';
			html += '</span>';
			html += '<span class="identifiers">' + pt.patientIdentifier + '</span>';
			html += '</div>';
			jq(html).appendTo(jq('#results')).click(function() {
				location.href = pageLink("registrationViewPatient", { patientId: jq(this).find('.patientId').val() }); 
			});
		}
	});
	jq(function() {
		// if the user goes back to this page in their history, redo the ajax query
		publish('patientSearch/changed');
	});
</script>