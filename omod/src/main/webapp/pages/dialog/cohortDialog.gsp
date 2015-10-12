<%
    ui.includeJavascript("kenyaemr", "controllers/report.js")
%>
<script type="text/javascript">

</script>
<div class="ke-panel-content">
	<% if (cohort.size() > 0) {%>
		<div class="ke-form-header">
			Viewing ${cohort.size()} Patients ${column}
		</div>
	<% } %>
	<table class="ke-table-vertical">
		<thead>
			<tr>
				<th>Name</th>
				<th>Age</th>
				<th>Sex</th>
				<th>Unique Patient Number</th>
			</tr>
		</thead>
		<tbody>
			<% patients.each { patient -> %>
			<tr>
				<td>
					<img src="${ ui.resourceLink("kenyaui", "images/glyphs/patient_" + patient.gender.toLowerCase() + ".png") }" class="ke-glyph" />
					<a href="${ ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patient.id ]) }">${ patient.name }</a>
				</td>
				<td>${ patient.age }</td>
				<td>${ patient.gender }</td>
				<td>${ patient.identifiers[0].identifier }</td>
			</tr>
			<% } %>
		</tbody>
	</table>
</div>
<div class="ke-panel-footer">
	<button type="button" onclick="kenyaui.closeDialog()"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/close.png") }" /> Close</button>
</div>
