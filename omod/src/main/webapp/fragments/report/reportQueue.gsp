<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Report Queue" ])

	ui.includeJavascript("kenyaui", "angular.js")
	ui.includeJavascript("kenyaemr", "controllers/report.js")
%>
<script type="text/javascript">

</script>
<table class="ke-table-vertical">
	<thead>
	<tr>
		<th>Report</th>
		<th>Requested</th>
		<th>By</th>
		<th>Status</th>
		<th>Time taken</th>
	</tr>
	</thead>
	<tbody>
	<tr ng-repeat="request in queue">
		<td>{{ request.report.name }}</td>
		<td>{{ request.requestDate }}</td>
		<td>{{ request.requestedBy.person.name }}</td>
		<td>{{ request.status }}</td>
		<td>{{ request.timeTaken }}</td>
	</tr>
	<tr ng-if="queue.length == 0">
		<td colspan="5" style="text-align: center"><i>None</i></td>
	</tr>
	</tbody>
</table>