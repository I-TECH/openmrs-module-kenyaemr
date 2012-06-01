<%
	def idFields = []
	identifierTypes.eachWithIndex { it, i ->
		idFields << [ hiddenInputName: "identifiers[${ i }].identifierType", value: it.id ]
		idFields << [ formFieldName: "identifiers[${ i }].identifier", label: ui.format(it), class: java.lang.String ]
	}
	
	def nameFields = [
		[formFieldName: "names[0].givenName", label: "Given Name", class: java.lang.String],
		[formFieldName: "names[0].familyName", label: "Family Name", class: java.lang.String]
	]
	
	def demogFields = [
		ui.decorate("labeled", [label: "Sex"], """
			<input type="radio" name="gender" value="F" id="gender-F"/>
			<label for="gender-F">Female</label>
			<input type="radio" name="gender" value="M" id="gender-M"/>
			<label for="gender-M">Male</label>
			<span class="error" style="display: none"></span>
		"""),
		"&nbsp;&nbsp;&nbsp;&nbsp;",
		[formFieldName: "birthdate", label: "Birthdate", class: java.util.Date],
		"- or -",
		[formFieldName: "age", label: "Age", class: java.lang.Integer, afterField: "year(s)"]
	]
	
	def contactFields = [
		[ hiddenInputName: "attributes[0].attributeType", value: telephoneContact.id ],
		[ formFieldName: "attributes[0].value", label: ui.format(telephoneContact), class: java.lang.String ]
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

<form id="create-patient-form" method="post" action="${ ui.actionLink("createPatient") }">

	<div class="global-error-container" style="display: none">
		${ ui.message("fix.error.plain") }
		<ul class="global-error-content"></ul>
	</div>
	
	${ ui.includeFragment("widget/rowOfFields", [ fields: idFields ]) }
	
	${ ui.includeFragment("widget/rowOfFields", [ fields: nameFields ]) }

	${ ui.includeFragment("widget/rowOfFields", [ fields: demogFields ]) }
	
	${ ui.includeFragment("widget/rowOfFields", [ fields: contactFields ]) }
	
	TODO: determine correct Kenya address format <br/>
	
	TODO: determine whether Nearest H/Centre is HIV-specific, or just a plain location for all patients <br/>
	
	TODO: determine how to handle "Married Polygamous" vs "Married Monogamous" as civil statuses <br/>
		
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