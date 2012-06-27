<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<style>
	#create-patient {
		position: fixed;
		bottom: 0;
		left: 40%;
	}
</style>

${ ui.includeFragment("patientSearch", [
		page: "registrationViewPatient",
		defaultWhich: "all"
	]) }

${ ui.includeFragment("widget/button", [
	id: "create-patient",
	iconProvider: "uilibrary",
	icon: "add1-32.png",
	label: "Create New Patient Record",
	extra: "Patient does not exist yet",
	href: ui.pageLink("registrationCreatePatient") ]) }