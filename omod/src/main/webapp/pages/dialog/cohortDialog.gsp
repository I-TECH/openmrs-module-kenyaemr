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
				<th>Age at reporting(Years)</th>
				<th>Sex</th>
				<th>Unique Patient Number</th>
                <th>HIV Enrollment Date</th>
                <th>ART Start Date</th>
				<th>Last VL(copies/ml)</th>
				<th>Last VL Date</th>
			</tr>
		</thead>
		<tbody>
            <%
                def rq = reportRequest.id;
                def ds = dataSet;
                def col = column.name;
				java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
            %>
			<% patients.each { patient -> %>
			<tr>
				<td>
					<img src="${ ui.resourceLink("kenyaui", "images/glyphs/patient_" + patient.gender.toLowerCase() + ".png") }" class="ke-glyph" />
					<a href="${ ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patient.id ]) }">${ patient.name }</a>
				</td>
				<td>${ patient.age }</td>
				<td>${ ageAtReportingResults.getData().get(patient.id) }</td>
				<td>${ patient.gender.toUpperCase() }</td>
				<td>${ patient.identifiers[0].identifier }</td>
			    <td>${ enrollmentDates.get(patient.id) != null? (enrollmentDates.get(patient.id).value != null ?
						dateFormat.format(enrollmentDates.get(patient.id).value) : "") : ""  }</td>
                <td>${ artInitializationDates.get(patient.id) != null ?
						dateFormat.format(artInitializationDates.get(patient.id).value) : "" }</td>
				<td>${ lastVlResults.get(patient.id) != null && lastVlResults.get(patient.id).value != null ?
					lastVlResults.get(patient.id).value.lastVl : "" }</td>
				<td>${ lastVlResults.get(patient.id) != null && lastVlResults.get(patient.id).value != null ?
						dateFormat.format(lastVlResults.get(patient.id).value.lastVlDate) : "" }
			</tr>
			<% } %>
		</tbody>
	</table>
</div>
<div class="ke-panel-footer">
	<button type="button" onclick="kenyaui.closeDialog()"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/close.png") }" /> Close</button>
	<button type="button" onclick="downloadCohort('${ rq }', '${ ds }', '${ col }')"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/csv.png") }" /> Download</button>
</div>
