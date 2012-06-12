<style>
	.user-details {
		width: 33%;
	}
	
	.user-details .label {
		color: #888888;
	}
	
	.login-details, .person-details, .role-details {
		border: 1px grey solid;
		padding: 1em;
	}
	
	.login-details {
		border-bottom: none;
		background-color: #e0e0e0;
	}
	
	.role-details {
		border-bottom: none;
	}
	
	.no-form-padding {
		margin: 0px;
	}
</style>

<br/>
<br/>

<div class="user-details">
	<div class="login-details">
		<img class="icon" src="${ ui.resourceLink("images/" + (user.retired ? "user_blue_32.png" : "user_32.png")) }"/>
		<span class="label">User:</span>
		${ user.username }
		
		<% if (user.retired) { %>
			<form method="post" action="${ ui.actionLink("adminEditUser", "unretireUser") }" class="no-form-padding">
				<input type="hidden" name="userId" value="${ user.userId }"/>
				<br/>
				Account disabled.
				<input type="submit" value="Enable account"/>
			</form>
			
		<% } else { %>
			<form method="post" action="${ ui.actionLink("adminEditUser", "retireUser") }" class="no-form-padding">
				<input type="hidden" name="userId" value="${ user.userId }"/>
				${ ui.includeFragment("widget/button", [ label: "Disable account", type: "submit" ]) }
			</form>
			<%= ui.includeFragment("widget/popupForm", [
					buttonConfig: [
						label: "Edit login details"
					],
					fragment: "adminEditUser",
					action: "editLoginDetails",
					prefix: "editLoginDetails",
					commandObject: editLoginDetails,
					hiddenProperties: [ "userId" ],
					properties: [ "username", "password", "confirmPassword" ],
					propConfig: [
						"password": [ type: "password" ],
						"confirmPassword": [ type: "password" ]
					],
					popupTitle: "Edit login details for '${ user.username }'",
					submitLabel: "Save Changes",
					cancelLabel: "Cancel",
					successCallbacks: [ "ui.reloadPage();" ]
				]) %>
		<% } %>
	</div>

	<div class="role-details">
		<span class="label">Roles:</span>
		${ user.roles.join(", ") }
		<br/>
		<%
			def inheritedRoles = user.allRoles
			inheritedRoles.removeAll(user.roles)
		%>
		<% if (inheritedRoles) { %>
			<span class="label">
				Inherited:
				${ inheritedRoles.join(", ") }
			</span>
		<% } %>
	</div>

	<div class="person-details">
		<span class="label">Real Name:</span>
		${ user.personName }
		<br/>
		<span class="label">Gender:</span>
		${ user.person.gender }
		<br/>
		
		<%= ui.includeFragment("widget/popupForm", [
					buttonConfig: [
						label: "Edit person details"
					],
					fragment: "adminEditUser",
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
					popupTitle: "Edit person details for '${ user.username }'",
					submitLabel: "Save Changes",
					cancelLabel: "Cancel",
					successCallbacks: [ "ui.reloadPage();" ]
				]) %>
	</div>
</div>

<br/>
