<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Available Forms" ])

	config.require("visit")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
	function enterHtmlForm(visitId, patientId, htmlFormId) {
		location.href = ui.pageLink('kenyaemr', 'enterHtmlForm', { patientId: patientId, htmlFormId: htmlFormId, visitId: visitId, returnUrl: location.href });
	}
</script>

<% if (availableForms) {
	availableForms.each { form ->
		def formOnClick = """enterHtmlForm(${ visit.visitId }, ${ patient.id }, ${ form.htmlFormId })"""
%>
	<div class="stack-item clickable" onclick="${ formOnClick }">
		<img src="${ ui.resourceLink(form.iconProvider, "images/" + form.icon) }" style="float: left; margin: 2px" alt="Enter Form" />
		<b>${ form.label }</b>
		<div style="clear: both"></div>
	</div>
<%
	}
} else {
%>
	<i>None</i>
<% } %>