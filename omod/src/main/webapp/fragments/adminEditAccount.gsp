<style type="text/css">
	.person-details, .login-details, .provider-details {
		width: 50%;
	}

	.subtle-label {
		color: #888888;
	}
	
	.no-form-padding {
		margin: 0px;
	}
	
	.editable-container {
		position: relative;
	}
	
	.editable-button {
		position: absolute;
		top: 0px;
		right: 0px;
	}
</style>

<script type="text/javascript">
jq(function() {
	jq('.editable-button').hide();
	jq('.editable-container').mouseenter(function() {
		jq(this).find('.editable-button').show();
	}).mouseleave(function() {
		jq(this).find('.editable-button').hide();
	});
});
</script>

<br/>

<fieldset class="person-details editable-container">
	<legend>
		<img src="${ ui.resourceLink("uilibrary", "images/user_info_32.png") }"/>
		Person Details
	</legend>
	
	<span class="subtle-label">Real Name:</span>
	${ person.personName }
	<br/>
	<span class="subtle-label">Gender:</span>
	${ person.gender }
	<br/>
	
	<%= ui.includeFragment("uilibrary", "widget/popupForm", [
				linkConfig: [
					label: "Edit",
					classes: [ "editable-button" ]
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
</fieldset>

<br/>

<fieldset class="login-details <% if (user && !user.retired) { %> editable-container <% } %>">
	<legend>
		<img src="${ ui.resourceLink("uilibrary", "images/screen_32.png") }"/>
		Login Details
	</legend>
	<% if (user) { %>
			<% if (user.retired) { %>
				<form method="post" action="${ ui.actionLink("kenyaemr", "adminEditAccount", "unretireUser") }" class="no-form-padding">
					<input type="hidden" name="userId" value="${ user.userId }"/>
					<br/>
					<i>Account is disabled.</i>
					<br/><br/>
					${ ui.includeFragment("uilibrary", "widget/button", [ label: "Enable account", type: "submit" ]) }
					<br/><br/>
				</form>
			<% } %>
	
			<span class="subtle-label">User:</span>
			${ user.username }
			<br/>
			
			<span class="subtle-label">Roles:</span>
			${ user.roles.join(", ") }
			<br/>
			<%
				def inheritedRoles = user.allRoles
				inheritedRoles.removeAll(user.roles)
			%>
			<% if (inheritedRoles) { %>
				<span class="subtle-label">
					Inherited:
					${ inheritedRoles.join(", ") }
				</span>
			<% } %>
			
			<% if (!user.retired) { %>
				<form method="post" action="${ ui.actionLink("kenyaemr", "adminEditAccount", "retireUser") }" class="no-form-padding">
					<input type="hidden" name="userId" value="${ user.userId }"/>
					<br/>
					${ ui.includeFragment("uilibrary", "widget/button", [ label: "Disable account", type: "submit" ]) }
					<br/>
				</form>
			<% } %>
			
			<%= ui.includeFragment("uilibrary", "widget/popupForm", [
					linkConfig: [
						label: "Edit",
						classes: [ "editable-button" ]
					],
					fragment: "adminEditAccount",
					fragmentProvider: "kenyaemr",
					action: "editLoginDetails",
					prefix: "editLoginDetails",
					commandObject: editLoginDetails,
					hiddenProperties: [ "userId" ],
					properties: [ "username", "password", "confirmPassword", "roles" ],
					fieldConfig: [
						roles: [ fieldFragment: "field/RoleCollection" ]
					],
					propConfig: [
						"password": [ type: "password" ],
						"confirmPassword": [ type: "password" ]
					],
					extraFields: [
						[ hiddenInputName: "personId", value: person.id ]
					],
					popupTitle: "Edit login details for '${ user.username }'",
					submitLabel: "Save Changes",
					cancelLabel: "Cancel",
					successCallbacks: [ "ui.reloadPage();" ]
				]) %>

	<% } else { %>
	
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
				buttonConfig: [
					label: "Create Login"
				],
				fragment: "adminEditAccount",
				fragmentProvider: "kenyaemr",
				action: "editLoginDetails",
				prefix: "editLoginDetails",
				commandObject: editLoginDetails,
				properties: [ "username", "password", "confirmPassword" ],
				propConfig: [
					"password": [ type: "password" ],
					"confirmPassword": [ type: "password" ]
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

</fieldset>

<br/>

<fieldset class="provider-details <% if (provider && !provider.retired) { %> editable-container <% } %>">
	<legend>
		<img src="${ ui.resourceLink("uilibrary", "images/user_business_32.png") }"/>
		Provider Details
	</legend>
	
	<% if (provider) { %>
	
		<span class="subtle-label">Provider ID:</span>
		${ provider.identifier }
		<br/>
		
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
				linkConfig: [
					label: "Edit",
					classes: [ "editable-button" ]
				],
				fragment: "adminEditAccount",
				fragmentProvider: "kenyaemr",
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

		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
				buttonConfig: [
					label: "Make this person a Provider"
				],
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
</fieldset>