<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = []
	if (user && user.retired) {
		menuItems << [ iconProvider: "kenyaui", icon: "buttons/user_enable.png", label: "Enable Account", href: "javascript:enableUser()" ]
	}
	if (user && !user.retired) {
		menuItems << [ iconProvider: "kenyaui", icon: "buttons/user_disable.png", label: "Disable Account", href: "javascript:disableUser()" ]
	}

	menuItems << [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to Accounts", href: ui.pageLink("kenyaemr", "adminManageAccounts") ]
%>

<div id="content-side">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: person ? "Account" : "Menu", items: menuItems ]) }
</div>

<div id="content-main">
<% if (person) { %>
	${ ui.includeFragment("kenyaemr", "admin/editAccount") }
<% } else { %>
	${ ui.includeFragment("kenyaemr", "admin/newAccount") }
<% } %>
</div>

<% if (user) { %>
	<script type="text/javascript">
		function enableUser() {
			jq('#enable_user_form').submit();
		}
		function disableUser() {
			jq('#disable_user_form').submit();
		}
	</script>
	<form id="enable_user_form" method="post" action="${ ui.actionLink("kenyaemr", "admin/editAccount", "unretireUser") }" style="display: none">
		<input type="hidden" name="userId" value="${ user.userId }"/>
	</form>
	<form id="disable_user_form" method="post" action="${ ui.actionLink("kenyaemr", "admin/editAccount", "retireUser") }" style="display: none">
		<input type="hidden" name="userId" value="${ user.userId }"/>
	</form>
<% } %>

