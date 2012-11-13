<%
	def demographics = [
		[
			[formFieldName: "personName.givenName", label: "Given Name", class: java.lang.String],
			[formFieldName: "personName.familyName", label: "Family Name", class: java.lang.String]
		],
		[
			ui.decorate("uilibrary", "labeled", [label: "Sex"], """
				<input type="radio" name="gender" value="F" id="gender-F"/>
				<label for="gender-F">Female</label>
				<input type="radio" name="gender" value="M" id="gender-M"/>
				<label for="gender-M">Male</label>
				<span class="error" style="display: none"></span>
			""")
		]
	]
	
	def login = [
		[
			[formFieldName: "username", label: "Username", class: java.lang.String]
		],
		[
			[formFieldName: "password", label: "Password", class: java.lang.String, config: [ type: "password" ]],
			[formFieldName: "confirmPassword", label: "Confirm Password", class: java.lang.String, config: [ type: "password" ]]
		],
	]

	def roleHtml = context.userService.allRoles.findAll {
		it.role != "Anonymous" && it.role != "Authenticated"
	}.collect {
		"""<input type="checkbox" name="roles" value="${ it.role }" id="role-${ it.uuid }"/>
			<label for="role-${ it.uuid }">${ it.role }</label>"""
	}.join("<br/>")
	roleHtml += """<br/><span class="error" style="display: none"></span>"""
	
%>

<form id="create-user-form" method="post" action="${ ui.actionLink("kenyaemr", "adminNewUser", "createUser") }">
	<div class="global-error-container" style="display: none">
		${ ui.message("fix.error.plain") }
		<ul class="global-error-content"></ul>
	</div>

	<fieldset>
		<legend>Person Info</legend>
		<% demographics.each { %>
			${ ui.includeFragment("kenyaemr", "widget/rowOfFields", [ fields: it ]) }
		<% } %>
	</fieldset>
	
	<br/>
	
	<fieldset>
		<legend>Login Info</legend>
		<% login.each { %>
			${ ui.includeFragment("kenyaemr", "widget/rowOfFields", [ fields: it ]) }
		<% } %>
	
		${ ui.decorate("uilibrary", "labeled", [label: "Roles"], roleHtml) }
	</fieldset>
	
	<br/>
	
	<input type="submit" value="Create User"/>	
</form>

<script type="text/javascript">
jq(function() {
	jq('#create-user-form input[type=submit]').button();
	
	ui.setupAjaxPost('#create-user-form', {
		onSuccess: function(data) {
			if (data.userId) {
				location.href = ui.pageLink('kenyaemr', 'adminManageUsers');
			} else {
				notifyError('Creating user was successful, but unexpected response');
				debugObject(data);
			}
		}
	});
});
</script>