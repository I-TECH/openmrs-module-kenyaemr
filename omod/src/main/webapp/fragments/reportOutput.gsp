<%
	config.require("definition", "data")

	ui.decorateWith("kenyaui", "panel", [ heading: definition.name ])

	// If report has a single dataset, then it won't be wrapped in a fieldset
	def singleDataset = (data.dataSets.size() == 1)
%>

<% if (definition.description) { %>
<fieldset>
	<legend>Description</legend>

	${ definition.description }
</fieldset>
<% } %>

<% if (definition.parameters) { %>
<fieldset>
	<legend>Parameters</legend>

	<% definition.parameters.each { %>
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: it.label, value: data.context.parameterValues[it.name] ]) }
	<% } %>
</fieldset>
<% } %>

<% data.dataSets.each {
	def ds = it.value
	def dsd = definition.dataSetDefinitions[it.key].parameterizable
%>

	<% if (!singleDataset) { %>
	<fieldset><legend>${ dsd.name }</legend>
	<% } %>

	<%
	if (dsd.class.name == "org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition") {

		def total = ds.rows.size(), males = 0, females = 0
		ds.rows.each {
			def gender = it.getColumnValue("Sex");
			if (gender.equals("M")) {
				++males
			} else if (gender.equals("F")) {
				++females
			}
		}
	%>
	<fieldset>
		<legend>Summary</legend>
		<table>
			<tr>
				<td style="padding-right: 20px">${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Total", value: total ]) }</td>
				<td style="padding-right: 20px">${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Males", value: males ]) }</td>
				<td>${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Females", value: females ]) }</td>
			</tr>
		</table>
	</fieldset>
	<% } %>

	${ ui.includeFragment("kenyaemr", "reportDataSet", [ dataSet: ds ]) }

	<% if (!singleDataset) { %>
	</fieldset>
	<% } %>

<% } %>