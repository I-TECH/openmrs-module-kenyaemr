<table style="width: 100%">
	<tr>
		<td style="width: 32px; vertical-align: top; padding-right: 5px">
			<img ng-src="${ ui.resourceLink("kenyaui", "images/buttons/person_") }{{ person.gender }}.png" />
		</td>
		<td style="text-align: left; vertical-align: top">
			<strong>{{ person.name }}</strong><br/>
			{{ person.birthDate }}
		</td>
	</tr>
</table>