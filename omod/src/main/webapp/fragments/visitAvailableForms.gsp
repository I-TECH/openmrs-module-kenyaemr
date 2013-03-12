<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Available Forms" ])

	config.require("visit")
%>

${ ui.includeFragment("kenyaui", "widget/formStack", [ visit: visit, forms: availableForms ]) }