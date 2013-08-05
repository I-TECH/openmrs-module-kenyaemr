<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient, visit: visit ])
%>

<input id="cancel-form" type="button" value="${ ui.message("htmlformentry.discard") }"/>

<script type="text/javascript">
	jq(function() {
		jq('#cancel-form').click(function() {
			location.href = '${ returnUrl }';
		}).insertAfter(jq('input.submitButton'));
	});
</script>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "enterHtmlForm", [ patient: patient, formUuid: formUuid, visit: visit, returnUrl: returnUrl ]) }
</div>