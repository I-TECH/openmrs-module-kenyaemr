<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>
<style type="text/css">
	#create-user {
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
		Find a User
	</legend>
	
	${ ui.includeFragment("uilibrary", "widget/form", [
			id: "userSearch",
			fields: [
				[ label: "Username or Name", formFieldName: "q", class: java.lang.String ],
				[ label: "Role", formFieldName: "role", class: org.openmrs.Role ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "userSearch",
			action: "search",
			submitOnEvent: "userSearch/changed",
			resetOnSubmit: false,
			successEvent: "userSearch/results"
		] )}
</fieldset>

<fieldset style="border: none">
	${ ui.includeFragment("kenyaemr", "userList", [
		id: "results",
		page: "adminEditUser"
	]) }
</fieldset>

${ ui.includeFragment("uilibrary", "widget/button", [
	id: "create-user",
	iconProvider: "uilibrary",
	icon: "user_add_32.png",
	label: "Create User",
	href: ui.pageLink("kenyaemr", "adminEditUser") ]) }

<script type="text/javascript">
	subscribe("userSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		// if the user goes back to this page in their history, redo the ajax query
		publish('userSearch/changed');
	});
</script>