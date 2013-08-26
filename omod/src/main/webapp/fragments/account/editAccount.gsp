<script type="text/javascript">
	function onEnableUser(userId) {
		kenyaui.openConfirmDialog({
			heading: 'KenyaEMR',
			message: '${ ui.message("kenyaemr.confirmReenableUser") }',
			okCallback: function() { doUnretireUser(userId); }
		});
	}
	function onDisableUser(userId) {
		kenyaui.openConfirmDialog({
			heading: 'KenyaEMR',
			message: '${ ui.message("kenyaemr.confirmDisableUser") }',
			okCallback: function() { doRetireUser(userId); }
		});
	}
	function doUnretireUser(userId) {
		ui.getFragmentActionAsJson('kenyaemr', 'account/editAccount', 'unretireUser', { userId: userId, reason: 'Admin UI' }, function() {
			ui.reloadPage();
		});
	}
	function doRetireUser(userId) {
		ui.getFragmentActionAsJson('kenyaemr', 'account/editAccount', 'retireUser', { userId: userId, reason: 'Admin UI' }, function() {
			ui.reloadPage();
		});
	}
</script>

<div class="ke-panel-frame">
	<div class="ke-panel-heading">Person Details</div>
	<div class="ke-panel-content">
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Real name", value: person.personName ]) }
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Gender", value: (person.gender.toLowerCase() == 'f' ? "Female" : "Male") ]) }
	</div>
	<div class="ke-panel-footer">
		<%= ui.includeFragment("kenyaui", "widget/popupForm", [
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
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
				popupTitle: "Edit person details for '${ ui.format(person) }'",
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				successCallbacks: [ "ui.reloadPage();" ]
		]) %>
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

		<%= ui.includeFragment("kenyaui", "widget/popupForm", [
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
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
				popupTitle: "Edit login details for '${ ui.format(person) }'",
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				successCallbacks: [ "ui.reloadPage();" ]
		]) %>

		<%= ui.includeFragment("kenyaui", "widget/button", [
				label: "Disable",
				iconProvider: "kenyaui",
				icon: "glyphs/disable.png",
				onClick: "onDisableUser(" + user.id + ")"
		]) %>

	<% } else if (!user) { %>

		<%= ui.includeFragment("kenyaui", "widget/popupForm", [
				buttonConfig: [
						label: "Create Login",
						iconProvider: "kenyaui",
						icon: "buttons/user_enable.png"
				],
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
				popupTitle: "New Login Account for '${ ui.format(person) }'",
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				successCallbacks: [ "ui.reloadPage();" ]
		]) %>

	<% } else if (user.retired) { %>

		${ ui.includeFragment("kenyaui", "widget/button", [
				label: "Re-enable",
				iconProvider: "kenyaui",
				icon: "glyphs/enable.png",
				onClick: "onEnableUser(" + user.id + ")"
		]) }

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

		<%= ui.includeFragment("kenyaui", "widget/popupForm", [
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
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
				popupTitle: "Edit Provider account for '${ ui.format(person) }'",
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				successCallbacks: [ "ui.reloadPage();" ]
		]) %>

	<% } else { %>

		<%= ui.includeFragment("kenyaui", "widget/popupForm", [
				buttonConfig: [
						label: "Make this person a Provider",
						iconProvider: "kenyaui",
						icon: "buttons/provider.png"
				],
				fragmentProvider: "kenyaemr",
				fragment: "account/editAccount",
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