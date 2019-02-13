<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ label: "Create a new account", iconProvider: "kenyaui", icon: "buttons/account_add.png", href: ui.pageLink("kenyaemr", "admin/createAccount1") ],
			[ label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", href: ui.pageLink("kenyaemr", "admin/adminHome") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaemr", "account/accountSearchForm", [ defaultWhich: "all" ]) }

	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Tasks", items: menuItems ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "account/accountSearchResults", [ showUsernames: true, pageProvider: "kenyaemr", page: "admin/editAccount" ]) }
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('input[name="query"]').focus();
	});
</script>