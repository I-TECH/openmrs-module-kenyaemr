<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "My Profile",
			items: [
					[
							iconProvider: "kenyaui",
							icon: "buttons/profile_password.png",
							label: "Change Password",
							href: "javascript:showDivAsDialog('#change_password_form', 'Change Password', null)"
					],
					[
							iconProvider: "kenyaui",
							icon: "buttons/profile_secret_question.png",
							label: "Change Secret Question",
							href: "javascript:showDivAsDialog('#change_secret_question_form', 'Change Secret Question', null)"
					]
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

	${ ui.includeFragment("kenyaemr", "profileLoginDetails") }
</div>