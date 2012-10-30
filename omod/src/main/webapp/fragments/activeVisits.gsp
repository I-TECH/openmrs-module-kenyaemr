<style type="text/css">
#selected-patient-active-visits {
	width: 50%;
	float: right;
	overflow: auto;
	text-align: right;
}

#selected-patient-active-visits > .visit {
	color: #223;
	background-color: #EEF;
	margin-left: 3px;
	padding: 2px;
	border-radius: 2px;
	display: inline-block;
}
</style>

<div id="selected-patient-active-visits">
	<small>Active visits</small>
	<%
	if (visits) {
		visits.each { visit ->
	%>
	<span class="visit">${ ui.format(visit.visitType) }</span>
	<%
		}
	} else {
		print ui.message("general.none")
	} %>
</div>
