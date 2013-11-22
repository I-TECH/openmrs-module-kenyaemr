<%
	config.require("category")
	config.require("reasonType")

	def concept = { uuid -> org.openmrs.api.context.Context.conceptService.getConceptByUuid(uuid).conceptId }
%>
<select id="${ config.id }" name="${ config.formFieldName }" >
	<option label="Select..." value=""></option>
<% if (config.category == "ARV" && config.reasonType == "change") { %>
	<optgroup label="Reason for SUBSTITUTION of Drug">
		<option value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Toxicity / side effects (1)</option>
		<option value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Pregnancy (2)</option>
		<option value="${ concept("160559AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Risk of pregnancy (3)</option>
		<option value="${ concept("160567AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">New diagnosis of TB (4)</option>
		<option value="${ concept("160561AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">New drug available (5)</option>
		<option value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Drugs out of stock (6)</option>
	</optgroup>
	<optgroup label="Reason for SWITCH of Regimen (e.g. to 2nd or 3rd line)">
		<option value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Clinical treatment failure (8)</option>
		<option value="${ concept("160566AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Immunological failure (9)</option>
		<option value="${ concept("160569AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Virological Failure (10)</option>
	</optgroup>
<% } else if (category == "ARV" && config.reasonType == "stop") { %>
	<option value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Toxicity / side effects (1)</option>
	<option value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Pregnancy (2)</option>
	<option value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Clinical treatment failure (3)</option>
	<option value="${ concept("159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Poor Adherence (4)</option>
	<option value="${ concept("5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Inpatient care or hospitalization (5)</option>
	<option value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Drugs out of stock (6)</option>
	<option value="${ concept("819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Patient lacks finance (7)</option>
	<option value="${ concept("127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Refusal / patient decision (8)</option>
	<option value="${ concept("160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Planned treatment interruption (9)</option>
	<option value="${ concept("1253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Completed total PMTCT</option>
	<option value="${ concept("1270AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Tuberculosis treatment started</option>
<% } else if (category == "TB") { %>
	<option value="${ concept("102AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Toxicity / side effects</option>
	<option value="${ concept("1434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Pregnancy</option>
	<option value="${ concept("843AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Clinical treatment failure</option>
	<option value="${ concept("159598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Poor adherence</option>
	<option value="${ concept("5485AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Inpatient care or hospitalization</option>
	<option value="${ concept("1754AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Drugs out of stock</option>
	<option value="${ concept("819AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Patient lacks finance</option>
	<option value="${ concept("127750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Refusal / patient decision</option>
	<option value="${ concept("160016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Planned treatment interruption</option>
	<option value="${ concept("1258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA") }">Drug formulation changed</option>
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