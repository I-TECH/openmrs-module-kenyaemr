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
					[ label: "Change Secret Question", iconProvider: "kenyaui", icon: "buttons/profile_secret_question.png", onClick: "showSecretQuestionDialog()" ]
			]
	]) }
</div>

<div class="ke-page-content">

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Personal Details</div>
		<div class="ke-panel-content">
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Real name", value: kenyaUi.formatPersonName(person) ]) }
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Gender", value: kenyaUi.formatPersonGender(person) ]) }
		</div>
	</div>

	${ ui.includeFragment("kenyaemr", "profileLoginDetails", [ tempPassword: tempPassword ]) }
</div>