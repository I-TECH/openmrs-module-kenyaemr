<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<style>
	#create-provider {
		position: fixed;
		bottom: 0;
		left: 40%;
		padding: 0.5em;
	}
	
	#search {
		float: left;
	}
</style>

<fieldset id="search">
	<legend>
		Find a Provider
	</legend>
	
	${ ui.includeFragment("widget/form", [
			id: "providerSearch",
			fields: [
				[ label: "Username or Name", formFieldName: "q", class: java.lang.String ],
				[ label: "Role", formFieldName: "role", class: org.openmrs.Role ]
			],
			fragment: "providerSearch",
			action: "search",
			submitOnEvent: "providerSearch/changed",
			resetOnSubmit: false,
			successEvent: "providerSearch/results"
		] )}
</fieldset>

<fieldset style="border: none">
	${ ui.includeFragment("providerList", [
		id: "results",
		page: "adminEditProvider"
	]) }
</fieldset>

${ ui.includeFragment("widget/button", [
	id: "create-provider",
	iconProvider: "uilibrary",
	icon: "user_business_add_32.png",
	label: "Create Provider",
	href: ui.pageLink("adminEditProvider") ]) }

<script>
	subscribe("providerSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		// if the user goes back to this page in their history, redo the ajax query
		publish('providerSearch/changed');
	});
</script>