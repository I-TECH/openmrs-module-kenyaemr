<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [
			heading: "My Profile",
			items: [
					[
							iconProvider: "kenyaemr",
							icon: "buttons/profile_password.png",
							label: "Change Password",
							href: "javascript:showDivAsDialog('#change_password_form', 'Change Password', null)"
					],
					[
							iconProvider: "kenyaemr",
							icon: "buttons/profile_secret_question.png",
							label: "Change Secret Question",
							href: "javascript:showDivAsDialog('#change_secret_question_form', 'Change Secret Question', null)"
					]
			]
	]) }
</div>

<div id="content-main">

	<div class="panel-frame">
		<div class="panel-heading">Personal Details</div>
		<div class="panel-content">
			${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Real name", value: person.personName ]) }
			${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Gender", value: (person.gender.toLowerCase() == 'f' ? "Female" : "Male") ]) }
		</div>
	</div>

	${ ui.includeFragment("kenyaemr", "profileLoginDetails") }
</div>