<%
	ui.decorateWith("standardKenyaEmrPage", [ patient: patient ])
%>

<input id="cancel-form" type="button" value="${ ui.message("htmlformentry.discard") }"/>

<script>
	jq(function() {
		jq('#cancel-form').click(function() {
			location.href = '${ returnUrl }';
		}).insertAfter(jq('input.submitButton'));
	});
</script>

${ ui.includeFragment("enterHtmlForm", [
		patient: patient,
		formUuid: formUuid,
		htmlFormId: htmlFormId,
		returnUrl: returnUrl
	]) }