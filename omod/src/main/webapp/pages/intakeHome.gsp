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

${ ui.includeFragment("kenyaemr", "patientSearch", [ page: "intakeViewPatient" ]) }

${ ui.includeFragment("uilibrary", "widget/button", [
	id: "create-patient",
	iconProvider: "uilibrary",
	icon: "add1-32.png",
	label: "Patient Not Found?",
	extra: "Registration -> Create New",
	href: ui.pageLink("kenyaemr", "registrationCreatePatient") ]) }