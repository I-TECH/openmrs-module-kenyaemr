<div class="ke-panel-frame">
	<div class="ke-panel-heading">Queue</div>
	<div class="ke-panel-content">
		<table class="ke-table-vertical">
			<thead>
			<tr>
				<th ng-if="!reportUuid">Report</th>
				<th>Requested</th>
				<th>By</th>
				<th>Status</th>
				<th>Time taken</th>
				<th>&nbsp;</th>
			</tr>
			</thead>
			<tbody>
			<tr ng-repeat="request in queued">
				<td ng-if="!reportUuid">{{ request.report.name }}</td>
				<td>{{ request.requestDate | keDateTime }}</td>
				<td>{{ request.requestedBy.person.name }}</td>
				<td>{{ request.status }}</td>
				<td>{{ request.timeTaken || '--:--:--' }}</td>
				<td style="text-align: right">
					<% if (config.allowCancel) { %>
					<a href="#" ng-click="cancelRequest(request.id)" ng-if="request.status == 'REQUESTED' || request.status == 'PROCESSING'">
						<img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" class="ke-glyph" /> Cancel
					</a>
					<% } %>
				</td>
			</tr>
			<tr ng-if="queued.length == 0">
				<td colspan="{{ reportUuid ? 5 : 6 }}" style="text-align: center"><i>None</i></td>
			</tr>
			</tbody>
		</table>
	</div>
</div>