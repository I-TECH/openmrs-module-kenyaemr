<%
	ui.decorateWith("kenyaui", "panel", [ heading: definition.name ])

	def formatData = { result -> (result != null) ? result : "-" }
%>
<div class="ke-panel-content">
	<% if (definition.description) { %>
	<fieldset>
		<legend>Description</legend>
		${ definition.description }
	</fieldset>
	<% } %>

	<fieldset>
		<legend>Parameters</legend>
		<% definition.parameters.each { %>
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: it.label, value: reportData.context.parameterValues[it.name] ]) }
		<% } %>
	</fieldset>

	<%
	reportData.dataSets.each {
		def ds = it.value
		def dsd = definition.dataSetDefinitions[it.key].parameterizable
	%>

	<fieldset>
		<legend>${ dsd.name }: ${ dsd.description }</legend>
		<table class="ke-table-vertical">
			<tbody>
			<% ds.metaData.columns.each { col -> %>
				<tr>
					<td><strong>${ col.name }</strong>&nbsp;&nbsp;${ col.label }</td>
					<td style="text-align: center">${ formatData(ds.getData(col)) }</td>
				</tr>
			<% } %>
			</tbody>
		</table>
	</fieldset>

	<% } %>
</div>