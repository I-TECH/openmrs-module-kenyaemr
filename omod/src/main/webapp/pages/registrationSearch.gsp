<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

<style type="text/css">
	#create-patient {
		position: fixed;
		bottom: 0;
		left: 40%;
	}
</style>

${ ui.includeFragment("kenyaemr", "patientSearch", [
		page: "registrationViewPatient",
		defaultWhich: "all"
	]) }

${ ui.includeFragment("uilibrary", "widget/button", [
	id: "create-patient",
	iconProvider: "uilibrary",
	icon: "add1-32.png",
	label: "Create New Patient Record",
	extra: "Patient does not exist yet",
	href: ui.pageLink("kenyaemr", "registrationCreatePatient") ]) }