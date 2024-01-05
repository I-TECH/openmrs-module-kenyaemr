<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
${ ui.includeFragment("kenyaemr", "report/adxView", [ request: reportRequest.id, returnUrl: returnUrl ]) }