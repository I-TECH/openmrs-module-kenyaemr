<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaui", "angular.js")

	def menuItems =  [
			[ iconProvider: "kenyaui", icon: "buttons/report_generate.png", label: "Request Report", href: "javascript:requestReport()" ],
			[ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]
	]
%>
<script type="text/javascript">
	var requestDialogContent = null;

	jq(function(){
		requestDialogContent = jq('#request-dialog-content').html();
		jq('#request-dialog-content').remove();

		//ui.getFragmentActionAsJson('kenyaemr', 'report/reportUtils', 'getRequests', { reportUuid: '${ definition.uuid }' }, function(data) {
		//	console.log(data);
		//});
	});

	function requestReport() {
		kenyaui.openPanelDialog({ heading: 'Request report', content: requestDialogContent });

		jq('#request-report-ok').click(function() {
			var params = { appId: '${ currentApp.id }', reportUuid: '${ report.targetUuid }' };

			<% if (isIndicator) { %>
			params.date = jq('#request-date').val();
			<% } %>

			ui.getFragmentActionAsJson('kenyaemr', 'report/reportUtils', 'requestReport', params, function() {
				ui.reloadPage();
			});
		});
		jq('#request-report-cancel').click(function() {
			kenyaui.closeDialog();
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

<div class="ke-page-content">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Summary</div>
		<div class="ke-panel-content">
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Name", value: definition.name ]) }
			<% if (definition.description) { %>
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Description", value: definition.description ]) }
			<% } %>
		</div>
	</div>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Requests</div>
		<div class="ke-panel-content">
			<% if (requests) { %>
			<table class="ke-table-vertical">
				<thead>
					<tr>
						<th>Requested</th>
						<th>By</th>
						<th>Status</th>
						<th>Time taken</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<% requests.each { request -> %>
					<tr>
						<td>${ request.requestDate }</td>
						<td>${ request.requestedBy.name }</td>
						<td>${ request.status }</td>
						<td>${ request.timeTaken ?: "--:--:--" }</td>
						<td style="text-align: right">
						<% if (request.finished) { %>
							${ ui.includeFragment("kenyaui", "widget/buttonlet", [
									label: "View",
									type: "view",
									href: ui.pageLink("kenyaemr", "reportView", [ appId: currentApp.id, request: request.id, returnUrl: ui.thisUrl() ])
							]) }
							${ ui.includeFragment("kenyaui", "widget/buttonlet", [
									label: "CSV",
									type: "csv",
									href: ui.pageLink("kenyaemr", "reportExport", [ appId: currentApp.id, request: request.id, type: "csv", returnUrl: ui.thisUrl() ])
							]) }
							<% if (excelRenderable) { %>
								${ ui.includeFragment("kenyaui", "widget/buttonlet", [
										label: "Excel",
										type: "excel",
										href: ui.pageLink("kenyaemr", "reportExport", [ appId: currentApp.id, request: request.id, type: "excel", returnUrl: ui.thisUrl() ])
								]) }
							<% } %>
						<% } %>
						</td>
					</tr>
				<% } %>
				</tbody>
			</table>
			<% } else { %>
				<i>None</i>
			<% } %>
		</div>
	</div>
</div>

<div id="request-dialog-content" style="display: none">
	<div class="ke-panel-content">
		<div>Report: <strong>${ ui.format(definition.name) }</strong></div>
		<% if (isIndicator) { %>
		<div>Period: ${ ui.includeFragment("kenyaui", "widget/selectList", [
				id: "request-date",
				formFieldName: "date",
				selected: startDateSelected,
				options: startDateOptions,
				optionsValueField: "key",
				optionsDisplayField: "value"
		]) }
		</div>
		<% } %>
	</div>
	<div class="ke-panel-footer">
		<div class="ke-button" id="request-report-ok"><div class="ke-button-text"><div class="ke-label">OK</div></div></div>
		<div class="ke-button" id="request-report-cancel"><div class="ke-button-text"><div class="ke-label">Cancel</div></div></div>
	</div>
</div>