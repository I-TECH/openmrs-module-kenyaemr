<%
	def returnUrl = config.returnUrl ?: (command.original ? ui.pageLink("registrationViewPatient", [patientId: command.original.patientId]) : ui.pageLink("registrationHome"))  
	
	def nameFields = [
		[ object: command, property: "personName.familyName", label: "Surname" ],
		[ object: command, property: "personName.givenName", label: "First Name" ],
		[ object: command, property: "personName.middleName", label: "Other Name(s)" ]		
	]
	
	def demogFields = [
		ui.decorate("labeled", [label: "Sex"], """
			<input type="radio" name="gender" value="F" id="gender-F" checked="${ command.gender == 'F' }"/>
			<label for="gender-F">Female</label>
			&nbsp;
			<input type="radio" name="gender" value="M" id="gender-M" checked="${ command.gender == 'M' }"/>
			<label for="gender-M">Male</label>
			<span class="error" style="display: none"></span>
		"""),
		"&nbsp;&nbsp;&nbsp;&nbsp;",
		[ object: command, property: "birthdate", label: "Birthdate" ],
		"- or -",
		[ object: command, property: "age", label: "Age", afterField: "year(s)"]
	]
	
	def contactFields = [
		[ object: command, property: "telephoneContact.value", label: ui.format(command.telephoneContact.attributeType) ]
	]
%>

<style>
#possible-matching-patients {
	float: right;
	border: 1px black solid;
	background-color: #e0e0e0;
}

#edit-patient-form {
	float: left;
}

#edit-patient-form h4 {
	text-decoration: underline;
	margin-bottom: 0.5em;
}
</style>

<% if (!command.original) { %>
	<div id="possible-matching-patients">
		TODO: as fields are filled out, <br/> show similar patients here.
	</div>
<% } %>

<form id="edit-patient-form" method="post" action="${ ui.actionLink("savePatient") }">
	<% if (command.original) { %>
		<input type="hidden" name="patientId" value="${ command.original.patientId }"/>
	<% } %>

	<div class="global-error-container" style="display: none">
		${ ui.message("fix.error.plain") }
		<ul class="global-error-content"></ul>
	</div>
	
	<h4>ID Numbers</h4>
	<table>
		<tr>
			<td>${ ui.format(command.patientClinicNumber.identifierType) }</td>
			<td>${ ui.includeFragment("widget/field", [ object: command, property: "patientClinicNumber.identifier" ]) }</td>
			<td><% if (!command.patientClinicNumber.identifier) { %>(if available)<% } %></td>
		</tr>
		<tr>
			<td>${ ui.format(command.hivIdNumber.identifierType) }</td>
			<td>${ ui.includeFragment("widget/field", [ object: command, property: "hivIdNumber.identifier" ]) }</td>
			<td>(HIV program<% if (!command.hivIdNumber.identifier) { %>, if assigned<% } %>)</td>
		</tr>
	</table>

	<h4>Demographics</h4>
	${ ui.includeFragment("widget/rowOfFields", [ fields: nameFields ]) }
	${ ui.includeFragment("widget/rowOfFields", [ fields: demogFields ]) }
	<br/>
	${ ui.includeFragment("widget/rowOfFields", [ fields: contactFields ]) }

	TODO: address <br/>

	TODO: marital status <br/>	
		
	<br/>
	
	<input class="button" type="submit" value="${ command.original ? "Save Changes" : "Create Patient" }"/>
	<input class="button cancel-button" type="button" value="Cancel"/>
	
</form>

<script>
jq(function() {
	jq('#edit-patient-form .button').button();
	
	jq('#edit-patient-form .cancel-button').click(function() {
		location.href = '${ returnUrl }';
	});
	
	ui.setupAjaxPost('#edit-patient-form', {
		onSuccess: function(data) {
			if (data.patientId) {
				<% if (returnUrl.indexOf('patientId') > 0) { %>
					location.href = '${ returnUrl }';
				<% } else { %>
					location.href = pageLink('registrationViewPatient', { patientId: data.patientId });
				<% } %>
			} else {
				notifyError('Saving patient was successful, but unexpected response');
				debugObject(data);
			}
		}
	});
});
</script>