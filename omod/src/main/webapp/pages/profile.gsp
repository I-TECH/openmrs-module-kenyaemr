<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>
<script type="text/javascript">
	function showChangePasswordDialog() {
		kenyaui.openPanelDialog({ templateId: 'change_password_form' });
	}
	function showSecretQuestionDialog() {
		kenyaui.openPanelDialog({ templateId: 'change_secret_question_form' });
	}

	<% if (tempPassword) { %>
	jq(function() {
		showChangePasswordDialog();
	});
	<% } %>
</script>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "My Profile",
			items: [
					[ label: "Change Password", iconProvider: "kenyaui", icon: "buttons/profile_password.png", onClick: "showChangePasswordDialog()" ],
			]
	]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "account/personDetails", [ person: person ]) }

	${ ui.includeFragment("kenyaemr", "profileLoginDetails", [ tempPassword: tempPassword ]) }
</div>