<div class="ke-panel-frame">
	<div class="ke-panel-heading">Person Details</div>
	<div class="ke-panel-content">
		<div class="ke-stack-item">
		<%= ui.includeFragment("kenyaui", "widget/popupForm", [
				buttonConfig: [
						label: "Edit",
						classes: [ "ke-editbutton" ],
						iconProvider: "kenyaui",
						icon: "edit.png"
				],
				fragment: "adminEditAccount",
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
				popupTitle: "Edit person details for '${ ui.format(person) }'",
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				successCallbacks: [ "ui.reloadPage();" ]
		]) %>

		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Real name", value: person.personName ]) }
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Gender", value: (person.gender.toLowerCase() == 'f' ? "Female" : "Male") ]) }
		</div>
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">Login Details</div>
	<div class="ke-panel-content">
		<div class="ke-stack-item">
			<% if (user && !user.retired) { %>
			<%= ui.includeFragment("kenyaui", "widget/popupForm", [
					buttonConfig: [
							label: "Edit",
							classes: [ "ke-editbutton" ],
							iconProvider: "kenyaui",
							icon: "edit.png"
					],
					fragment: "adminEditAccount",
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
					popupTitle: "Edit login details for '${ ui.format(person) }'",
					submitLabel: "Save Changes",
					cancelLabel: "Cancel",
					successCallbacks: [ "ui.reloadPage();" ]
			]) %>
			<% } %>

			<% if (user) { %>
				${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Username", value: user.username ]) }
				${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Roles", value: user.roles.join(", ") ]) }

				<%
					def inheritedRoles = user.allRoles
					inheritedRoles.removeAll(user.roles)

					if (inheritedRoles) {
				%>
				${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Inherited", value: inheritedRoles.join(", ") ]) }
				<% } %>

				<% if (user.retired) { %>
					<br/>
					<img src="${ ui.resourceLink("kenyaui", "images/alert.png") }" alt="" /> <i>Account is disabled</i>
				<% } %>

			<% } else { %>

				<%= ui.includeFragment("kenyaui", "widget/popupForm", [
						buttonConfig: [
							label: "Create Login",
							iconProvider: "kenyaui",
							icon: "buttons/user_enable.png"
						],
						fragment: "adminEditAccount",
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
							roles: [ fieldFragment: "field/RoleCollection" ]
						],
						extraFields: [
							[ hiddenInputName: "personId", value: person.id ]
						],
						popupTitle: "New Login Account for '${ ui.format(person) }'",
						submitLabel: "Save Changes",
						cancelLabel: "Cancel",
						successCallbacks: [ "ui.reloadPage();" ]
					]) %>

			<% } %>
		</div>
	</div>
</div>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">Provider Details</div>
	<div class="ke-panel-content">
		<div class="ke-stack-item">
			<% if (provider && !provider.retired) { %>
			<%= ui.includeFragment("kenyaui", "widget/popupForm", [
					buttonConfig: [
							label: "Edit",
							classes: [ "ke-editbutton" ],
							iconProvider: "kenyaui",
							icon: "edit.png"
					],
					fragmentProvider: "kenyaemr",
					fragment: "adminEditAccount",
					action: "editProviderDetails",
					prefix: "editProviderDetails",
					commandObject: editProviderDetails,
					properties: [ "identifier" ],
					extraFields: [
							[ hiddenInputName: "personId", value: person.id ],
							[ hiddenInputName: "editProviderDetails.providerId", value: provider.id ]
					],
					popupTitle: "Edit Provider account for '${ ui.format(person) }'",
					submitLabel: "Save Changes",
					cancelLabel: "Cancel",
					successCallbacks: [ "ui.reloadPage();" ]
			]) %>
			<% } %>
	
			<% if (provider) { %>
				${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Provider ID", value: provider.identifier ]) }
			<% } else { %>
				<%= ui.includeFragment("kenyaui", "widget/popupForm", [
					buttonConfig: [
						label: "Make this person a Provider",
						iconProvider: "kenyaui",
						icon: "buttons/provider.png"
					],
					fragmentProvider: "kenyaemr",
					fragment: "adminEditAccount",
					action: "editProviderDetails",
					prefix: "editProviderDetails",
					commandObject: editProviderDetails,
					properties: [ "identifier" ],
					extraFields: [
						[ hiddenInputName: "personId", value: person.id ]
					],
					popupTitle: "New Provider account for '${ ui.format(person) }'",
					submitLabel: "Save Changes",
					cancelLabel: "Cancel",
					successCallbacks: [ "ui.reloadPage();" ]
				]) %>
			<% } %>
		</div>
	</div>
</div>