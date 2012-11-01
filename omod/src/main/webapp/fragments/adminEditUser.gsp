<style type="text/css">
	.user-details {
		width: 33%;
	}
	
	.user-details .subtle-label {
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

<% if (user.retired) { %>
	<form method="post" action="${ ui.actionLink("kenyaemr", "adminEditUser", "unretireUser") }" class="no-form-padding">
		<input type="hidden" name="userId" value="${ user.userId }"/>
		<br/>
		<i>Account is disabled.</i>
		<br/><br/>
		${ ui.includeFragment("uilibrary", "widget/button", [ label: "Enable account", type: "submit" ]) }
		<br/><br/>
	</form>
	
<% } else { %>
	<form method="post" action="${ ui.actionLink("kenyaemr", "adminEditUser", "retireUser") }" class="no-form-padding">
		<input type="hidden" name="userId" value="${ user.userId }"/>
		<br/>
		<i>Account is enabled.</i>
		<br/><br/>
		${ ui.includeFragment("uilibrary", "widget/button", [ label: "Disable account", type: "submit" ]) }
		<br/><br/>
	</form>
	
<% } %>

<div class="user-details">
	<div class="login-details <% if (!user.retired) { %> editable-container <% } %>">
		<img class="icon" src="${ ui.resourceLink("uilibrary", "images/" + (user.retired ? "user_blue_32.png" : "user_32.png")) }"/>
		<span class="subtle-label">User:</span>
		${ user.username }
		
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
				linkConfig: [
					label: "Edit",
					classes: [ "editable-button" ]
				],
				fragment: "adminEditUser",
				fragmentProvider: "kenyaemr",
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
	</div>

	<div class="role-details <% if (!user.retired) { %> editable-container <% } %>">
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
		<%
			def roleHtml = context.userService.allRoles.findAll {
				it.role != "Anonymous" && it.role != "Authenticated"
			}.collect {
				"""<input type="checkbox" name="roles" value="${ it.role }" id="role-${ it.uuid }" ${ user.roles.contains(it) ? 'checked="true"' : "" }/>
					<label for="role-${ it.uuid }">${ it.role }</label>"""
			}.join("<br/>")
			roleHtml += """<br/><span class="error" style="display: none"></span>"""
			roleHtml += "<br/>"
		%>
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
				linkConfig: [
					label: "Edit",
					classes: [ "editable-button" ]
				],
				fragment: "adminEditUser",
				fragmentProvider: "kenyaemr",
				action: "editUserRoles",
				fields: [
					[ hiddenInputName: "userId", value: user.userId ],
					[ value: roleHtml ]
				],
				popupTitle: "Edit roles for '${ user.username }'",
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				successCallbacks: [ "ui.reloadPage();" ]
			]) %>
	</div>

	<div class="person-details <% if (!user.retired) { %> editable-container <% } %>">
		<span class="subtle-label">Real Name:</span>
		${ user.personName }
		<br/>
		<span class="subtle-label">Gender:</span>
		${ user.person.gender }
		<br/>
		
		<%= ui.includeFragment("uilibrary", "widget/popupForm", [
					linkConfig: [
						label: "Edit",
						classes: [ "editable-button" ]
					],
					fragment: "adminEditUser",
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
					popupTitle: "Edit person details for '${ user.username }'",
					submitLabel: "Save Changes",
					cancelLabel: "Cancel",
					successCallbacks: [ "ui.reloadPage();" ]
				]) %>
	</div>
</div>

<br/>
