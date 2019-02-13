<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/editPatient", [ patient: currentPatient, returnUrl: returnUrl ]) }
</div>