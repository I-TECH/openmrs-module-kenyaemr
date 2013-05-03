<%
	config.require("category")
	config.require("reasonType")

	ui.includeJavascript("kenyaui", "coreFragments.js")

	def concept = { uuid -> org.openmrs.api.context.Context.conceptService.getConceptByUuid(uuid).conceptId }
%>
<select id="${ config.id }" name="${ config.formFieldName }" >
	<option label="Select..." value="" />
<% if (config.category == "ARV" && config.reasonType == "change") { %>
	<optgroup label="Change of first line therapy">
		<option label="Toxicity / Side Effects" value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Risk of Pregnancy" value="${ concept("160559AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="New Drug Available" value="${ concept("160561AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Medication Unavailable / Stock Out" value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Currently Pregnant" value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="New Diagnosis of TB" value="${ concept("160567AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	</optgroup>
	<optgroup label="Change to second line therapy">
		<option label="Clinical Treatment Failure" value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Immunological Failure" value="${ concept("160566AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Virological Failure" value="${ concept("160569AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	</optgroup>
<% } else if (category == "ARV" && config.reasonType == "stop") { %>
	<option label="Clinical Treatment Failure" value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Toxicity / Side Effects" value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Completed Total PMTCT" value="${ concept("1253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Poor Adherence" value="${ concept("159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Inpatient Care or Hospitalization" value="${ concept("5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Tuberculosis Treatment Started" value="${ concept("1270AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Planned Treatment Interruption" value="${ concept("160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Refusal / Patient Decision" value="${ concept("127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Drug Formulation Changed" value="${ concept("1258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Cannot Afford Treatment" value="${ concept("819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Currently Pregnant" value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Medication Unavailable / Stock Out" value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
<% } else if (category == "TB") { %>
	<option label="Clinical Treatment Failure" value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Toxicity / Side Effects" value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Poor Adherence" value="${ concept("159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Inpatient Care or Hospitalization" value="${ concept("5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Planned Treatment Interruption" value="${ concept("160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Refusal / Patient Decision" value="${ concept("127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Drug Formulation Changed" value="${ concept("1258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Cannot Afford Treatment" value="${ concept("819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Currently Pregnant" value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Medication Unavailable / Stock Out" value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
<% } %>
</select>

<% if (config.parentFormId) { %>
<script type="text/javascript">
	FieldUtils.defaultSubscriptions('${ config.parentFormId }', '${ config.formFieldName }', '${ config.id }');
	jq(function() {
		jq('#${ config.id }').change(function() {
			publish('${ config.parentFormId }/changed');
		});
	});
</script>
<% } %>