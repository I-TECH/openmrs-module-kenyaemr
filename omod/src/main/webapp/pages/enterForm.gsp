<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, visit: currentVisit ])
%>

<input id="cancel-form" type="button" value="${ ui.message("htmlformentry.discard") }"/>

<script type="text/javascript">
	jq(function() {
		jq('#cancel-form').click(function() {
			ui.navigate('${ returnUrl }');
		}).insertAfter(jq('input.submitButton'));
	});
</script>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "enterHtmlForm", [ patient: currentPatient, formUuid: formUuid, visit: currentVisit, returnUrl: returnUrl ]) }
</div>