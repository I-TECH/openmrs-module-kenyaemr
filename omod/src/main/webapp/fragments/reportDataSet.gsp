<%
	config.require("dataSet")

	def formatData = { result -> (result != null) ? result : "-" }
%>
<% if (config.dataSet.class.name == 'org.openmrs.module.reporting.dataset.MapDataSet') { %>
		
	<table class="ke-table-decorated ke-table-vertical">
		<thead>
			<tr>
				<th style="text-align: left">Indicator</th>
				<th style="text-align: left">Description</th>
				<th style="text-align: center">Value</th>
			</tr>
		</thead>
		<tbody>
		<% config.dataSet.metaData.columns.each { col -> %>
			<tr>
				<th>${ col.name }</th>
				<td style="text-align: left">${ col.label }</td>
				<td>${ formatData(config.dataSet.getData(col)) }</td>
			</tr>
		<% } %>
		</tbody>
	</table>
		
<% } else {
	def cols = config.dataSet.metaData.columns
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
		<% config.dataSet.rows.each { row -> %>
			<tr>
			<% cols.each { %>
				<td style="text-align: left">${ formatData(row.getColumnValue(it)) }</td>
			<% } %>
			</tr>
		<% } %>
		</tbody>
	</table>

<% } %>