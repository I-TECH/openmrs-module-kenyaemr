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

${ ui.includeFragment("patientSearch", [ page: "intakeViewPatient" ]) }

${ ui.includeFragment("widget/button", [
	id: "create-patient",
	iconProvider: "uilibrary",
	icon: "add1-32.png",
	label: "Patient Not Found?",
	extra: "Registration -> Create New",
	onClick: "window.alert('TODO implement this')" ]) }