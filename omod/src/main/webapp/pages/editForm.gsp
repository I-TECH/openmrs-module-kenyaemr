<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, visit: currentVisit ])
%>

<script type="text/javascript">
	// HFE doesn't generate a Discard button so we append our own
	jq(function() {
		jq('#discard-button').insertAfter(jq('input.submitButton'));
	});

	function onClickDiscard() {
		ui.navigate('${ returnUrl }');
	}
</script>

<div class="ke-page-content">
	<div style="display: none">
		<input id="discard-button" type="button" value="${ ui.message("htmlformentry.discard") }" onclick="onClickDiscard()" />
	</div>

	${ ui.includeFragment("kenyaemr", "enterHtmlForm", [ patient: currentPatient, encounter: encounter, visit: currentVisit, returnUrl: returnUrl ]) }
</div>