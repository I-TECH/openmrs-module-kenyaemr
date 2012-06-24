<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<style>
	#create-button {
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
		Find an Account
	</legend>
	
	${ ui.includeFragment("widget/form", [
			id: "accountSearch",
			fields: [
				[ label: "Name or Username", formFieldName: "q", class: java.lang.String ],
				[ label: "Account Types", value: """TODO <input type="hidden" name="includeUsers" value="true"/> <input type="hidden" name="includeProviders" value="true"/> """]
			],
			fragment: "adminUtil",
			action: "accountSearch",
			submitOnEvent: "accountSearch/changed",
			resetOnSubmit: false,
			successEvent: "accountSearch/results"
		] )}
</fieldset>

<fieldset style="border: none">
	${ ui.includeFragment("accountList", [
		id: "results",
		page: "adminEditAccount"
	]) }
</fieldset>

${ ui.includeFragment("widget/button", [
	id: "create-button",
	iconProvider: "uilibrary",
	icon: "user_business_add_32.png",
	label: "Create Account",
	href: ui.pageLink("adminEditAccount") ]) }

<script>
	subscribe("accountSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		// if the user goes back to this page in their history, redo the ajax query
		publish('accountSearch/changed');
	});
</script>