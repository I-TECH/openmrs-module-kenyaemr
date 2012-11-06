<style type="text/css">
#selected-patient-active-visits {
	width: 50%;
	float: right;
	overflow: auto;
	text-align: right;
}
</style>

<div id="selected-patient-active-visits">
	<small>Active visits</small>
	<%
	if (visits) {
		visits.each { visit ->
	%>
	<span class="active-visit">${ ui.format(visit.visitType) }</span>
	<%
		}
	} else {
		print ui.message("general.none")
	} %>
</div>
