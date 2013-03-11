<%
	config.require("definition", "data")

	ui.decorateWith("kenyaui", "panel", [ heading: definition.name ])

	// If report has a single dataset, then it won't be wrapped in a fieldset
	def singleDataset = (data.dataSets.size() == 1)
%>

<% if (definition.parameters) { %>
	<fieldset>
		<legend>Parameters</legend>
	
		<% definition.parameters.each { %>
			${ ui.includeFragment("kenyaemr", "dataPoint", [ label: it.label, value: data.context.parameterValues[it.name] ]) }
		<% } %>
	</fieldset>
	<br/>
<% } %>

<% data.dataSets.each {
	def ds = it.value
	def dsd = definition.dataSetDefinitions[it.key].parameterizable
%>

	<% if (!singleDataset) { %>
	<fieldset><legend>${ dsd.name }</legend>
	<% } %>

	<% if (dsd.class.name == 'org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition') { %>
		
		<table class="table-decorated table-vertical">
			<tbody>
			<% dsd.columns.each { %>
				<tr>
					<th>${ it.name }</th>
					<td style="text-align: left">${ it.label }</td>
					<td>${ ds.getData(it) }</td>
				</tr>
			<% } %>
			</tbody>
		</table>
		
	<% } else {
		def cols = ds.metaData.columns
	%>
		<table class="table-decorated table-vertical">
			<thead>
				<tr>
					<% cols.each { %>
						<th style="text-align: left">${ it.label }</th>
					<% } %>
				</tr>
			</thead>
			<tbody>
				<% ds.rows.each { row -> %>
					<tr>
						<% cols.each { %>
							<td style="text-align: left">${ row.getColumnValue(it) }</td>
						<% } %>
					</tr>
				<% } %>
			</tbody>
		</table>

	<% } %>

	<% if (!singleDataset) { %>
	</fieldset>
	<% } %>

<% } %>