<table style="width: 100%">
	<tr>
		<td style="width: 32px; vertical-align: top; padding-right: 5px">
			<img width="32" height="32" ng-src="${ ui.resourceLink("kenyaui", "images/buttons/patient_") }{{ patient.gender }}.png" />
		</td>
		<td style="text-align: left; vertical-align: top; width: 33%">
			<strong>{{ patient.name }}</strong><br/>
			{{ patient.age }} <small>(DOB {{ patient.birthdate }})</small>
		</td>
		<td style="text-align: center; vertical-align: top; width: 33%">
			<div ng-repeat="identifier in patient.identifiers">
				<span class="ke-identifier-type">{{ identifier.identifierType }}</span>
				<span class="ke-identifier-value">{{ identifier.identifier }}</span>
			</div>
		</td>
		<td style="text-align: right; vertical-align: top; width: 33%">
			<div class="ke-visittag" ng-if="patient.activeVisit">
				<strong>{{ patient.activeVisit.visitType }}</strong><br />
				<small>since <strong>{{ patient.activeVisit.startDatetime | keDateAuto }}</strong></small>
			</div>
		</td>
	</tr>
</table>