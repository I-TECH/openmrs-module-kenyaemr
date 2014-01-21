<table style="width: 100%">
	<tr>
		<td style="width: 32px; vertical-align: top; padding-right: 5px">
			<img src="${ ui.resourceLink("kenyaui", "images/buttons/facility.png") }" />
		</td>
		<td style="text-align: left; vertical-align: top; width: 33%">
			<strong>{{ facility.name }}</strong><br/>
			{{ facility.code }} <small>{{ facility.description }}</small>
		</td>
		<td style="text-align: center; vertical-align: top; width: 33%">
			<div ng-if="facility.landline">
				<span class="ke-identifier-type">Landline</span> <span class="ke-identifier-value">{{ facility.landline }}</span>
			</div>
			<div ng-if="facility.mobile">
				<span class="ke-identifier-type">Mobile</span> <span class="ke-identifier-value">{{ facility.mobile }}</span>
			</div>
			<div ng-if="facility.fax">
				<span class="ke-identifier-type">Fax</span> <span class="ke-identifier-value">{{ facility.fax }}</span>
			</div>
		</td>
		<td style="text-align: right; vertical-align: top; width: 33%">
			{{ facility.division }}, {{ facility.district }}<br/>
			{{ facility.county }}, {{ facility.province }}
		</td>
	</tr>
</table>