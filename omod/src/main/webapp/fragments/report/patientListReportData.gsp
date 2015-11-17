<%
	ui.decorateWith("kenyaui", "panel", [ heading: definition.name ])

	def columns = dataSet.metaData.columns
	def nonIdOrNameColumns = columns.findAll { it.label != "id" && it.label != "Name" }

	def formatData = { result -> (result != null) ? result : "-" }
%>

<fieldset>
	<legend>Summary</legend>
	<table>
		<tr>
			<td style="padding-right: 20px">${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Total", value: summary["total"] ]) }</td>
			<td style="padding-right: 20px">${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Males", value: summary["males"] ]) }</td>
			<td>${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Females", value: summary["females"] ]) }</td>
		</tr>
	</table>
</fieldset>

<% if (nonIdOrNameColumns.size() > 0) { %>
<div style="overflow: auto">
	<table class="ke-table-vertical">
		<thead>
		<tr>
			<th>Name</th>
			<% nonIdOrNameColumns.each { col -> %>
			<th>${ col.label }</th>
			<% } %>
		</tr>
		</thead>
		<tbody>
		<%
			dataSet.rows.each { row ->
				def patientId = row.getColumnValue("id")
				def personName = row.getColumnValue("Name")
				def personGender = row.getColumnValue("Sex").toLowerCase()
		%>
			<tr>
				<td>
					<img src="${ ui.resourceLink("kenyaui", "images/glyphs/patient_" + personGender + ".png") }" class="ke-glyph" />
					<a href="${ ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patientId ]) }">${ personName }</a>
				</td>

			<% nonIdOrNameColumns.each { col -> %>
				<td>${ formatData(row.getColumnValue(col)) }</td>
			<% } %>
			</tr>
		<% } %>
		</tbody>
	</table>
</div>
<% } else if(isCohortReport) {%>
<fieldset>
	Insufficient follow-up time to generate this report
</fieldset>
<% } else {%>
<fieldset>
	None
</fieldset>
<% } %>

