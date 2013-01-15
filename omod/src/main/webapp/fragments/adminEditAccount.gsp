<div class="panel-frame" style="width: 70%">
	<%= ui.includeFragment("uilibrary", "widget/popupForm", [
		linkConfig: [
			label: "Edit",
			classes: [ "panel-editlink", "clickable" ]
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
	<div class="panel-heading">Person Details</div>
	<div class="panel-content">
		${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Real name", value: person.personName ]) }
		${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Gender", value: (person.gender.toLowerCase() == 'f' ? "Female" : "Male") ]) }
	</div>
</div>

<div class="panel-frame" style="width: 70%">
	<% if (user && !user.retired) { %>
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
			linkConfig: [
				label: "Edit",
				classes: [ "panel-editlink", "clickable" ]
			],
			fragment: "adminEditAccount",
			fragmentProvider: "kenyaemr",
			action: "editLoginDetails",
			prefix: "editLoginDetails",
			commandObject: editLoginDetails,
			hiddenProperties: [ "userId" ],
			properties: [ "username", "password", "confirmPassword", "roles" ],
			propConfig: [
					password: [ type: "password" ],
					confirmPassword: [ type: "password" ]
			],
			fieldConfig: [
					roles: [ fieldFragment: "field/RoleCollection" ]
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
	<div class="panel-heading">Login Details</div>
	<div class="panel-content">

	<% if (user) { %>
		${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Username", value: user.username ]) }
		${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Roles", value: user.roles.join(", ") ]) }

		<%
			def inheritedRoles = user.allRoles
			inheritedRoles.removeAll(user.roles)

			if (inheritedRoles) {
		%>
		${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Inherited", value: inheritedRoles.join(", ") ]) }
		<% } %>

		<br/>

		<% if (user.retired) { %>
			<form method="post" action="${ ui.actionLink("kenyaemr", "adminEditAccount", "unretireUser") }" class="no-form-padding">
				<input type="hidden" name="userId" value="${ user.userId }"/>
				<i>Account is disabled</i> <br />
				${ ui.includeFragment("uilibrary", "widget/button", [ label: "Enable account", type: "submit", iconProvider: "kenyaemr", icon: "buttons/user_enable.png" ]) }
			</form>
		<% } else { %>
			<form method="post" action="${ ui.actionLink("kenyaemr", "adminEditAccount", "retireUser") }" class="no-form-padding">
				<input type="hidden" name="userId" value="${ user.userId }"/>
				${ ui.includeFragment("uilibrary", "widget/button", [ label: "Disable account", type: "submit", iconProvider: "kenyaemr", icon: "buttons/user_disable.png" ]) }
			</form>
		<% } %>
	<% } else { %>
	
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
				buttonConfig: [
					label: "Create Login",
					iconProvider: "kenyaemr",
					icon: "buttons/user_enable.png"
				],
				fragment: "adminEditAccount",
				fragmentProvider: "kenyaemr",
				action: "editLoginDetails",
				prefix: "editLoginDetails",
				commandObject: editLoginDetails,
				properties: [ "username", "password", "confirmPassword", "roles" ],
				propConfig: [
					password: [ type: "password" ],
					confirmPassword: [ type: "password" ]
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

<div class="panel-frame" style="width: 70%">
	<% if (provider && !provider.retired) { %>
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
			linkConfig: [
				label: "Edit",
				classes: [ "panel-editlink", "clickable" ]
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
	<div class="panel-heading">Provider Details</div>
	<div class="panel-content">
	
	<% if (provider) { %>
		${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Provider ID", value: provider.identifier ]) }
	<% } else { %>
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
			buttonConfig: [
				label: "Make this person a Provider",
				iconProvider: "kenyaemr",
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