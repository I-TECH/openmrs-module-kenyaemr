<%
	config.require("definition", "data")
%>

<h2>${ definition.name }</h2>

<fieldset>
	<legend> Parameters </legend>

	<% definition.parameters.each { %>
		${ it.label }: ${ ui.format(data.context.parameterValues[it.name]) } <br/>
	<% } %>
</fieldset>

<br/>

<% data.dataSets.each {
	def ds = it.value
	def dsd = definition.dataSetDefinitions[it.key].parameterizable
%>

	<% if (dsd.class.name == 'org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition') { %>
		
		<table>
			<% dsd.columns.each { %>
				<tr>
					<th>${ it.name }</th>
					<th>${ it.label }</th>
					<td>${ ds.getData(it) }</td>
				</tr>
			<% } %>
		</table>
		
	<% } else {
		def cols = ds.metaData.columns
	%>
	
		<fieldset>
			<legend>${ dsd.name }</legend>
		
			<table cellspacing="0" cellpadding="2" border="1">
				<thead>
					<tr>
						<% cols.each { %>
							<th>${ it.label }</th>
						<% } %>
					</tr>
				</thead>
				<tbody>
					<% ds.rows.each { row -> %>
						<tr>
							<% cols.each { %>
								<td>${ row.getColumnValue(it) }</td>
							<% } %>
						</tr>
					<% } %>
				</tbody>
			</table>
		</fieldset>
	<% } %>
<% } %>