<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Available Forms" ])

	config.require("visit")
%>

<script type="text/javascript">
	function enterHtmlForm(visitId, htmlFormId, title) {
		location.href = ui.pageLink('kenyaemr', 'enterHtmlForm', { patientId: ${ patient.id }, htmlFormId: htmlFormId, visitId: visitId, returnUrl: '${ ui.urlEncode(ui.thisUrl()) }' });
	}
</script>

<% if (availableForms) {
	availableForms.each {
	%>
	${ ui.includeFragment("uilibrary", "widget/button", [
			iconProvider: it.iconProvider,
			icon: it.icon,
			label: it.label,
			onClick: "enterHtmlForm(" + visit.visitId + "," + it.htmlFormId + ", '" + it.label + "');"
	]) }
	<br/>
	<%
	}
} else {
%>
	<i>None</i>
<% } %>