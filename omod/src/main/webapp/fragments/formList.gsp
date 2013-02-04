<%
	config.require("forms")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
	function enterHtmlForm(visitId, patientId, formUuid) {
		location.href = ui.pageLink('kenyaemr', 'enterHtmlForm', { patientId: patientId, formUuid: formUuid, visitId: visitId, returnUrl: location.href });
	}
</script>

<% if (config.forms && config.forms.size > 0) {
	config.forms.each { form ->
		def formOnClick = """enterHtmlForm(${ config.visit ? config.visit.visitId : "null" }, ${ patient.id }, '${ form.formUuid }')"""
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