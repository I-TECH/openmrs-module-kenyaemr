<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Available Forms" ])

	config.require("visit")
%>

${ ui.includeFragment("kenyaemr", "formList", [ visit: visit, forms: availableForms ]) }