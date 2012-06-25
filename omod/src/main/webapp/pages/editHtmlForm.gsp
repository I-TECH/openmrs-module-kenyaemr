<%
	ui.decorateWith("standardKenyaEmrPage", [ afterAppHeader: ui.includeFragment("selectedPatientHeader") ])
%>

<div style="border-bottom: 2px gray dashed; font-size: 0.8em; margin-bottom: 0.5em;">
	<div style="float: right">
		<input id="cancel-form" type="button" value="${ ui.message("htmlformentry.discard") }"/>
	</div>
	<i>Editing ${ ui.format(encounter.encounterType) } at ${ ui.format(encounter.location) } on ${ ui.format(encounter.encounterDatetime) }</i>
	<div style="clear: both"></div>
</div>

<script>
	jq('#cancel-form').click(function() {
		location.href = '${ returnUrl }';
	});
</script>

${ ui.includeFragment("enterHtmlForm", [
		patient: patient,
		encounter: encounter,
		returnUrl: returnUrl
	]) }