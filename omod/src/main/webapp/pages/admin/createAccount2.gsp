<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ label: "Back to previous step", iconProvider: "kenyaui", icon: "buttons/back.png", href: ui.pageLink("kenyaemr", "admin/createAccount1") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Create Account", items: menuItems ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "account/newAccount", [ person: patient ]) }
</div>