<%
	ui.decorateWith("kenyaui", "panel", [ heading: (command.original ? "Edit" : "Create") + " Patient", frameOnly: true ])

	def returnUrl = config.returnUrl ?: (command.original ? ui.pageLink("kenyaemr", "registration/registrationViewPatient", [patientId: command.original.patientId]) : ui.pageLink("kenyaemr", "registration/registrationHome"))

	def femaleChecked = command.gender == 'F' ? 'checked="true"' : ''
	def maleChecked = command.gender == 'M' ? 'checked="true"' : ''

	def demogFieldRows = [
		[
			[ object: command, property: "personName.familyName", label: "Surname *" ],
			[ object: command, property: "personName.givenName", label: "First name *" ],
			[ object: command, property: "personName.middleName", label: "Other name(s)" ]
		],
		[
			ui.decorate("kenyaui", "labeled", [label: "Sex *"], """
				<input type="radio" name="gender" value="F" id="gender-F" ${ femaleChecked }/>
				<label for="gender-F">Female</label>
				&nbsp;
				<input type="radio" name="gender" value="M" id="gender-M" ${ maleChecked }/>
				<label for="gender-M">Male</label>
				<span id="gender-F-error" class="error" style="display: none"></span>
				<span id="gender-M-error" class="error" style="display: none"></span>
			"""),
			"&nbsp;&nbsp;&nbsp;&nbsp;",
			[ object: command, property: "birthdate", label: "Birthdate *" ],
			[ object: command, property: "birthdateEstimated", label: "Estimated?" ]
		],
		[
			[ object: command, property: "maritalStatus", label: "Marital status", config: [ style: "list", options: maritalStatusOptions ] ],
			[ object: command, property: "occupation", label: "Occupation", config: [ style: "list", answerTo: occupationConcept ] ],
			[ object: command, property: "education", label: "Education", config: [ style: "list", options: educationOptions ] ]
		],
		[
			[ object: command, property: "dead", label: "Deceased" ],
			[ object: command, property: "deathDate", label: "Date of death" ],
			[ object: command, property: "causeOfDeath", label: "Cause of death", config: [ style: "list", options: causeOfDeathOptions ] ]
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
			[ object: command, property: "personAddress.address1", label: "Postal Address", config: [ size: 60 ] ],
			[ object: command, property: "personAddress.country", label: "County", config: [ size: 60 ] ],
			[ object: command, property: "subChiefName.value", label: ui.format(command.subChiefName.attributeType) ]

		],
		[
			[ object: command, property: "personAddress.address3", label: "School/Employer Address",config: [ size: 60 ] ],
			[ object: command, property: "personAddress.countyDistrict", label: "District" ],
			[ object: command, property: "personAddress.stateProvince", label: "Province", config: [ size: 60 ] ]


		],
	[		[ object: command, property: "personAddress.address6", label: "Location" ],
			[ object: command, property: "personAddress.address5", label: "Sub-location" ],
			[ object: command, property: "personAddress.address4", label: "Division", config: [ size: 60 ] ]


		],
		[

			[ object: command, property: "personAddress.address6", label: "Location" ],
			[ object: command, property: "personAddress.address5", label: "Sub-location" ],
			[ object: command, property: "personAddress.address4", label: "Division", config: [ size: 60 ] ]
		],
		[
 			[ object: command, property: "personAddress.cityVillage", label: "Village/Estate" ],
			[ object: command, property: "personAddress.address2", label: "Landmark" ],
			[ object: command, property: "personAddress.postalCode", label: "House/Plot Number" ]
		]
	]
%>

<form id="edit-patient-form" method="post" action="${ ui.actionLink("kenyaemr", "patient/editPatient", "savePatient") }">
	<% if (command.original) { %>
		<input type="hidden" name="patientId" value="${ command.original.patientId }"/>
	<% } %>

	<div class="ke-panel-content">

		<div class="ke-form-globalerrors" style="display: none"></div>

		<div class="ke-form-instructions">
			<b>*</b> indicates a required field
		</div>

		<fieldset>
			<legend>ID Numbers</legend>

			<table>
				<% if (command.inHivProgram) { %>
					<tr>
						<td class="ke-field-label">${ ui.format(command.hivIdNumber.identifierType) }</td>
						<td>${ ui.includeFragment("kenyaui", "widget/field", [ object: command, property: "hivIdNumber.identifier" ]) }</td>
						<td class="ke-field-instructions">(HIV program<% if (!command.hivIdNumber.identifier) { %>, if assigned<% } %>)</td>
					</tr>
				<% } %>
				<tr>
					<td class="ke-field-label">${ ui.format(command.patientClinicNumber.identifierType) }</td>
					<td>${ ui.includeFragment("kenyaui", "widget/field", [ object: command, property: "patientClinicNumber.identifier" ]) }</td>
					<td class="ke-field-instructions"><% if (!command.patientClinicNumber.identifier) { %>(if available)<% } %></td>
				</tr>
				<tr>
					<td class="ke-field-label">${ ui.format(command.nationalIdNumber.identifierType) } </td>
					<td>${ ui.includeFragment("kenyaui", "widget/field", [ object: command, property: "nationalIdNumber.identifier" ]) }</td>
					<td class="ke-field-instructions"><% if (!command.nationalIdNumber.identifier) { %>(if available)<% } %></td>
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

	</div>
	<div class="ke-panel-footer">
		<input class="ke-button" type="submit" value="${ command.original ? "Save Changes" : "Create Patient" }"/>
		<input class="ke-button cancel-button" type="button" value="Cancel"/>
	</div>
	
</form>

<script type="text/javascript">
jq(function() {
	jq('#edit-patient-form .cancel-button').click(function() {
		location.href = '${ returnUrl }';
	});

	kenyaui.setupAjaxPost('edit-patient-form', {
		onSuccess: function(data) {
			if (data.patientId) {
				<% if (returnUrl.indexOf('patientId') > 0) { %>
					location.href = '${ returnUrl }';
				<% } else { %>
					location.href = ui.pageLink('kenyaemr', 'registration/registrationViewPatient', { patientId: data.patientId });
				<% } %>
			} else {
				ui.notifyError('Saving patient was successful, but unexpected response');
			}
		}
	});
});
</script>