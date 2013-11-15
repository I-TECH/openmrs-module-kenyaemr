<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Database Summary" ])

	ui.includeJavascript("kenyaemr", "controllers/system.js")
%>
<div ng-controller="SystemController" ng-init="init('${ currentApp.id }')">
	<table class="ke-table-vertical">
		<tbody>
			<tr ng-repeat="infopoint in databaseSummary">
				<td>{{ infopoint.label }}</td>
				<td>{{ infopoint.value }}</td>
			</tr>
		</tbody>
	</table>
</div>