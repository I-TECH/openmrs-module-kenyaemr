<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/report.js")

	def menuItems =  [
			[ iconProvider: "kenyaui", icon: "buttons/report_generate.png", label: "Request report", onClick: "requestReport()" ],
			[ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: returnUrl ]
	]
%>
<script type="text/javascript">
	function requestReport() {
		kenyaui.openPanelDialog({ templateId: 'request-dialog-template' });
	}

	function onReportRequest(params) {
		kenyaui.updateController('ng-reportctrl', function (scope) {
			scope.requestReport(params);
		});
	}
</script>

<div class="ke-page-sidebar">

	<div class="ke-panel-frame" id="end-of-day">
		<div class="ke-panel-heading">Tasks</div>
		<% menuItems.each { item -> %>
			${ ui.includeFragment("kenyaui", "widget/panelMenuItem", item) }
		<% } %>
	</div>
</div>

<div class="ke-page-content" id="ng-reportctrl" ng-controller="ReportController" ng-init="init('${ currentApp.id }', '${ definition.uuid }')">

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Summary</div>
		<div class="ke-panel-content">
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Name", value: definition.name ]) }
			<% if (definition.description) { %>
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Description", value: definition.description ]) }
			<% } %>
		</div>
	</div>

	${ ui.includeFragment("kenyaemr", "report/reportQueue", [ allowCancel: false ]) }

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Finished</div>
		<div class="ke-panel-content">
			<table class="ke-table-vertical">
				<thead>
				<tr>
					<th>Requested</th>
					<th>By</th>
					<th>Status</th>
					<th>Time taken</th>
					<th>&nbsp;</th>
				</tr>
				</thead>
				<tbody>
				<tr ng-repeat="request in finished">
					<td>{{ request.requestDate | keDateTime }}</td>
					<td>{{ request.requestedBy.person.name }}</td>
					<td>{{ request.status }}</td>
					<td>{{ request.timeTaken || '--:--:--' }}</td>
					<td style="text-align: right">
						<div ng-if="request.status == 'COMPLETED'">
							<a href="#" ng-click="viewReportData(request.id)">
								<img src="${ ui.resourceLink("kenyaui", "images/glyphs/view.png") }" class="ke-glyph" /> View
							</a>
							&nbsp;&nbsp;
							<a href="#" ng-click="exportReportData(request.id, 'csv')">
								<img src="${ ui.resourceLink("kenyaui", "images/glyphs/csv.png") }" class="ke-glyph" /> CSV
							</a>
							&nbsp;&nbsp;
							<% if (excelRenderable) { %>
							<a href="#" ng-click="exportReportData(request.id , 'excel')">
								<img src="${ ui.resourceLink("kenyaui", "images/glyphs/excel.png") }" class="ke-glyph" /> Excel
							</a>
							<% } %>
						</div>
						<div ng-if="request.status == 'FAILED'">
							<a href="#" ng-click="viewReportError(request.id)">
								<img src="${ ui.resourceLink("kenyaui", "images/glyphs/monitor.png") }" class="ke-glyph" /> Error
							</a>
						</div>
					</td>
				</tr>
				<tr ng-if="finished.length == 0">
					<td colspan="5" style="text-align: center"><i>None</i></td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>

</div>

<div id="request-dialog-template" title="Request Report" style="display: none">
	${ ui.includeFragment("kenyaemr", "report/reportRequestForm", [ definition: definition, onRequestCallback: "onReportRequest" ]) }
</div>