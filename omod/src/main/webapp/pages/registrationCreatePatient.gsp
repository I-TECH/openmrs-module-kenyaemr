<%
	ui.decorateWith("standardAppPage")
	
	def idFields = context.patientService.getAllPatientIdentifierTypes(false).collect {
		[formFieldName: "identifier." + it.id, label: it.name, class: java.lang.String]
	}
	
	def nameFields = [
		[formFieldName: "patient.names[0].givenName", label: "Given Name", class: java.lang.String],
		[formFieldName: "patient.names[0].familyName", label: "Family Name", class: java.lang.String]
	]
	
	def demogFields = [
		[formFieldName: "birthdate", label: "Birthdate", class: java.util.Date],
		"- or -",
		[formFieldName: "age", label: "Age", class: java.lang.Integer, afterField: "year(s)"],
		"&nbsp;&nbsp;&nbsp;&nbsp;",
		ui.decorate("labeled", [label: "Gender"], """
			<input type="radio" name="patient.gender" value="F" id="gender-F"/>
			<label for="gender-F">Female</label>
			<input type="radio" name="patient.gender" value="M" id="gender-M"/>
			<label for="gender-M">Male</label>
			<span class="error" style="display: none"></span>
		""")
	]
%>

<style>
#possible-matching-patients {
	float: right;
	border: 1px black solid;
	background-color: #e0e0e0;
}

#create-patient-form {
	float: left;
}
</style>

<h2>Create a New Patient Record</h2>

<div id="possible-matching-patients">
	TODO: as fields are filled out, <br/> show similar patients here.
</div>

<form id="create-patient-form" method="post" action="${ ui.actionLink("registrationUtil", "createPatient") }">

	<div class="global-error-container" style="display: none">
		${ ui.message("fix.error.plain") }
		<ul class="global-error-content"></ul>
	</div>
	
	${ ui.includeFragment("widget/rowOfFields", [ fields: nameFields ]) }

	${ ui.includeFragment("widget/rowOfFields", [ fields: demogFields ]) }
		
	${ ui.includeFragment("widget/rowOfFields", [ fields: idFields ]) }
	
	<br/>
	<input type="submit" value="Create Patient"/>
</form>

<script>
jq(function() {
	jq('#create-patient-form input[type=submit]').button();
	
	ui.setupAjaxPost('#create-patient-form', {
		onSuccess: function(data) {
			if (data.patientId) {
				location.href = pageLink('registrationViewPatient', { patientId: data.patientId });
			} else {
				notifyError('Creating patient was successful, but unexpected response');
				debugObject(data);
			}
		}
	});
});
</script>