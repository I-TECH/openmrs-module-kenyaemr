<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div class="ke-page-sidebar">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Find an Account</div>
		<div class="ke-panel-content">
			<%= ui.includeFragment("kenyaui", "widget/form", [
				id: "accountSearch",
				fields: [
					[ label: "Name or Username", formFieldName: "q", class: java.lang.String ],
					[ label: "Account Types", fragment: "widget/radioButtons", formFieldName: "which",
						selected: "both",
						separator: "&nbsp;&nbsp;",
						options: [
							[ label: "Users", value: "users" ],
							[ label: "Providers", value: "providers" ],
							[ label: "Both", value: "both" ]
						],
						onChange: """function() { publish('accountSearch/changed'); }"""
					]
				],
				fragmentProvider: "kenyaemr",
				fragment: "search",
				action: "accounts",
				submitOnEvent: "accountSearch/changed",
				resetOnSubmit: false,
				successEvent: "accountSearch/results"
			] ) %>
		</div>
	</div>

	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
		heading: "Tasks",
		items: [
			[ iconProvider: "kenyaui", icon: "buttons/account_add.png", label: "Create a New Account", href: ui.pageLink("kenyaemr", "admin/editAccount") ]
		]
	]) }
</div>


<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "account/accountList", [ id: "results", page: "admin/editAccount", heading: "Matching Accounts" ]) }
</div>

<script type="text/javascript">
	subscribe("accountSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		// if the user goes back to this page in their history, redo the ajax query
		publish('accountSearch/changed');
	});
</script>