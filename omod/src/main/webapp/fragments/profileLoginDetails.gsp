<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Login Settings" ])

	def checkCurrentPassword = !config.tempPassword;

	def changePasswordProps = checkCurrentPassword ? [ "oldPassword", "newPassword", "confirmNewPassword" ] : [ "newPassword", "confirmNewPassword" ];
	def changePasswordHiddenProps = checkCurrentPassword ? [] : [ "oldPassword" ]
%>

${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Username", value: user.username ]) }
${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Secret question", value: user.secretQuestion ]) }

${ ui.includeFragment("kenyaui", "widget/dialogForm", [
		id: "change_password",
		dialogConfig: [ heading: "Change Password" ],
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
		submitLabel: "Update",
		cancelLabel: !config.tempPassword ? "Cancel" : null,
		onSuccessCallback:  "ui.reloadPage();"
]) }

${ ui.includeFragment("kenyaui", "widget/dialogForm", [
		id: "change_secret_question",
		dialogConfig: [ heading: "Change Secret Question" ],
		fragmentProvider: "kenyaemr",
		fragment: "profileLoginDetails",
		action: "changeSecretQuestion",
		prefix: "changeSecretQuestionForm",
		commandObject: changeSecretQuestionForm,
		properties: [ "currentPassword", "secretQuestion", "newSecretAnswer" ],
		propConfig: [
				currentPassword: [ type: "password" ]
		],
		submitLabel: "Update",
		cancelLabel: "Cancel",
		onSuccessCallback: "ui.reloadPage();"
]) }