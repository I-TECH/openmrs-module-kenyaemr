<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [
		items: [
			[ iconProvider: "kenyaemr", icon: "buttons/back.png", label: "Back to Accounts", href: ui.pageLink("kenyaemr", "adminManageAccounts") ]
		]
	]) }
</div>

<div id="content-main">
<% if (person) { %>
	${ ui.includeFragment("kenyaemr", "adminEditAccount") }
<% } else { %>
	<h3>Create a new Account</h3>
	${ ui.includeFragment("kenyaemr", "adminNewAccount") }
<% } %>
</div>

