<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, visit: currentVisit ])
%>
<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "form/enterHtmlForm", [ patient: currentPatient, encounter: encounter, visit: currentVisit, returnUrl: returnUrl ]) }
</div>