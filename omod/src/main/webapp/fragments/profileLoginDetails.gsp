<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Login Settings" ])

	def checkCurrentPassword = !forcePasswordChange;

	def changePasswordProps = checkCurrentPassword ? [ "oldPassword", "newPassword", "confirmNewPassword" ] : [ "newPassword", "confirmNewPassword" ];
	def changePasswordHiddenProps = checkCurrentPassword ? [] : [ "oldPassword" ]
%>

${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Username", value: user.username ]) }
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Secret question", value: user.secretQuestion ]) }

${ ui.includeFragment("kenyaui", "widget/popupForm", [
		id: "change_password",
		linkConfig: [
				label: "",
				classes: [ "hidden" ]
		],
		fragmentProvider: "kenyaemr",
		fragment: "profileLoginDetails",
		action: "changePassword",
		prefix: "changePasswordForm",
		commandObject: changePasswordForm,
		properties: changePasswordProps,
		propConfig: [
				oldPassword: [ type: "password" ],
				newPassword: [ type: "password" ],
				confirmNewPassword: [ type: "password" ]
		],
		hiddenProperties: changePasswordHiddenProps,
		popupTitle: "Change Password",
		submitLabel: "Update",
		cancelLabel: !forcePasswordChange ? "Cancel" : null,
		successCallbacks: [ "ui.reloadPage();" ]
]) }

${ ui.includeFragment("kenyaui", "widget/popupForm", [
		id: "change_secret_question",
		linkConfig: [
				label: "",
				classes: [ "hidden" ]
		],
		fragmentProvider: "kenyaemr",
		fragment: "profileLoginDetails",
		action: "changeSecretQuestion",
		prefix: "changeSecretQuestionForm",
		commandObject: changeSecretQuestionForm,
		properties: [ "currentPassword", "secretQuestion", "newSecretAnswer" ],
		propConfig: [
				currentPassword: [ type: "password" ]
		],
		popupTitle: "Change Secret Question",
		submitLabel: "Update",
		cancelLabel: "Cancel",
		successCallbacks: [ "ui.reloadPage();" ]
]) }

<% if (forcePasswordChange) { %>
<script type="text/javascript">
	jq(function() {
		showDivAsDialog('#change_password_form', 'Reset password', null);
	});
</script>
<% } %>