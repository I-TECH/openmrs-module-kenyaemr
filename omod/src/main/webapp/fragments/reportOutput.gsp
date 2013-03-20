<%
	config.require("definition", "data")

	ui.decorateWith("kenyaui", "panel", [ heading: definition.name ])

	// If report has a single dataset, then it won't be wrapped in a fieldset
	def singleDataset = (data.dataSets.size() == 1)

	def formatData = { it -> it ?: "-" }
%>

<% if (definition.description) { %>
<fieldset>
	<legend>Description</legend>
	${ definition.description }
</fieldset>
<br/>
<% } %>

<% if (definition.parameters) { %>
	<fieldset>
		<legend>Parameters</legend>
	
		<% definition.parameters.each { %>
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: it.label, value: data.context.parameterValues[it.name] ]) }
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

	<% if (ds.class.name == 'org.openmrs.module.reporting.dataset.MapDataSet') { %>
		
		<table class="ke-table-decorated ke-table-vertical">
			<tbody>
			<% ds.metaData.columns.each { col -> %>
				<tr>
					<th>${ col.name }</th>
					<td style="text-align: left">${ col.label }</td>
					<td>${ formatData(ds.getData(col)) }</td>
				</tr>
			<% } %>
			</tbody>
		</table>
		
	<% } else {
		def cols = ds.metaData.columns
	%>
		<table class="ke-table-decorated ke-table-vertical">
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
							<td style="text-align: left">${ formatData(row.getColumnValue(it)) }</td>
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