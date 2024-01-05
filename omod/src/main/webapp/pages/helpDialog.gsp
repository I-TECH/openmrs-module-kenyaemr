<%
	ui.decorateWith("kenyaemr", "standardPage")

%>

<form  action="${ ui.pageLink("kenyaemr", "helpDialog") }">
	${ ui.includeFragment("kenyaemr", "helpResources") }

</form>
