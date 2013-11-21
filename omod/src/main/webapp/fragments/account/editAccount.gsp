<div class="ke-panel-frame">
	<div class="ke-panel-heading">Person Details</div>
	<div class="ke-panel-content">
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Real name", value: kenyaui.formatPersonName(person) ]) }
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Gender", value: kenyaui.formatPersonGender(person) ]) }
	</div>
	<div class="ke-panel-footer">
		${ ui.includeFragment("kenyaui", "widget/dialogForm", [
				id: "person-details-form",
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
				dialogConfig: [ heading: "Edit person details for " + kenyaui.formatPersonName(person) ],
				fragment: "account/editAccount",
				fragmentProvider: "kenyaemr",
				action: "editPersonDetails",
				prefix: "editPersonDetails",
				commandObject: editPersonDetails,
				hiddenProperties: [ "personId" ],
				properties: [ "personName.givenName", "personName.familyName", "gender" ],
				propConfig: [
						"gender": [
								options: [
										[ label: "Female", value: "F" ],
										[ label: "Male", value: "M" ]
								]
						]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) }
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">Login Details</div>

	<% if (user) { %>
	<div class="ke-panel-content">
		<% if (user.retired) { %>
		<div class="ke-warning" style="margin-bottom: 5px">${ ui.message("kenyaemr.loginIsDisabled") }</div>
		<% } %>

		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Username", value: user.username ]) }
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Roles", value: user.roles.join(", ") ]) }

		<%
			def inheritedRoles = user.allRoles
			inheritedRoles.removeAll(user.roles)
		%>

		<% if (inheritedRoles) { %>
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Inherited", value: inheritedRoles.join(", ") ]) }
		<% } %>
	</div>
	<% } %>

	<div class="ke-panel-footer">
	<% if (user && !user.retired) { %>

		<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
				dialogConfig: [ heading: "Edit login details for ${ kenyaui.formatPersonName(person) }", width: 90, height: 90 ],
				fragment: "account/editAccount",
				fragmentProvider: "kenyaemr",
				action: "editLoginDetails",
				prefix: "editLoginDetails",
				commandObject: editLoginDetails,
				hiddenProperties: [ "userId" ],
				properties: [ "username", "password", "confirmPassword", "secretQuestion", "secretAnswer", "roles" ],
				propConfig: [
						password: [ type: "password" ],
						confirmPassword: [ type: "password" ],
						secretAnswer: [ type: "password" ]
				],
				fieldConfig: [
						roles: [ fieldFragment: "field/RoleCollection", hideRoles: [ "Anonymous", "Authenticated", "API Privileges", "API Privileges (View and Edit)" ] ]
				],
				extraFields: [
						[ hiddenInputName: "personId", value: person.id ]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) %>

	<% } else if (!user) { %>

		<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
				buttonConfig: [
						label: "Create login",
						iconProvider: "kenyaui",
						icon: "buttons/user_enable.png"
				],
				dialogConfig: [ heading: "New Login Account for ${ kenyaui.formatPersonName(person) }", width: 90, height: 90 ],
				fragment: "account/editAccount",
				fragmentProvider: "kenyaemr",
				action: "editLoginDetails",
				prefix: "editLoginDetails",
				commandObject: editLoginDetails,
				properties: [ "username", "password", "confirmPassword", "secretQuestion", "secretAnswer", "roles" ],
				propConfig: [
						password: [ type: "password" ],
						confirmPassword: [ type: "password" ],
						secretAnswer: [ type: "password" ]
				],
				fieldConfig: [
						roles: [ fieldFragment: "field/RoleCollection", hideRoles: [ "Anonymous", "Authenticated", "API Privileges", "API Privileges (View and Edit)" ] ]
				],
				extraFields: [
						[ hiddenInputName: "personId", value: person.id ]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) %>

	<% } %>
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">Provider Details</div>

	<% if (provider && !provider.retired) { %>
	<div class="ke-panel-content">
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Provider ID", value: provider.identifier ]) }
	</div>
	<% } %>

	<div class="ke-panel-footer">
	<% if (provider && !provider.retired) { %>

		<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
				dialogConfig: [ heading: "Edit Provider account for ${ kenyaui.formatPersonName(person) }", width: 50, height: 30 ],
				fragmentProvider: "kenyaemr",
				fragment: "account/editAccount",
				action: "editProviderDetails",
				prefix: "editProviderDetails",
				commandObject: editProviderDetails,
				properties: [ "identifier" ],
				extraFields: [
						[ hiddenInputName: "personId", value: person.id ],
						[ hiddenInputName: "editProviderDetails.providerId", value: provider.id ]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) %>

	<% } else { %>

		<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
				buttonConfig: [
						label: "Make this person a provider",
						iconProvider: "kenyaui",
						icon: "buttons/provider_${ person.gender == "F" ? 'f' : 'm' }.png"
				],
				dialogConfig: [ heading: "New Provider account for ${ kenyaui.formatPersonName(person) }", width: 50, height: 30 ],
				fragmentProvider: "kenyaemr",
				fragment: "account/editAccount",
				action: "editProviderDetails",
				prefix: "editProviderDetails",
				commandObject: editProviderDetails,
				properties: [ "identifier" ],
				extraFields: [
						[ hiddenInputName: "personId", value: person.id ]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) %>

	<% } %>
	</div>
</div>