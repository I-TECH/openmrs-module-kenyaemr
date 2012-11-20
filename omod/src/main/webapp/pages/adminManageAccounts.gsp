<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	<div class="panel-frame">
		<div class="panel-heading">Find an Account</div>
		<div class="panel-content">
			<%= ui.includeFragment("uilibrary", "widget/form", [
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
				fragment: "adminUtil",
				action: "accountSearch",
				submitOnEvent: "accountSearch/changed",
				resetOnSubmit: false,
				successEvent: "accountSearch/results"
			] ) %>
		</div>
	</div>

	<div class="panel-frame">
		<div class="panel-heading">Tasks</div>

		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
				iconProvider: "kenyaemr",
				icon: "buttons/account_add.png",
				label: "Create a New Account",
				href: ui.pageLink("kenyaemr", "adminEditAccount")
		]) }
	</div>
</div>


<div id="content-main">
	${ ui.includeFragment("kenyaemr", "accountList", [ id: "results", page: "adminEditAccount" ]) }
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