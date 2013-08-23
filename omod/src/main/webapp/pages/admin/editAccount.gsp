<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to Accounts", href: ui.pageLink("kenyaemr", "admin/manageAccounts") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: person ? "Account" : "Menu", items: menuItems ]) }
</div>

<div class="ke-page-content">
<% if (person) { %>
	${ ui.includeFragment("kenyaemr", "account/editAccount") }
<% } else { %>
	${ ui.includeFragment("kenyaemr", "account/newAccount") }
<% } %>
</div>