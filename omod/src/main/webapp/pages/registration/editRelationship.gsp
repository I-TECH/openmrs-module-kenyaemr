<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, visit: currentVisit ])
%>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/editRelationship", [ relationship: relationship, patient: currentPatient, returnUrl: returnUrl ]) }
</div>