<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, visit: currentVisit ])
%>
<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "form/enterHtmlForm", [ patient: currentPatient, formUuid: formUuid, visit: currentVisit, returnUrl: returnUrl ]) }
</div>