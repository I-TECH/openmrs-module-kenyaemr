<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Patient Validation", frameOnly: true ])

	ui.includeJavascript("kenyaemr", "controllers/developer.js")
%>
<div ng-controller="PatientValidation">
	<div class="ke-panel-content">
		<table class="ke-table-vertical">
			<thead>
				<tr>
					<th>Patient</th>
					<th>Problems</th>
				</tr>
			</thead>
			<tbody>
				<tr ng-repeat="result in results">
					<td>
						<img ng-src="${ ui.resourceLink("kenyaui", "images/glyphs/patient_") }{{ result.patient.gender }}.png" class="ke-glyph" />
						<a ng-href="${ ui.pageLink("kenyaemr", "chart/chartViewPatient") }?patientId={{ result.patient.id }}">{{ result.patient.name }}</a>
					</td>
					<td>
						<div ng-repeat="error in result.errors">{{ error }}</div>
					</td>
				</tr>
			</tbody>
		</table>
		<div ng-if="loading" style="text-align: center; padding-top: 5px">
			<img src="${ ui.resourceLink("kenyaui", "images/loading.gif") }" />
		</div>
	</div>

	<div class="ke-panel-controls">
		<button id="patient-validation-run" ng-click="run()" ng-disabled="loading"><img src="${ ui.resourceLink("images/glyphs/start.png") }" /> Run</button>
	</div>
</div>