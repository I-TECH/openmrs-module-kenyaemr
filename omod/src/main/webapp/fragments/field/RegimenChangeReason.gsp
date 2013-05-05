<%
	config.require("category")
	config.require("reasonType")

	ui.includeJavascript("kenyaui", "coreFragments.js")

	def concept = { uuid -> org.openmrs.api.context.Context.conceptService.getConceptByUuid(uuid).conceptId }
%>
<select id="${ config.id }" name="${ config.formFieldName }" >
	<option label="Select..." value="" />
<% if (config.category == "ARV" && config.reasonType == "change") { %>
	<optgroup label="Reason for SUBSTITUTION of Drug">
		<option label="Toxicity / side effects (1)" value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Pregnancy (2)" value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Risk of pregnancy (3)" value="${ concept("160559AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="New diagnosis of TB (4)" value="${ concept("160567AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="New drug available (5)" value="${ concept("160561AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Drugs out of stock (6)" value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	</optgroup>
	<optgroup label="Reason for SWITCH of Regimen (e.g. to 2nd or 3rd line)">
		<option label="Clinical treatment failure (8)" value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Immunological failure (9)" value="${ concept("160566AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
		<option label="Virological Failure (10)" value="${ concept("160569AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	</optgroup>
<% } else if (category == "ARV" && config.reasonType == "stop") { %>
	<option label="Toxicity / side effects (1)" value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Pregnancy (2)" value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Clinical treatment failure (3)" value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Poor Adherence (4)" value="${ concept("159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Inpatient care or hospitalization (5)" value="${ concept("5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Drugs out of stock (6)" value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Patient lacks finance (7)" value="${ concept("819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Refusal / patient decision (8)" value="${ concept("127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Planned treatment interruption (9)" value="${ concept("160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Completed total PMTCT" value="${ concept("1253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Tuberculosis treatment started" value="${ concept("1270AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
<% } else if (category == "TB") { %>
	<option label="Toxicity / side effects" value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Pregnancy" value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Clinical treatment failure" value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Poor adherence" value="${ concept("159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Inpatient care or hospitalization" value="${ concept("5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Drugs out of stock" value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Patient lacks finance" value="${ concept("819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Refusal / patient decision" value="${ concept("127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Planned treatment interruption" value="${ concept("160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
	<option label="Drug formulation changed" value="${ concept("1258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }" />
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