<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Step 2: Register Account" ])

	def nameFields = [
			[
					[ object: command, property: "personName.familyName", label: "Surname *" ],
					[ object: command, property: "personName.givenName", label: "First name *" ],
			]
	]

	def contactFields = [
			[
					[ object: command, property: "telephoneContact", label: "Telephone contact *" ],
					[ object: command, property: "emailAddress", label: "Email address" ]
			]
	]
	
	def userFields = [
			[
					[formFieldName: "username", label: "Username", class: java.lang.String]
			],
			[
					[
							formFieldName: "password",
							label: "Password",
							class: java.lang.String,
							config: [ type: "password" ]
					],
					[
							formFieldName: "confirmPassword",
							label: "Confirm Password",
							class: java.lang.String,
							config: [ type: "password" ]
					]
			],
			[
					[
							formFieldName: "roles",
							label: "Roles",
							class: java.util.List,
							fieldFragment: "field/RoleCollection",
							hideRoles: [
									"Anonymous",
									"Authenticated",
									"API Privileges",
									"API Privileges (View and Edit)",
									"Provider",
									"System Developer"
							]
					]
			]
	]

	def providerFields = [
			[
					[ formFieldName: "providerIdentifier", label: "Provider ID", class: java.lang.String ]
			]
	]
%>

<form id="create-account-form" method="post" action="${ ui.actionLink("kenyaemr", "account/newAccount", "submit") }">
	<div class="ke-form-globalerrors" style="display: none"></div>

	<% if (command.original) { %>
	<input type="hidden" name="personId" value="${ command.original.id }"/>
	<% } %>

	<fieldset>
		<legend>Person Info</legend>

		<% nameFields.each { %>
		${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		<% } %>

		<table>
			<tr>
				<td valign="top">
					<label class="ke-field-label">Sex *</label>
					<span class="ke-field-content">
						<input type="radio" name="gender" value="F" id="gender-F" ${ command.gender == 'F' ? 'checked="checked"' : '' }/> Female
						<input type="radio" name="gender" value="M" id="gender-M" ${ command.gender == 'M' ? 'checked="checked"' : '' }/> Male
						<span id="gender-F-error" class="error" style="display: none"></span>
						<span id="gender-M-error" class="error" style="display: none"></span>
					</span>
				</td>
			</tr>
		</table>

		<% contactFields.each { %>
		${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		<% } %>

	</fieldset>

	<fieldset>
		<legend>Login Info</legend>
		<% userFields.each { %>
			${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		<% } %>
	</fieldset>

	<fieldset>
		<legend>Provider Info</legend>
		<% providerFields.each { %>
			${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		<% } %>
	</fieldset>

	<div class="ke-form-footer">
		<button type="submit"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/ok.png") }" /> Create Account</button>
	</div>
</form>

<script type="text/javascript">
	jq(function() {
		kenyaui.setupAjaxPost('create-account-form', {
			onSuccess: function(data) {
				if (data.personId) {
					ui.navigate('kenyaemr', 'admin/manageAccounts');
				} else {
					kenyaui.notifyError('Creating user was successful, but unexpected response');
				}
			}
		});
	});
</script>