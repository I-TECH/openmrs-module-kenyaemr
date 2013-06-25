<%
	def returnUrl = config.returnUrl ?: (command.original ? ui.pageLink("kenyaemr", "registrationViewPatient", [patientId: command.original.patientId]) : ui.pageLink("kenyaemr", "registrationHome"))

	def femaleChecked = command.gender == 'F' ? 'checked="true"' : ''
	def maleChecked = command.gender == 'M' ? 'checked="true"' : ''

	def demogFieldRows = [
		[
			[ object: command, property: "personName.familyName", label: "Surname *" ],
			[ object: command, property: "personName.givenName", label: "First Name *" ],
			[ object: command, property: "personName.middleName", label: "Other Name(s)" ]		
		],
		[
			ui.decorate("kenyaui", "labeled", [label: "Sex *"], """
				<input type="radio" name="gender" value="F" id="gender-F" ${ femaleChecked }/>
				<label for="gender-F">Female</label>
				&nbsp;
				<input type="radio" name="gender" value="M" id="gender-M" ${ maleChecked }/>
				<label for="gender-M">Male</label>
				<span class="error" style="display: none"></span>
			"""),
			"&nbsp;&nbsp;&nbsp;&nbsp;",
			[ object: command, property: "birthdate", label: "Birthdate *" ],
			[ object: command, property: "birthdateEstimated", label: "Estimated?" ]
		],
		[
			[ object: command, property: "maritalStatus", label: "Marital Status", config: [ options: maritalStatusOptions ] ],
			[ object: command, property: "occupation", label: "Occupation", config: [ answerTo: occupationConcept ] ],
			[ object: command, property: "education", label: "Education", config: [ options: educationOptions ] ]
		]
	]
	def nextOfKinFieldRows = [
		[
 	 		[ object: command, property: "nameOfNextOfKin.value", label: ui.format(command.nameOfNextOfKin.attributeType) ],
 	 		[ object: command, property: "nextOfKinRelationship.value", label: ui.format(command.nextOfKinRelationship.attributeType) ]
 	    ],
	   	[
 			[ object: command, property: "nextOfKinContact.value", label: ui.format(command.nextOfKinContact.attributeType) ],
 	  		[ object: command, property: "nextOfKinAddress.value", label: ui.format(command.nextOfKinAddress.attributeType) ]
 	 	]
		
 	 	
 	  ]
	
	def addressFieldRows = [
		[
			[ object: command, property: "telephoneContact.value", label: ui.format(command.telephoneContact.attributeType) ],
			[ object: command, property: "personAddress.address1", label: "Postal Address", config: [ size: 60 ] ]
		],
		[
			[ object: command, property: "personAddress.address3", label: "School/Employer Address",config: [ size: 60 ] ],
			[ object: command, property: "personAddress.countyDistrict", label: "District" ]
			
			
		],
		[
			
			[ object: command, property: "personAddress.address6", label: "Location" ],
			[ object: command, property: "personAddress.address5", label: "Sub-location" ]
		],
		[
			[ object: command, property: "personAddress.cityVillage", label: "Village/Estate" ],
			[ object: command, property: "personAddress.address2", label: "Landmark" ]
		]
	]
%>

<style type="text/css">
#possible-matching-patients {
	float: right;
	border: 1px black solid;
	background-color: #e0e0e0;
}
</style>

<form id="edit-patient-form" method="post" action="${ ui.actionLink("kenyaemr", "registrationEditPatient", "savePatient") }">
	<% if (command.original) { %>
		<input type="hidden" name="patientId" value="${ command.original.patientId }"/>
	<% } %>

	<div class="global-error-container" style="display: none">
		${ ui.message("fix.error.plain") }
		<ul class="global-error-content"></ul>
	</div>

	<div class="ke-form-instructions">
		<b>*</b> indicates a required field
	</div>
	
	<fieldset>
		<legend>ID Numbers</legend>
	
		<table>
			<tr>
				<td class="ke-field-label">${ ui.format(command.patientClinicNumber.identifierType) }</td>
				<td>${ ui.includeFragment("kenyaui", "widget/field", [ object: command, property: "patientClinicNumber.identifier" ]) }</td>
				<td class="ke-field-instructions"><% if (!command.patientClinicNumber.identifier) { %>(if available)<% } %></td>
			</tr>
			<% if (command.inHivProgram) { %>
				<tr>
					<td class="ke-field-label">${ ui.format(command.hivIdNumber.identifierType) }</td>
					<td>${ ui.includeFragment("kenyaui", "widget/field", [ object: command, property: "hivIdNumber.identifier" ]) }</td>
					<td class="ke-field-instructions">(HIV program<% if (!command.hivIdNumber.identifier) { %>, if assigned<% } %>)</td>
				</tr>
			<% } %>
			<tr>
				<td class="ke-field-label">${ ui.format(command.nationalIdNumber.attributeType) } </td>
				<td>${ ui.includeFragment("kenyaui", "widget/field", [ object: command, property: "nationalIdNumber.value" ]) }</td>
				<td class="ke-field-instructions"><% if (!command.nationalIdNumber.value) { %>(if available)<% } %></td>
			</tr>
		</table>

	</fieldset>

	<fieldset>
		<legend>Demographics</legend>

		<% demogFieldRows.each { %>
			${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		<% } %>

	</fieldset>

	<fieldset>
		<legend>Address</legend>
	
		<% addressFieldRows.each { %>
			${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		<% } %>

	</fieldset>

	<fieldset>
		<legend>Next of Kin Details</legend>
	
		 <% nextOfKinFieldRows.each { %>
		   ${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		 <% } %>

	</fieldset>

	<div class="ke-form-buttons">
		<input class="ke-button" type="submit" value="${ command.original ? "Save Changes" : "Create Patient" }"/>
		<input class="ke-button cancel-button" type="button" value="Cancel"/>
	</div>
	
</form>

<script type="text/javascript">
jq(function() {
	jq('#edit-patient-form .cancel-button').click(function() {
		location.href = '${ returnUrl }';
	});

	kenyaui.setupAjaxPost('#edit-patient-form', {
		onSuccess: function(data) {
			if (data.patientId) {
				<% if (returnUrl.indexOf('patientId') > 0) { %>
					location.href = '${ returnUrl }';
				<% } else { %>
					location.href = ui.pageLink('kenyaemr', 'registrationViewPatient', { patientId: data.patientId });
				<% } %>
			} else {
				ui.notifyError('Saving patient was successful, but unexpected response');
			}
		}
	});
});
</script>